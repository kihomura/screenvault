package com.kihomura.screenvault.runner;

import com.kihomura.screenvault.enums.Category;
import com.kihomura.screenvault.service.impl.CsvImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Automatically imports CSV data when the application starts
 * Arguments:
 * - Full import: --spring.profiles.active=csv-import
 * - Resume from a specific line: --spring.profiles.active=csv-import --resume-file=movies.csv --start-line=458087
 * - Import the fixed file: --spring.profiles.active=csv-import --file=movies.csv.fixed
 */
@Component
@Profile("csv-import")
public class CsvImportRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CsvImportRunner.class);

    @Autowired
    private CsvImportService csvImportService;

    @Override
    public void run(String... args) {
        String resumeFile = null;
        int startLine = 1;
        String specificFile = null;
        boolean fixMode = false;

        for (String arg : args) {
            if (arg.startsWith("--resume-file=")) {
                resumeFile = arg.substring("--resume-file=".length());
            } else if (arg.startsWith("--start-line=")) {
                try {
                    startLine = Integer.parseInt(arg.substring("--start-line=".length()));
                } catch (NumberFormatException e) {
                    logger.warn("Invalid starting line number argument: {}", arg);
                }
            } else if (arg.startsWith("--file=")) {
                specificFile = arg.substring("--file=".length());
            } else if (arg.equals("--fix-mode")) {
                fixMode = true;
            }
        }

        // Select operation based on the arguments
        if (fixMode) {
            // fix mode
            logger.info("Start CSV file repair mode");
            csvImportService.fixCsvQuotes("movies.csv");
            csvImportService.fixCsvQuotes("tv_shows.csv");
        } else if (resumeFile != null) {
            // resume mode
            logger.info("Resume import from line {} of file {}", resumeFile, startLine);
            csvImportService.resumeImportFromLine(resumeFile, startLine);
        } else if (specificFile != null) {
            // import specific file
            logger.info("Import specific file: {}", specificFile);
            Category category = specificFile.contains("movie") ? Category.MOVIE : Category.TV_SHOW;
            csvImportService.importCsvFileFromLine(specificFile, category, 1);
        } else {
            // import full file
            logger.info("Start importing all CSV data");
            csvImportService.importAllData();
        }
    }
}