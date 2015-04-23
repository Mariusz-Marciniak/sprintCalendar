package dao

import play.api.libs.json.JsValue

import scala.util.Try

trait VacationsDao {
  val VacationsPrefix = "vacationsOf"

  def saveVacations(employeeIdentifier: String, vacations: JsValue): Try[JsValue]
  def loadVacations(employeeIdentifier: String): Try[JsValue]
}