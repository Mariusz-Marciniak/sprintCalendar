package dao.file

import dao.SprintsDao
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

class FileSprintsDao extends SprintsDao with FileNameConverter {

  override def saveSprintData(sprintIdentifier: String, sprintData: JsValue): Try[JsValue] = {
    println(s"saving sprint data $sprintData for : $sprintIdentifier ")
    val data = Json.obj("id" -> sprintIdentifier, "data"-> sprintData)
    save("data/sprints/"+toFilename(sprintIdentifier)+".json", data)
    Success(sprintData)
  }

  override def loadSprintData(sprintIdentifier: String): Try[JsValue] = {
    try {
      load("data/sprints/"+toFilename(sprintIdentifier)+".json") match {
        case Success(v) => Success(v \ "data")
        case Failure(e) => Failure(e)
      }
    } catch {
      case e: NoSuchElementException => Failure(e)
    }
  }

}
