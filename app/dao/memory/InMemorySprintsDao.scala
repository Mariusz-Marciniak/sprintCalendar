package dao.memory

import dao.SprintsDao
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

class InMemorySprintsDao extends SprintsDao {

  private var sprints: Map[String, JsValue] = Map(
    "Sprint 1 2015-12-14::2016-01-01"->Json.obj(
      "storyPoints" -> 133,
      "workload" -> Json.arr(
        Json.obj("employee"->"Thomas", "availability"->10, "maxAvailability"->18),
        Json.obj("employee"->"Mary", "availability"->2, "maxAvailability"->6)
      )
    ),
    "Sprint 2 2016-01-02::2016-01-18"->Json.obj(
      "storyPoints" -> 78,
      "confirmed" -> true,
      "workload" -> Json.arr(
        Json.obj("employee"->"Mary", "availability"->8, "maxAvailability"->12)
      )
    )
  )

  override def saveSprintData(sprintIdentifier: String, sprintData: JsValue): Try[JsValue] = {
    println(s"saving sprint data $sprintData for : $sprintIdentifier ")
    sprints = sprints + Tuple2(sprintIdentifier, sprintData)
    Success(sprintData)
  }
  override def loadSprintData(sprintIdentifier: String): Try[JsValue] = {
    try {
      Success(sprints(sprintIdentifier))
    } catch {
      case e: NoSuchElementException => Failure(e)
    }
  }

}
