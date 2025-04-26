/**
 * Calculate star class based on current rating and star index
 * @param {Number} starIndex - Current star position (1-10)
 * @param {Number} rating - Current rating value
 * @param {Number|null} hoverRating - Current hover rating (if any)
 * @returns {String} - CSS class for the star (empty, half, or full)
 */
export const getStarClass = (starIndex, rating, hoverRating) => {
    const activeRating = hoverRating !== null ? hoverRating : rating;
    if (!activeRating) return 'empty';
    if (starIndex <= Math.floor(activeRating)) {
        return 'full';
    } else if (starIndex === Math.ceil(activeRating) && activeRating % 1 !== 0) {
        return 'half';
    } else {
        return 'empty';
    }
};

/**
 * Calculate rating based on mouse position within a star element
 * @param {Event} event - Mouse event
 * @param {Number} starIndex - Current star position (1-10)
 * @returns {Number} - Calculated rating (can be half or whole)
 */
export const calculateRatingFromMousePosition = (event, starIndex) => {
    const rect = event.currentTarget.getBoundingClientRect();
    const mouseX = event.clientX - rect.left;
    const halfPoint = rect.width / 2;

    if (mouseX < halfPoint) {
        return starIndex - 0.5;
    } else {
        return starIndex;
    }
};

/**
 * Validate and normalize rating value to ensure it's between 0-10 and only allows .0 or .5 values
 * @param {Number} rating - The rating value to validate
 * @returns {Number} - Normalized rating value
 */
export const validateRating = (rating) => {
    if (rating === null || rating === undefined) return null;

    let normalizedRating = Math.round(rating * 2) / 2;

    if (normalizedRating > 10) {
        normalizedRating = 10;
    } else if (normalizedRating < 0) {
        normalizedRating = 0;
    }

    return normalizedRating;
};