package dao

import play.api.libs.json.JsValue

import scala.util.Try

trait VacationsDao {
  val VacationsPrefix = "vacationsOf"

  def saveVacations(employeeIdentifier: String, vacations: JsValue): Try[JsValue]
  def loadVacations(employeeIdentifier: String): Try[JsValue]

  def appendPrefixIfNotPresent(identifier: String): String = if(identifier.startsWith(VacationsPrefix)) identifier else VacationsPrefix+identifier
}