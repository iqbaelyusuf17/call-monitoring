import { describe, it, expect } from 'vitest'
import { formatCallTimestamp, getTodayDateString, getThreeMonthsAgoDateString } from '../dateFormatter'
import { formatSentimentScore } from '../numberFormatter'

describe('formatSentimentScore', () => {
  it('formats numeric scores into one decimal percentage string', () => {
    expect(formatSentimentScore(85.5)).toBe('85.5%')
    expect(formatSentimentScore(100)).toBe('100.0%')
    expect(formatSentimentScore(0)).toBe('0.0%')
    expect(formatSentimentScore('72.34')).toBe('72.3%')
  })

  it('returns "-" for null, undefined, or NaN inputs', () => {
    expect(formatSentimentScore(null)).toBe('-')
    expect(formatSentimentScore(undefined)).toBe('-')
    expect(formatSentimentScore('invalid')).toBe('-')
  })
})

describe('formatCallTimestamp', () => {
  it('formats valid ISO date timestamp to Indonesian date with WIB suffix', () => {
    const isoDate = '2026-08-15T07:30:00Z'
    const result = formatCallTimestamp(isoDate)
    expect(result).toContain('WIB')
    expect(result).toContain('2026')
  })

  it('returns "-" when given empty, null, or undefined values', () => {
    expect(formatCallTimestamp(null)).toBe('-')
    expect(formatCallTimestamp(undefined)).toBe('-')
    expect(formatCallTimestamp('')).toBe('-')
  })

  it('returns raw value if date is invalid', () => {
    expect(formatCallTimestamp('not-a-date')).toBe('not-a-date')
  })
})

describe('date helper functions', () => {
  it('getTodayDateString returns YYYY-MM-DD format', () => {
    const today = getTodayDateString()
    expect(today).toMatch(/^\d{4}-\d{2}-\d{2}$/)
  })

  it('getThreeMonthsAgoDateString returns YYYY-MM-DD format and is before today', () => {
    const threeMonthsAgo = getThreeMonthsAgoDateString()
    const today = getTodayDateString()

    expect(threeMonthsAgo).toMatch(/^\d{4}-\d{2}-\d{2}$/)
    expect(new Date(threeMonthsAgo).getTime()).toBeLessThan(new Date(today).getTime())
  })
})
