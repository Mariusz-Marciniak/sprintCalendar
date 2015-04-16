package controllers

import java.util.Date

import play.api.Routes
import play.api.libs.json.{JsArray, JsValue}
import play.api.mvc.{Action, Controller};


object Settings extends Controller {

  def javascriptRoutes = Action {implicit request =>
    Ok(
      Routes.javascriptRouter("settingsJsRoutes") (
        routes.javascript.Settings.employees,
        routes.javascript.Settings.holidays,
        routes.javascript.Settings.sprints,
        routes.javascript.Settings.saveSettings
      )
    ).as("text/javascript")
  }

  def employees = Action { implicit request =>
    Ok(SettingsData.loadEmployees);
  }
  def holidays = Action { implicit request =>
    Ok(SettingsData.loadHolidays);
  }
  def sprints = Action { implicit request =>
    Ok(SettingsData.loadSprints);
  }

  def mainPage = Action { implicit request =>
    Ok(views.html.settings())
  }

  def saveSettings = Action(parse.json) { implicit request =>
    SettingsData.saveSettings(request.body)
    Ok(views.html.settings())
  }

}

object SettingsData {
  import config.Configuration._

  val Employees = "employees"
  val Holidays = "holidays"
  val Sprints = "sprints"

  private val dao = config.dao

  def saveSettings(data: JsValue): Unit = {
    dao.saveEmployees(convertToJsArray(data \ Employees))
    dao.saveHolidays(convertToJsArray(data \ Holidays))
    dao.saveSprints(convertToJsArray(data \ Sprints))
  }

  private def convertToJsArray(value: JsValue): JsArray = {
    value match {
      case v: JsArray => v
      case _ => JsArray(Seq())
    }
  }


  def loadEmployees = dao.loadEmployees

  def loadHolidays = dao.loadHolidays

  def loadSprints = dao.loadSprints

  def parseToDate(date: String): Date = config.AppDateFormat.parse(date)

  def formatDate(date: Date): String = config.AppDateFormat.format(date)

}