package dao.file

import java.io.{BufferedWriter, FileWriter}

import dao.VacationsDao
import play.api.libs.json._

import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success, Try}

class FileVacationsDao extends VacationsDao {

  override def saveVacations(employeeIdentifier: String, emplVacations: JsValue): Try[JsValue] = {
    val identifier = appendPrefixIfNotPresent(employeeIdentifier)
    println(s"saving vacations $emplVacations of : $identifier ")
    save("data/"+identifier+".json", emplVacations)
    Success(emplVacations)
  }
  override def loadVacations(employeeIdentifier: String): Try[JsValue] = {
    val identifier = appendPrefixIfNotPresent(employeeIdentifier)
    load("data/"+identifier+".json")
  }
}
