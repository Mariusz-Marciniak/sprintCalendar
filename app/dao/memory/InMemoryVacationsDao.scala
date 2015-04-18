package dao.memory

import dao.VacationsDao
import play.api.libs.json
import play.api.libs.json.{JsObject, JsValue, JsArray, JsString, JsBoolean}

class InMemoryVacationsDao extends VacationsDao {
  private var vacations: Map[String, JsArray] = Map(
    "vacationsOfMary"->JsArray(Seq(
      JsObject(Seq(
        ("label",JsString("2015-12-12::2015-12-28")),
        ("accepted", JsBoolean(true)),
        ("from",JsString("2015-12-12")),
        ("to",JsString("2015-12-28"))
      ))
    ))
  )

  override def saveVacations(employeeIdentifier: String, emplVacations: JsArray): Unit = {
    println(s"saving vacations $emplVacations of : $employeeIdentifier ")
    vacations = vacations + Tuple2(employeeIdentifier, emplVacations)
  }
  override def loadVacations(employeeIdentifier: String): JsArray = {
    vacations(employeeIdentifier)
  }

}
