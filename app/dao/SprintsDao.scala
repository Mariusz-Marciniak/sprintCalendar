package dao

import play.api.libs.json.JsValue

import scala.util.Try

trait SprintsDao {

  def saveSprintData(sprintIdentifier: String, sprintData: JsValue): Try[JsValue]
  def loadSprintData(sprintIdentifier: String): Try[JsValue]
}