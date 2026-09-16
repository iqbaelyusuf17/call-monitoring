/**
 * Format string tanggal ISO-8601 ke format ramah Indonesia (WIB)
 * Contoh: 2026-08-15T14:30:00Z -> 15 Agu 2026, 21:30 WIB
 */
export function formatCallTimestamp(isoString) {
  if (!isoString) return '-'
  try {
    const date = new Date(isoString)
    if (isNaN(date.getTime())) return isoString

    return new Intl.DateTimeFormat('id-ID', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      hour12: false,
    }).format(date) + ' WIB'
  } catch {
    return isoString
  }
}

/**
 * Mendapatkan tanggal batas minimal (3 bulan lalu dari hari ini).
 * Digunakan untuk membatasi pilihan datepicker (min attribute) sesuai AC-6.
 */
export function getThreeMonthsAgoDateString() {
  const date = new Date()
  date.setMonth(date.getMonth() - 3)
  return date.toISOString().split('T')[0]
}

/**
 * Mendapatkan tanggal hari ini dalam format YYYY-MM-DD (max attribute).
 */
export function getTodayDateString() {
  return new Date().toISOString().split('T')[0]
}
