package controllers

import play.api.Routes
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.ArrayBuffer

object Dashboard extends Controller {
  import config.Configuration._

  private val settingsDao = config.settingsDao
  private val vacationsDao = config.vacationsDao

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
      timelines += Json.obj(
        "label" -> employee,
        "entries" -> vacationsDao.loadVacations(vacationsDao.VacationsPrefix+employee)
      )
    }
    Ok(Json.toJson(timelines));
  }

  def mainPage = Action {
    Ok(views.html.dashboard())
  }

}

