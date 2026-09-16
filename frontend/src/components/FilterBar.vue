<template>
  <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-5 mb-6">
    <div class="flex items-center justify-between mb-4 pb-3 border-b border-slate-100">
      <div class="flex items-center space-x-2">
        <Filter class="w-4 h-4 text-red-700" />
        <h2 class="text-sm font-bold text-slate-800 uppercase tracking-wider">Call Filters & Search</h2>
      </div>
      <button
        v-if="hasActiveFilters"
        @click="handleReset"
        class="inline-flex items-center text-xs font-semibold text-rose-600 hover:text-rose-700 transition-colors"
      >
        <RotateCcw class="w-3.5 h-3.5 mr-1" />
        Reset Filters
      </button>
    </div>

    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
      <!-- 1. Search Keyword (AC-4) -->
      <div>
        <label class="block text-xs font-semibold text-slate-700 mb-1.5">Search Keyword</label>
        <div class="relative">
          <Search class="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" />
          <input
            type="text"
            v-model="localFilters.search"
            @input="onFilterChange"
            placeholder="Search by ID, CS, or Customer..."
            class="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-red-600 focus:border-red-600 bg-white placeholder-slate-400 transition"
          />
        </div>
      </div>

      <!-- 2. Start Period (AC-5, AC-6: latest 3 months limit) -->
      <div>
        <label class="block text-xs font-semibold text-slate-700 mb-1.5">Start Period</label>
        <div class="relative">
          <Calendar class="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" />
          <input
            type="date"
            v-model="localFilters.startDate"
            :min="minDate"
            :max="localFilters.endDate || maxDate"
            @change="onFilterChange"
            class="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-red-600 focus:border-red-600 bg-white text-slate-700 transition"
          />
        </div>
      </div>

      <!-- 3. End Period (AC-5, AC-6: latest 3 months limit) -->
      <div>
        <label class="block text-xs font-semibold text-slate-700 mb-1.5">End Period</label>
        <div class="relative">
          <Calendar class="w-4 h-4 text-slate-400 absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none" />
          <input
            type="date"
            v-model="localFilters.endDate"
            :min="localFilters.startDate || minDate"
            :max="maxDate"
            @change="onFilterChange"
            class="w-full pl-9 pr-3 py-2 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-red-600 focus:border-red-600 bg-white text-slate-700 transition"
          />
        </div>
      </div>

      <!-- 4. Customer Sentiment Filter (AC-7, AC-8) -->
      <div>
        <label class="block text-xs font-semibold text-slate-700 mb-1.5">Customer Sentiment</label>
        <select
          v-model="localFilters.sentiment"
          @change="onFilterChange"
          class="w-full px-3 py-2 text-xs rounded-lg border border-slate-300 focus:outline-none focus:ring-2 focus:ring-red-600 focus:border-red-600 bg-white text-slate-700 transition cursor-pointer"
        >
          <option value="ALL">All Sentiments</option>
          <option value="UNDER_70">Under 70% (&lt; 70%)</option>
          <option value="70_AND_ABOVE">70% or more (&ge; 70%)</option>
        </select>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from 'vue'
import { Search, Calendar, RotateCcw, Filter } from 'lucide-vue-next'
import { getThreeMonthsAgoDateString, getTodayDateString } from '../utils/dateFormatter'

const emit = defineEmits(['filter-change', 'reset'])

// Limit date range to latest 3 months (AC-6)
const minDate = getThreeMonthsAgoDateString()
const maxDate = getTodayDateString()

const localFilters = reactive({
  search: '',
  startDate: '',
  endDate: '',
  sentiment: 'ALL',
})

const hasActiveFilters = computed(() => {
  return (
    localFilters.search.trim() !== '' ||
    localFilters.startDate !== '' ||
    localFilters.endDate !== '' ||
    localFilters.sentiment !== 'ALL'
  )
})

let debounceTimer = null

function onFilterChange() {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(() => {
    emit('filter-change', { ...localFilters })
  }, 300)
}

function handleReset() {
  localFilters.search = ''
  localFilters.startDate = ''
  localFilters.endDate = ''
  localFilters.sentiment = 'ALL'
  emit('reset')
}
</script>
