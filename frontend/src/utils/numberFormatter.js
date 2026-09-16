/**
 * Format skor sentimen nasabah ke bentuk persentase rapi.
 * Contoh: 85.50 -> 85.5%
 */
export function formatSentimentScore(score) {
  if (score === null || score === undefined) return '-'
  const num = Number(score)
  if (isNaN(num)) return '-'
  return `${num.toFixed(1)}%`
}
