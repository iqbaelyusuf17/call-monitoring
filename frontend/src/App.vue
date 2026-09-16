<template>
  <div class="min-h-screen bg-slate-50 flex flex-col font-sans">
    <!-- 1. Header / Navbar Monitoring Menu (AC-1) -->
    <Navbar />

    <!-- 2. Main Content Container -->
    <main class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 flex-1 w-full">
      <!-- Banner Pesan Error (jika ada) -->
      <div
        v-if="errorMessage"
        class="mb-6 p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-800 text-xs flex items-center justify-between"
      >
        <div class="flex items-center space-x-2">
          <span class="font-bold">Error:</span>
          <span>{{ errorMessage }}</span>
        </div>
        <button
          @click="fetchData"
          class="px-2.5 py-1 bg-rose-100 hover:bg-rose-200 rounded font-semibold transition"
        >
          Retry
        </button>
      </div>

      <!-- 3. Filter Section (AC-4, AC-5, AC-6, AC-7, AC-8, AC-9) -->
      <FilterBar
        @filter-change="applyFilters"
        @reset="resetFilters"
      />

      <!-- 4. Loading State -->
      <div v-if="isLoading" class="bg-white rounded-xl shadow-sm border border-slate-200 p-12 text-center my-6">
        <div class="inline-block animate-spin rounded-full h-8 w-8 border-4 border-blue-600 border-t-transparent mb-3"></div>
        <p class="text-xs font-semibold text-slate-600">Loading call monitoring records...</p>
      </div>

      <!-- 5. Empty State (AC-12) -->
      <EmptyState
        v-else-if="!isLoading && records.length === 0"
        @reset="resetFilters"
      />

      <!-- 6. Tabel Data & Navigasi Paginasi (AC-2, AC-3, AC-10, AC-11) -->
      <div v-else>
        <CallTable
          :records="records"
          :sorting="sorting"
          @sort="toggleSort"
        />

        <Pagination
          :meta="meta"
          @change-page="setPage"
        />
      </div>
    </main>

    <!-- Footer -->
    <footer class="bg-white border-t border-slate-200 py-4 text-center text-xs text-slate-400">
      &copy; 2026 Customer Call Monitoring System &bull; Technical Assessment Portal
    </footer>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useCallMonitoring } from './composables/useCallMonitoring'
import Navbar from './components/Navbar.vue'
import FilterBar from './components/FilterBar.vue'
import CallTable from './components/CallTable.vue'
import Pagination from './components/Pagination.vue'
import EmptyState from './components/EmptyState.vue'

const {
  records,
  meta,
  isLoading,
  errorMessage,
  sorting,
  fetchData,
  setPage,
  toggleSort,
  applyFilters,
  resetFilters,
} = useCallMonitoring()

// Ambil data pertama kali saat halaman monitoring dibuka (AC-1, AC-3)
onMounted(() => {
  fetchData()
})
</script>
