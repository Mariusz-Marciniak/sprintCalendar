package controllers

import play.api.Routes
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.ArrayBuffer
import config.JsonImplicits._

object Dashboard extends Controller {
  import config.Configuration._

  private val settingsDao = configuration.settingsDao
  private val vacationsDao = configuration.vacationsDao

  def javascriptRoutes = Action {implicit request =>
    Ok(
      Routes.javascriptRouter("dashboardJsRoutes") (
        routes.javascript.Dashboard.timelineData
      )
    ).as("text/javascript")
  }

  def timelineData = Action { implicit request =>
    val timelines: ArrayBuffer[JsObject] = ArrayBuffer()
    val employees: Seq[String] = settingsDao.loadEmployeesNames

    for(employee <- employees) {
      val entries: JsArray = vacationsDao.loadVacations(vacationsDao.VacationsPrefix+employee).getOrElse(JsArray())
      timelines += Json.obj(
        "label" -> employee,
        "entries" -> entries
      )
    }
    Ok(Json.toJson(timelines));
  }

  def mainPage = Action {
    Ok(views.html.dashboard())
  }

}

