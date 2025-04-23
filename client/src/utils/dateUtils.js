export function formatYear(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.getFullYear();
}