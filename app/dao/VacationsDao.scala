package dao

import play.api.libs.json.JsArray

trait VacationsDao {
  def saveVacations(employeeIdentifier: String, vacations: JsArray): Unit
  def loadVacations(employeeIdentifier: String): JsArray
}