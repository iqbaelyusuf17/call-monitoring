<template>
  <header class="bg-white border-b border-slate-200 shadow-sm sticky top-0 z-20">
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
      <!-- Brand & Menu Title (AC-1) -->
      <div class="flex items-center space-x-3">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-blue-700 to-indigo-600 flex items-center justify-center text-white shadow-md shadow-blue-500/20">
          <PhoneCall class="w-5 h-5" />
        </div>
        <div>
          <div class="flex items-center space-x-2">
            <h1 class="text-base font-bold text-slate-900 tracking-tight">Call Monitoring</h1>
            <span class="inline-flex items-center px-2 py-0.5 rounded text-[11px] font-semibold bg-blue-50 text-blue-700 border border-blue-200">
              Monitoring Menu
            </span>
          </div>
          <p class="text-xs text-slate-500 font-medium">Customer Service Supervision Portal</p>
        </div>
      </div>

      <!-- Supervisor Profile Badge -->
      <div class="flex items-center space-x-3">
        <div class="hidden sm:flex flex-col text-right">
          <span class="text-xs font-semibold text-slate-800">Supervisor Service</span>
          <span class="text-[11px] text-emerald-600 font-medium flex items-center justify-end">
            <span class="w-1.5 h-1.5 rounded-full bg-emerald-500 inline-block mr-1"></span>
            Online
          </span>
        </div>
        <div class="w-9 h-9 rounded-full bg-slate-100 border border-slate-200 flex items-center justify-center text-slate-700 font-bold text-xs shadow-inner">
          SPV
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { PhoneCall } from 'lucide-vue-next'
</script>
