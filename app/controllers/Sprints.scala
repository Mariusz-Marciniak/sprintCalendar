package controllers

import play.api.Routes
import play.api.libs.json.JsArray
import play.api.mvc.{Action, Controller}


object Sprints extends Controller {

  import config.Configuration._
  import config.JsonImplicits._

  private val settingsDao = configuration.settingsDao
  private val vacationsDao = configuration.vacationsDao

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("sprintsJsRoutes")(
        routes.javascript.Sprints.saveSprintData,
        routes.javascript.Sprints.sprints
      )
    ).as("text/javascript")
  }

  def saveSprintData(sprintId: String) = Action(parse.json) { implicit request =>
    Ok("aqq")
  }

  def mainPage = Action { implicit request => {
    Ok(views.html.sprints())
  }}

  def sprints = Action { implicit request => {
    Ok(settingsDao.loadSprints.getOrElse(JsArray()))
  }}


  private def sprintsNames(): Seq[String] = {
    val sprints = settingsDao.loadSprints.getOrElse(JsArray())
    (sprints \\ "name") map {
      castToJsString(_).value
    }
  }
}
