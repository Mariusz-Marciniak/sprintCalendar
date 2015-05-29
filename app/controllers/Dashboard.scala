package controllers

import entities.{DateRange, Statistics, UserDefaults}
import play.api.Routes
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.ArrayBuffer
import config.JsonImplicits._

object Dashboard extends Controller {
  import config.Configuration._

  private val settingsDao = configuration.settingsDao
  private val vacationsDao = configuration.vacationsDao
  private val userDefaultsDao = configuration.userDefaultsDao

  def javascriptRoutes = Action {implicit request =>
    Ok(
      Routes.javascriptRouter("dashboardJsRoutes") (
        routes.javascript.Dashboard.timelineData,
        routes.javascript.Dashboard.saveDefaults
      )
    ).as("text/javascript")
  }

  def saveDefaults(fromDate:String, toDate:String) = Action {
    userDefaultsDao.saveDefaults(UserDefaults(fromDate, toDate))
    Ok("Data saved")
  }

  def loadDefaults() = userDefaultsDao.loadDefaults().getOrElse(UserDefaults())

  def timelineData = Action { implicit request =>
    val timelines: ArrayBuffer[JsObject] = ArrayBuffer()
    val employees: Seq[String] = settingsDao.loadEmployeesNames

    for(employee <- employees) {
      val entries: JsArray = vacationsDao.loadVacations(employee).getOrElse(JsArray())
      timelines += Json.obj(
        "label" -> employee,
        "entries" -> entries
      )
    }
    Ok(Json.toJson(timelines));
  }

  def mainPage = Action {
    val defaults = loadDefaults()
    val range = DateRange(AppDateFormatter.parseLocalDate(defaults.timelineDateFrom), AppDateFormatter.parseLocalDate(defaults.timelineDateTo))
    Ok(views.html.dashboard(Statistics(range)))
  }

}

