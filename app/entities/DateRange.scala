package entities

import com.github.nscala_time.time.Imports._

case class DateRange(fromDate:LocalDate, toDate: LocalDate) {
  def notContains(date: LocalDate): Boolean = fromDate.isAfter(date) || toDate.isBefore(date)
  def contains(date: LocalDate): Boolean = !notContains(date)
  def in(range: DateRange): Boolean = range.contains(fromDate) && range.contains(toDate)
}
