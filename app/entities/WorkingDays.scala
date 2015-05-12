package entities

import com.github.nscala_time.time.Imports._
import entities.WorkingDays.{Dates, Holidays}
import org.joda.time.{DateTimeFieldType, ReadablePartial}
import play.api.libs.json._

object WorkingDays {

  import config.JsonImplicits._

  type Holidays = Seq[ReadablePartial]
  type Dates = Seq[LocalDate]

  val DaysNames = List("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday")

  def holidaysFromJsArray(holidays: JsArray): Holidays = {
    holidays map (jsValue =>  {
      val date : JsString = jsValue \ "date"
      val dateParts = date.value.split("-")
      if(dateParts.length > 2)
        new Partial(
          Array(DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()),
          Array(dateParts(0).toInt,dateParts(1).toInt,dateParts(2).toInt)
        )
      else
        new Partial(
          Array(DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()),
          Array(dateParts(0).toInt,dateParts(1).toInt)
        )
    })
  }

  /**
   * Prepares sequence of dates that match with given partials in [fromDate,toDate] range. For partials without year it will return specific day for each year
   * @param holidays
   * @param range
   * @return
   */
  def holidaysInRange(holidays: Holidays, range: DateRange): Dates = {
    def isParitalInRange(partial: ReadablePartial) : Boolean =
      compareDatePartials(partial, range.fromDate) >= 0 && compareDatePartials(partial, range.toDate) <= 0

    holidays filter isParitalInRange map (partial =>
      if(partial.isSupported(DateTimeFieldType.year()))
        List(new LocalDate(partial.get(DateTimeFieldType.year()),partial.get(DateTimeFieldType.monthOfYear()),partial.get(DateTimeFieldType.dayOfMonth())))
      else
        for {
          year <- range.fromDate.year.get to range.toDate.year.get
        } yield new LocalDate(year,partial.get(DateTimeFieldType.monthOfYear()),partial.get(DateTimeFieldType.dayOfMonth()))
      ) flatten
  }

  def compareDatePartials(partial1: ReadablePartial, partial2: ReadablePartial) : Int = {
    var result = 0
    if(partial1.isSupported(DateTimeFieldType.year()) && partial2.isSupported(DateTimeFieldType.year())) {
      result = partial1.get(DateTimeFieldType.year()) - partial2.get(DateTimeFieldType.year())
    }
    if(result == 0 && partial1.isSupported(DateTimeFieldType.monthOfYear()) && partial2.isSupported(DateTimeFieldType.monthOfYear())) {
        result = partial1.get(DateTimeFieldType.monthOfYear()) - partial2.get(DateTimeFieldType.monthOfYear())
    }
    if(result == 0) {
      result = partial1.get(DateTimeFieldType.dayOfMonth()) - partial2.get(DateTimeFieldType.dayOfMonth())
    }
    result
  }

  /**
   * Retrieves workdays data from passed JSON object. Passed object needs to have property workdays with proper flags for each day
   * e.g. { "workdays" : {"Monday" : true, "Tuesday" -> false} }
   * Unspecified days are treaten as free days.
   *
   * @param settings - JSON object
   * @return work days indexes in Javascript format (0 Sunday - 6 Saturday)
   */
  def workdaysFromJsObject(settings: JsObject): Seq[Int] = {
    def processWorkdays(workdaysJson: JsObject): List[Boolean] = {
      DaysNames map (dayName =>
        workdaysJson \ dayName match {
          case marked: JsBoolean => marked.value
          case _:JsValue => false
        }
      )
    }
    settings \ "workdays" match {
      case _:JsUndefined => throw new IllegalArgumentException("Couldn't find workdays property")
      case workdaysJson: JsObject => {
          val workdays  = processWorkdays(workdaysJson)
          (workdays.zipWithIndex filter{ case (value,index) => value} unzip)._2
      }
      case _:JsValue => throw new IllegalArgumentException("Illegal type of workdays, should be JsObject")
    }
  }

  /**
   * Converts JavaScript week days indexes into Joda. Generally the difference is Sunday 0 in js and 7 in Joda.
   * Returned result is unspecified for values out of 0 to 6 range.
   *
   * @param jsWeekDayIndex value from 0 to 6
   * @return Joda week day index
   */
  def convertWeekDayToJoda(jsWeekDayIndex: Int) : Int = if(jsWeekDayIndex == 0) 7 else jsWeekDayIndex

  /**
   * Converts Joda week days indexes into JavaScript. Generally the difference is Sunday 0 in js and 7 in Joda.
   * Returned result is unspecified for values out of 1 to 7 range.
   *
   * @param jodaWeekDayIndex value from 1 to 7
   * @return Javascript.Date week day index
   */
  def convertWeekDayToJs(jodaWeekDayIndex: Int) : Int = if(jodaWeekDayIndex == 7) 0 else jodaWeekDayIndex

  /**
   * Returns all workdays that are included in dates range
   * @param range
   * @param workdays in JavaScript format (Sunday - 0)
   * @return
   */
  def workdaysInRange(range: DateRange, workdays: Seq[Int]): WorkingDays  = {
    var date = range.fromDate
    var result = Array[LocalDate]()
    while(date.isBefore(range.toDate) || date.isEqual(range.toDate)) {
      if(workdays contains convertWeekDayToJs(date.dayOfWeek().get))
        result = result :+ date
      date = date.plusDays(1)
    }
    WorkingDays(result.toSeq)
  }
  
  /**
   * 
   */
  def vacationsFromJsArray(vacations: JsArray) : Seq[DateRange] = {
    vacations map (jsValue =>  {
      val fromDate : JsString = jsValue \ "from"
      val toDate : JsString = jsValue \ "to"
      DateRange(LocalDate.parse(fromDate.value),LocalDate.parse(toDate.value))
    })
  }

}

case class WorkingDays(dates: Dates) {
  def filterHolidays(holidays: Dates) : WorkingDays = {
    WorkingDays (dates filter (!holidays.contains(_)))
  }
  def filterEmployeeVacations(vacations: Seq[DateRange]) : WorkingDays = {
    WorkingDays(dates filter(date => vacations.find(_.contains(date)) == None))
  }
}

case class DateRange(fromDate:LocalDate, toDate: LocalDate) {
  def notContains(date: LocalDate): Boolean = fromDate.isAfter(date) || toDate.isBefore(date)
  def contains(date: LocalDate): Boolean = !notContains(date)
}
