package dao

import play.api.libs.json.{Json, JsObject, JsValue}

import scala.util.Try

trait SettingsDao {
  val DefaultDaysAndPrecisionOptions: JsObject = Json.obj(
    "workdays" -> Json.obj(
      "Monday" -> true,
      "Tuesday" -> true,
      "Wednesday" -> true,
      "Thursday" -> true,
      "Friday" -> true,
      "Saturday" -> false,
      "Sunday" -> false),
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