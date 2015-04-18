package dao.memory

import dao.VacationsDao
import play.api.libs.json
import play.api.libs.json.{JsObject, JsValue, JsArray, JsString}

class InMemoryVacationsDao extends VacationsDao {
  private var vacations: Map[String, JsArray] = Map(
    "vacationsOfMary"->JsArray(Seq(
      JsObject(Seq(("label",JsString("Puerto"))))
    ))
  )

  def saveVacations(employeeIdentifier: String, emplVacations: JsArray): Unit = {
    vacations = vacations + Tuple2(employeeIdentifier, emplVacations)
  }
  def loadVacations(employeeIdentifier: String): JsArray = {
    vacations(employeeIdentifier)
  }

}
