package dao

import play.api.libs.json.JsValue

import scala.util.Try

trait SettingsDao {
  def saveEmployees(employees: JsValue): Try[JsValue]
  def loadEmployees: Try[JsValue]
  def loadEmployeesNames: Seq[String]
  def saveHolidays(holidays: JsValue): Try[JsValue]
  def loadHolidays: Try[JsValue]
  def saveSprints(sprints: JsValue): Try[JsValue]
  def loadSprints: Try[JsValue]
}