/**
 * Returns the appropriate image path based on content source type
 * @param {Object} content - The content object containing image and source_type properties
 * @param {string} officialPrefix - The prefix to use for official data sources (e.g. TMDB)
 * @return {string} The complete image URL or fallback image path
 */
export function getContentImagePath(content) {

    const fallbackImage = '/assets/default-content-poster.png';
    const prefix = 'https://image.tmdb.org/t/p/w1280';

    if (!content || !content.image) {
        return fallbackImage;
    }

    // If source_type is OFFICIAL_DATA, prepend the official prefix
    // Otherwise, just return the image URL as is
    return content.sourceType === 'OFFICIAL_DATA'
        ? prefix + content.image
        : content.image;
}