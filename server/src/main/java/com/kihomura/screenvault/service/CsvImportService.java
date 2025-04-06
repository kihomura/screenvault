package com.kihomura.screenvault.service;

import com.kihomura.screenvault.pojo.Content;
import com.kihomura.screenvault.enums.Category;
import com.kihomura.screenvault.enums.Genre;
import com.kihomura.screenvault.enums.SourceType;
import com.kihomura.screenvault.mapper.ContentMapper;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CsvImportService {

    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);
    private static final int BATCH_SIZE = 5000;
    private static final String CSV_DIR = "metadata";

    @Autowired
    private ContentMapper contentMapper; // 使用MyBatis Plus的Mapper替换JPA的Repository
    // to avoid interruptions from unexpected errors,
    // records the last imported line, so it can resume from there next time
    private AtomicInteger lastSuccessfulLine = new AtomicInteger(0);

    /**
     * import all .csv files
     */
    public void importAllData() {
        try {
            importCsvFile("tv_shows.csv", Category.TV_SHOW);
            importCsvFile("movies.csv", Category.MOVIE);
        } catch (Exception e) {
            logger.error("Error in the import process: ", e);
        }
    }

    /**
     * Imports a single CSV file from a specific line (last interrupted line)
     * @param fileName The name of the CSV file (movies or tv_shows)
     * @param startLine The starting line number (0 for the header, 1 for the first data row)
     */
    public void resumeImportFromLine(String fileName, int startLine) {
        try {
            Category category = fileName.contains("movie") ? Category.MOVIE : Category.TV_SHOW;
            importCsvFileFromLine(fileName, category, startLine);
        } catch (Exception e) {
            logger.error("Error in the import recovery process: ", e);
        }
    }

    /**
     * Imports a single CSV file
     */
    @Transactional
    public void importCsvFile(String fileName, Category defaultCategory) {
        importCsvFileFromLine(fileName, defaultCategory, 1); // skip the header
    }

    /**
     * Imports a single CSV file starting from a specified line
     */
    @Transactional
    public void importCsvFileFromLine(String fileName, Category defaultCategory, int startLine) {
        Path filePath = Paths.get(CSV_DIR, fileName);
        AtomicInteger totalProcessed = new AtomicInteger(0);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger currentLine = new AtomicInteger(0);

        logger.info("Starting to import the file: {}, from line {}", filePath, startLine);

        try {
            var csvParser = new CSVParserBuilder()
                    .withSeparator(',')
                    .withQuoteChar('"')
                    .withEscapeChar('\\')
                    .withStrictQuotes(false)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreQuotations(false)
                    .withErrorLocale(java.util.Locale.getDefault())
                    .build();

            try (BufferedReader fileReader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8));
                 CSVReader reader = new CSVReaderBuilder(fileReader)
                         .withSkipLines(startLine)
                         .withCSVParser(csvParser)
                         .build())
            {
                currentLine.set(startLine);
                String[] line;
                List<Content> batch = new ArrayList<>(BATCH_SIZE);

                while ((line = reader.readNext()) != null) {
                    currentLine.incrementAndGet();
                    try {
                        // Check if the row contains enough fields
                        if (line.length < 10) {
                            logger.warn("Line {}: Insufficient columns ({}/10)", currentLine.get(), line.length);
                            failCount.incrementAndGet();
                            continue;
                        }

                        Content content = parseContent(line, defaultCategory);
                        if (content != null) {
                            batch.add(content);
                            successCount.incrementAndGet();
                            lastSuccessfulLine.set(currentLine.get());
                        } else {
                            failCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                        logger.warn("Line {}: Parsing failed, reason: {}", currentLine.get(), e.getMessage());
                    }

                    // Batch save data
                    if (batch.size() >= BATCH_SIZE) {
                        try {
                            for (Content content : batch) {
                                contentMapper.insert(content);
                            }
                            logger.info("{} records imported", totalProcessed.addAndGet(batch.size()));
                            batch.clear();
                        } catch (Exception e) {
                            logger.error("Failed to batch save data: ", e);
                            // Record the current position to resume later
                            logger.info("Last successfully processed line: {}", lastSuccessfulLine.get());
                            // Retry saving one by one
                            saveOneByOne(batch);
                            batch.clear();
                        }
                    }
                }

                // Process the remaining data that doesn't make up a full batch
                if (!batch.isEmpty()) {
                    try {
                        for (Content content : batch) {
                            contentMapper.insert(content);
                        }
                        totalProcessed.addAndGet(batch.size());
                    } catch (Exception e) {
                        logger.error("Last batch save failed: ", e);
                        saveOneByOne(batch);
                    }
                }

                logger.info("-----File {} import completed, successfully: {} records, failed: {} records-----",
                        fileName, successCount.get(), failCount.get());

            }
        } catch (IOException | CsvValidationException e) {
            logger.error("Error importing file: {}", e.getMessage(), e);
            logger.info("Last successfully processed line number: {}", lastSuccessfulLine.get());
        }
    }

    /**
     * Save content one by one, used as a fallback strategy when batch saving fails
     */
    private void saveOneByOne(List<Content> batch) {
        int saved = 0;
        for (Content content : batch) {
            try {
                contentMapper.insert(content);
                saved++;
            } catch (Exception e) {
                logger.warn("Failed to save a single record: {}", e.getMessage());
            }
        }
        logger.info("Successfully saved {}/{} records in single-save mode", saved, batch.size());
    }

    /**
     * Fix the quotation marks issue in the CSV file
     */
    public String fixCsvQuotes(String fileName) {
        Path inputPath = Paths.get(CSV_DIR, fileName);
        Path outputPath = Paths.get(CSV_DIR, fileName + ".fixed");

        try (BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
             BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)) {

            String line;
            boolean inQuotes = false;
            StringBuilder currentLine = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                // Count the number of quotation marks in the current line
                int quoteCount = 0;
                for (char c : line.toCharArray()) {
                    if (c == '"') quoteCount++;
                }

                // Start a new line if not inside quotation marks
                if (!inQuotes) {
                    currentLine = new StringBuilder(line);
                } else {
                    currentLine.append("\n").append(line);
                }

                if (quoteCount % 2 == 1) {
                    inQuotes = !inQuotes;
                }

                if (!inQuotes) {
                    writer.write(currentLine.toString());
                    writer.newLine();
                }
            }

            // Force end and write if still inside quotation marks at the end
            if (inQuotes) {
                currentLine.append("\"");
                writer.write(currentLine.toString());
                writer.newLine();
            }

            logger.info("CSV file repair completed, saved to: {}", outputPath);
            return outputPath.toString();

        } catch (IOException e) {
            logger.error("CSV file repair failed: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Get the line number of the last successful import
     */
    public int getLastSuccessfulLine() {
        return lastSuccessfulLine.get();
    }

    /**
     * Parse CSV row data into a Content object
     */
    private Content parseContent(String[] line, Category defaultCategory) {

//        if (line.length < 10) {
//            logger.warn("Incorrect data row format, insufficient columns: {}", String.join(",", line));
//            return null;
//        }

        Content content = new Content();

        // title
        if (line[0] != null && !line[0].isEmpty()) {
            content.setTitle(line[0]);
        } else {
            logger.warn("Title cannot be empty, skipping row {}", line[0]);
            return null;
        }

        // other_title
        if (line[1] != null && !line[1].isEmpty()) {
            content.setOtherTitle(line[1]);
        }

        // country
        if (line[2] != null && !line[2].isEmpty()) {
            content.setCountry(line[2]);
        }

        // language
        if (line[3] != null && !line[3].isEmpty()) {
            content.setLanguage(line[3]);
        }

        // description
        if (line[4] != null && !line[4].isEmpty()) {
            content.setDescription(line[4]);
        }

        // image
        if (line[5] != null && !line[5].isEmpty()) {
            content.setImage(line[5]);
        }

        // release_date
        if (line[6] != null && !line[6].isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                content.setReleaseDate(LocalDate.parse(line[6], formatter));
            } catch (DateTimeParseException e) {
                logger.warn("Incorrect date format: {}", line[6]);
            }
        }

        // genre
        if (line[7] != null && !line[7].isEmpty()) {
            try {
                content.setGenre(Genre.valueOf(line[7]));
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown genre: {}", line[7]);
            }
        }

        // category
        if (line[8] != null && !line[8].isEmpty()) {
            try {
                content.setCategory(Category.valueOf(line[8]));
            } catch (IllegalArgumentException e) {
                logger.warn("Unknown category: {}", line[8]);
                content.setCategory(defaultCategory);
            }
        } else {
            content.setCategory(defaultCategory);
        }

        // source_type
        if (line[9] != null && !line[9].isEmpty()) {
            try {
                content.setSourceType(SourceType.valueOf(line[9]));
            } catch (IllegalArgumentException e) {
                logger.warn("unknown source-type: {}", line[9]);
                content.setSourceType(SourceType.OFFICIAL_DATA);
            }
        } else {
            content.setSourceType(SourceType.OFFICIAL_DATA);
        }

        content.setCreatorId(null);

        return content;
    }
}