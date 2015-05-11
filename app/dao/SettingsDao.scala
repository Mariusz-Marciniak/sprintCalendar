package dao

import entities.WorkingDays
import play.api.libs.json.{Json, JsObject, JsValue}

import scala.util.Try

trait SettingsDao {

  val DefaultDaysAndPrecisionOptions: JsObject = Json.obj(
    "workdays" -> Json.obj(
      WorkingDays.DaysNames(0) -> true,
      WorkingDays.DaysNames(1) -> true,
      WorkingDays.DaysNames(2) -> true,
      WorkingDays.DaysNames(3) -> true,
      WorkingDays.DaysNames(4) -> true,
      WorkingDays.DaysNames(5) -> false,
      WorkingDays.DaysNames(6) -> false),
    "precision" -> Json.obj(
      "type" -> "days"
    )
  )


  def saveEmployees(employees: JsValue): Try[JsValue]
  def loadEmployees: Try[JsValue]
  def loadEmployeesNames: Seq[String]
  def saveHolidays(holidays: JsValue): Try[JsValue]
  def loadHolidays: Try[JsValue]
  def saveSprints(sprints: JsValue): Try[JsValue]
  def loadSprints: Try[JsValue]
  def saveDayAndPrecision(sprints: JsValue): Try[JsValue]
  def loadDayAndPrecision: Try[JsValue]
}