/**
 * Memformat skor numerik sentimen menjadi string persentase.
 * Contoh: 85.5 -> 85.5%, null -> -
 */
export function formatSentimentScore(score) {
  if (score === null || score === undefined || score === '') {
    return '-'
  }

  const num = Number(score)
  if (isNaN(num)) {
    return '-'
  }

  return ${num.toFixed(1)}%
}