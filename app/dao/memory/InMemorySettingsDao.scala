package dao.memory

import dao.SettingsDao
import play.api.libs.json
import play.api.libs.json.{JsValue, JsArray, JsString}

class InMemorySettingsDao extends SettingsDao {
  private var employees: JsArray = JsArray(Seq())
  private var holidays: JsArray = JsArray(Seq())
  private var sprints: JsArray = JsArray(Seq())

  def saveEmployees(employees: JsArray): Unit = {
    this.employees = employees
  }

  def loadEmployees: JsArray = employees

  def saveHolidays(holidays: JsArray): Unit = {
    this.holidays = holidays
  }

  def loadHolidays: JsArray = holidays

  def saveSprints(sprints: JsArray): Unit = {
    this.sprints = sprints
  }

  def loadSprints: JsArray = sprints

}
