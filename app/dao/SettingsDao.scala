package dao

import play.api.libs.json.JsArray

trait SettingsDao {
  def saveEmployees(employees: JsArray): Unit
  def loadEmployees: JsArray
  def saveHolidays(holidays: JsArray): Unit
  def loadHolidays: JsArray
  def saveSprints(sprints: JsArray): Unit
  def loadSprints: JsArray
}