package dao

import play.api.libs.json.JsObject

trait SettingsDao {
  def saveEmployees(employees: JsObject): Unit
  def loadEmployees: JsObject
  def saveHolidays(holidays: JsObject): Unit
  def loadHolidays: JsObject
  def saveSprints(sprints: JsObject): Unit
  def loadSprints: JsObject
}