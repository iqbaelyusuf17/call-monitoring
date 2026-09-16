<template>
  <div class="bg-white rounded-xl shadow-sm border border-slate-200 p-12 text-center my-6">
    <div class="w-14 h-14 bg-red-50 text-red-700 rounded-2xl flex items-center justify-center mx-auto mb-4 border border-red-100 shadow-inner">
      <SearchX class="w-7 h-7" />
    </div>
    <h3 class="text-base font-bold text-slate-800 mb-1.5">No Call Records Found</h3>
    <p class="text-xs text-slate-500 max-w-md mx-auto mb-5 leading-relaxed">
      No call monitoring records match your search keyword, date range, or sentiment filter.
    </p>
    <button
      @click="$emit('reset')"
      class="inline-flex items-center px-4 py-2 text-xs font-semibold rounded-lg bg-red-700 text-white hover:bg-red-800 active:bg-red-900 shadow-sm transition cursor-pointer"
    >
      <RotateCcw class="w-3.5 h-3.5 mr-1.5" />
      Reset All Filters
    </button>
  </div>
</template>

<script setup>
import { SearchX, RotateCcw } from 'lucide-vue-next'

defineEmits(['reset'])
</script>
