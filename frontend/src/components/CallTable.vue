<template>
  <div class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
    <div class="overflow-x-auto">
      <table class="min-w-full divide-y divide-slate-200 text-left text-xs">
        <thead class="bg-slate-50 text-slate-600 font-bold uppercase tracking-wider">
          <tr>
            <!-- 0. No. (AC-2) -->
            <th scope="col" class="px-4 py-3.5 w-16 text-center">
              No.
            </th>

            <!-- 1. Call ID (Clickable Sort - AC-10) -->
            <th
              scope="col"
              @click="$emit('sort', 'call_id')"
              class="px-4 py-3.5 cursor-pointer hover:bg-slate-100 select-none transition-colors"
            >
              <div class="flex items-center space-x-1">
                <span>Call ID</span>
                <component :is="getSortIcon('call_id')" class="w-3.5 h-3.5" :class="getSortIconColor('call_id')" />
              </div>
            </th>

            <!-- 2. Call Timestamp (Clickable Sort - AC-10) -->
            <th
              scope="col"
              @click="$emit('sort', 'call_timestamp')"
              class="px-4 py-3.5 cursor-pointer hover:bg-slate-100 select-none transition-colors"
            >
              <div class="flex items-center space-x-1">
                <span>Call Timestamp</span>
                <component :is="getSortIcon('call_timestamp')" class="w-3.5 h-3.5" :class="getSortIconColor('call_timestamp')" />
              </div>
            </th>

            <!-- 3. CS Name (Clickable Sort - AC-10) -->
            <th
              scope="col"
              @click="$emit('sort', 'cs_name')"
              class="px-4 py-3.5 cursor-pointer hover:bg-slate-100 select-none transition-colors"
            >
              <div class="flex items-center space-x-1">
                <span>CS Name</span>
                <component :is="getSortIcon('cs_name')" class="w-3.5 h-3.5" :class="getSortIconColor('cs_name')" />
              </div>
            </th>

            <!-- 4. Customer Name (Clickable Sort - AC-10) -->
            <th
              scope="col"
              @click="$emit('sort', 'customer_name')"
              class="px-4 py-3.5 cursor-pointer hover:bg-slate-100 select-none transition-colors"
            >
              <div class="flex items-center space-x-1">
                <span>Customer Name</span>
                <component :is="getSortIcon('customer_name')" class="w-3.5 h-3.5" :class="getSortIconColor('customer_name')" />
              </div>
            </th>

            <!-- 5. Customer Sentiment Score (Clickable Sort - AC-10) -->
            <th
              scope="col"
              @click="$emit('sort', 'sentiment_score')"
              class="px-4 py-3.5 text-right cursor-pointer hover:bg-slate-100 select-none transition-colors"
            >
              <div class="flex items-center justify-end space-x-1">
                <span>Sentiment Score</span>
                <component :is="getSortIcon('sentiment_score')" class="w-3.5 h-3.5" :class="getSortIconColor('sentiment_score')" />
              </div>
            </th>
          </tr>
        </thead>

        <tbody class="divide-y divide-slate-100 text-slate-700">
          <tr
            v-for="(row, index) in records"
            :key="row.call_id"
            class="hover:bg-slate-50/80 transition-colors"
          >
            <!-- 0. No. (AC-2) -->
            <td class="px-4 py-3.5 text-center font-mono text-slate-500 font-medium">
              {{ calculateRowNumber(index) }}
            </td>

            <!-- 1. Call ID -->
            <td class="px-4 py-3.5 font-semibold text-slate-900 font-mono">
              {{ row.call_id }}
            </td>

            <!-- 2. Call Timestamp (Formatted) -->
            <td class="px-4 py-3.5 text-slate-600 whitespace-nowrap">
              {{ formatCallTimestamp(row.call_timestamp) }}
            </td>

            <!-- 3. CS Name -->
            <td class="px-4 py-3.5 font-medium text-slate-800">
              {{ row.cs_name || '-' }}
            </td>

            <!-- 4. Customer Name -->
            <td class="px-4 py-3.5 font-medium text-slate-900">
              {{ row.customer_name || '-' }}
            </td>

            <!-- 5. Sentiment Score -->
            <td class="px-4 py-3.5 text-right font-semibold">
              <span
                v-if="row.sentiment_score !== null && row.sentiment_score !== undefined"
                :class="getSentimentBadgeClass(row.sentiment_score)"
                class="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-semibold"
              >
                {{ formatSentimentScore(row.sentiment_score) }}
              </span>
              <span v-else class="text-slate-400 font-normal">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ArrowUpDown, ArrowUp, ArrowDown } from 'lucide-vue-next'
import { formatCallTimestamp } from '../utils/dateFormatter'
import { formatSentimentScore } from '../utils/numberFormatter'

const props = defineProps({
  records: {
    type: Array,
    required: true,
  },
  meta: {
    type: Object,
    default: () => ({ page: 1, limit: 10 }),
  },
  sorting: {
    type: Object,
    default: () => ({ sortBy: 'call_timestamp', sortOrder: 'desc' }),
  },
})

defineEmits(['sort'])

function calculateRowNumber(index) {
  const page = props.meta?.page || 1
  const limit = props.meta?.limit || 10
  return (page - 1) * limit + index + 1
}

function getSortIcon(column) {
  if (props.sorting.sortBy !== column) {
    return ArrowUpDown
  }
  return props.sorting.sortOrder === 'asc' ? ArrowUp : ArrowDown
}

function getSortIconColor(column) {
  if (props.sorting.sortBy === column) {
    return 'text-red-700'
  }
  return 'text-slate-400'
}

function getSentimentBadgeClass(score) {
  const num = Number(score)
  if (num >= 70) {
    return 'bg-emerald-50 text-emerald-700 border border-emerald-200'
  }
  return 'bg-rose-50 text-rose-700 border border-rose-200'
}
</script>
