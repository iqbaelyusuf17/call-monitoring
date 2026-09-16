import { ref, reactive, readonly } from 'vue'
import { getCallMonitorings } from '../api/callMonitoringApi'

/**
 * Composable untuk mengelola state dan logika data Call Monitoring.
 * Bertindak seperti Service Layer di backend: mengorkestrasikan filter, sorting, pagination, dan fetch data.
 */
export function useCallMonitoring() {
  const records = ref([])
  const meta = ref({
    page: 1,
    limit: 5,
    total_records: 0,
    total_pages: 0,
    has_previous: false,
    has_next: false,
  })

  const isLoading = ref(false)
  const errorMessage = ref(null)

  const filters = reactive({
    search: '',
    startDate: '',
    endDate: '',
    sentiment: 'ALL',
  })

  const sorting = reactive({
    sortBy: 'call_timestamp',
    sortOrder: 'desc',
  })

  /**
   * Mengambil data dari backend dengan parameter filter, sorting, dan paginasi yang aktif.
   */
  async function fetchData() {
    isLoading.value = true
    errorMessage.value = null

    try {
      const response = await getCallMonitorings({
        page: meta.value.page,
        limit: meta.value.limit,
        search: filters.search.trim() || undefined,
        start_date: filters.startDate || undefined,
        end_date: filters.endDate || undefined,
        sentiment: (filters.sentiment && filters.sentiment !== 'ALL') ? filters.sentiment : undefined,
        sort_by: sorting.sortBy,
        sort_order: sorting.sortOrder,
      })

      records.value = response.data || []
      meta.value = response.meta || {
        page: 1,
        limit: 5,
        total_records: 0,
        total_pages: 0,
        has_previous: false,
        has_next: false,
      }
    } catch (err) {
      errorMessage.value = err.response?.data?.message || err.message || 'Gagal memuat data monitoring panggilan'
      records.value = []
    } finally {
      isLoading.value = false
    }
  }

  /**
   * Pindah ke halaman tertentu (AC-11: mempertahankan filter & sort yang aktif).
   */
  function setPage(newPage) {
    if (newPage < 1 || (meta.value.total_pages > 0 && newPage > meta.value.total_pages)) {
      return
    }
    meta.value.page = newPage
    fetchData()
  }

  /**
   * Toggle sortir pada kolom (AC-10: klik kolom yang sama bergantian asc <-> desc).
   */
  function toggleSort(column) {
    if (sorting.sortBy === column) {
      sorting.sortOrder = sorting.sortOrder === 'asc' ? 'desc' : 'asc'
    } else {
      sorting.sortBy = column
      sorting.sortOrder = 'asc'
    }
    // AC-11: Tetap di page saat ini atau fetch ulang dengan sorting baru
    fetchData()
  }

  /**
   * Menerapkan filter baru (AC-9: menerapkan seluruh filter aktif dan mereset ke page 1).
   */
  function applyFilters(newFilters) {
    Object.assign(filters, newFilters)
    meta.value.page = 1
    fetchData()
  }

  /**
   * Reset seluruh filter ke kondisi awal.
   */
  function resetFilters() {
    filters.search = ''
    filters.startDate = ''
    filters.endDate = ''
    filters.sentiment = 'ALL'
    meta.value.page = 1
    fetchData()
  }

  return {
    records: readonly(records),
    meta: readonly(meta),
    isLoading: readonly(isLoading),
    errorMessage: readonly(errorMessage),
    filters: readonly(filters),
    sorting: readonly(sorting),
    fetchData,
    setPage,
    toggleSort,
    applyFilters,
    resetFilters,
  }
}
