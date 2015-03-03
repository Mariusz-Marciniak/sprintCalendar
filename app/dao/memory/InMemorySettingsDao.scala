package dao.memory

import dao.SettingsDao
import play.api.libs.json.JsObject

class InMemorySettingsDao extends SettingsDao {
  private var employees: JsObject = _
  private var holidays: JsObject = _
  private var sprints: JsObject = _

  def saveEmployees(employees: JsObject): Unit = {
    this.employees = employees
  }

  def loadEmployees: JsObject = employees

  def saveHolidays(holidays: JsObject): Unit = {
    this.holidays = holidays;
  }

  def loadHolidays: JsObject = holidays

  def saveSprints(sprints: JsObject): Unit = {
    this.sprints = sprints;
  }

  def loadSprints: JsObject = sprints

}
