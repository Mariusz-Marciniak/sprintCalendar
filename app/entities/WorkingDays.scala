package entities

import play.api.libs.json.{JsArray, JsValue}
import com.github.nscala_time.time.Imports._


object WorkingDays {


  def amountOfWorkdays(from: DateTime, to: DateTime, workDays: Seq[Int]): Int  = {
    5
  }
  def amountOfWorkdays(sprint: JsValue, holidays: JsArray, vacations: JsArray): Int  = {
    println(sprint)
    println(holidays)
    println(vacations)
    5
  }
}
