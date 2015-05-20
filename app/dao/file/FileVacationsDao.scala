package dao.file

import dao.VacationsDao
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

class FileVacationsDao extends VacationsDao with FileNameConverter {

  override def saveVacations(employeeIdentifier: String, emplVacations: JsValue): Try[JsValue] = {
    val identifier = appendPrefixIfNotPresent(employeeIdentifier)
    println(s"saving vacations $emplVacations of : $identifier ")
    val data = Json.obj("id" -> employeeIdentifier, "vacations"-> emplVacations)
    save("data/vacations/"+toFilename(identifier)+".json", data)
    Success(emplVacations)
  }
  override def loadVacations(employeeIdentifier: String): Try[JsValue] = {
    val identifier = appendPrefixIfNotPresent(employeeIdentifier)
    load("data/vacations/"+toFilename(identifier)+".json") match {
      case Success(v) => Success(v \ "vacations")
      case Failure(e) => Failure(e)
    }
  }
}
