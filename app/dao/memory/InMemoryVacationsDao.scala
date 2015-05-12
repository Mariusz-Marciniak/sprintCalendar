package dao.memory

import dao.VacationsDao
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

class InMemoryVacationsDao extends VacationsDao {

  private var vacations: Map[String, JsValue] = Map(
    "vacationsOfMary"->JsArray(Seq(
      JsObject(Seq(
        ("label",JsString("2015-12-12::2015-12-28")),
        ("accepted", JsBoolean(true)),
        ("from",JsString("2015-12-12")),
        ("to",JsString("2015-12-28"))
      ))
    ))
  )

  override def saveVacations(employeeIdentifier: String, emplVacations: JsValue): Try[JsValue] = {
    val identifier = appendPrefixIfNotPresent(employeeIdentifier)
    println(s"saving vacations $emplVacations of : $identifier ")
    vacations = vacations + Tuple2(identifier, emplVacations)
    Success(emplVacations)
  }
  override def loadVacations(employeeIdentifier: String): Try[JsValue] = {
    try {
      val identifier = appendPrefixIfNotPresent(employeeIdentifier)
      Success(vacations(identifier))
    } catch {
      case e: NoSuchElementException => Failure(e)
    }
  }

}
