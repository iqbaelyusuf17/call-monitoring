/**
 * Memformat timestamp ISO (misal: 2026-09-15T10:00:00Z) menjadi format lokal/WIB yang mudah dibaca.
 * Contoh output: 15 Sep 2026, 17:00
 */
export function formatCallTimestamp(isoString) {
  if (!isoString) return '-'

  try {
    const date = new Date(isoString)
    if (isNaN(date.getTime())) return '-'

    return new Intl.DateTimeFormat('id-ID', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
    }).format(date)
  } catch {
    return '-'
  }
}

/**
 * Mendapatkan tanggal batas minimal 3 bulan yang lalu dalam format YYYY-MM-DD.
 */
export function getThreeMonthsAgoDateString() {
  const d = new Date()
  d.setMonth(d.getMonth() - 3)
  return d.toISOString().split('T')[0]
}

/**
 * Mendapatkan tanggal hari ini dalam format YYYY-MM-DD.
 */
export function getTodayDateString() {
  return new Date().toISOString().split('T')[0]
}