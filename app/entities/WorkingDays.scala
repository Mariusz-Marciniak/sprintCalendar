package entities

import org.joda.time.{DateTimeField, DateTimeFieldType, ReadablePartial}
import play.api.libs.json.{JsString, JsArray, JsValue}
import com.github.nscala_time.time.Imports._


object WorkingDays {

  import config.JsonImplicits._

  def holidaysFromJsArray(holidays: JsArray) : Seq[ReadablePartial] = {
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

  def holidaysInRange(from: LocalDate, to: LocalDate, holidays: Seq[ReadablePartial]): Seq[ReadablePartial] = {
    Seq()
  }

  def isParitalInRange(partial: ReadablePartial, from: LocalDate, to: LocalDate ) : Boolean =
    compareDatePartials(partial, from) <= 0 && compareDatePartials(partial, to) >= 0

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

  def amountOfWorkdays(from: LocalDate, to: LocalDate, workDays: Seq[Int]): Int  = {
    5
  }
  def amountOfWorkdays(sprint: JsValue, holidays: JsArray, vacations: JsArray): Int  = {
    println(sprint)
    println(holidays)
    println(vacations)
    5
  }

}
