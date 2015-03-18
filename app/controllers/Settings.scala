package controllers

import config.Configuration
import play.api.Routes
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json, JsArray}
import play.api.mvc.{Action, Controller}
import java.util.Date;


object Settings extends Controller {
/*
  val settings: Form[Settings] = Form(
    mapping(
      "users" -> text,
      "holidays" -> text
    )
      ((users, holidays) => {
        Settings(Json.parse(users), Json.parse(holidays))
      })
      ((s) => {
        Some((Json.stringify(s.users), Json.stringify(s.holidays)))
      })
  )
*/


  def javascriptRoutes = Action {implicit request =>
    Ok(
      Routes.javascriptRouter("settingsJsRoutes") (
        routes.javascript.Settings.users,
        routes.javascript.Settings.holidays,
        routes.javascript.Settings.sprints,
        routes.javascript.Settings.saveSettings
      )
    ).as("text/javascript")
  }

  def users = Action { implicit request =>
    Ok(SettingsData.loadEmployees);
  }
  def holidays = Action { implicit request =>
    Ok(SettingsData.loadHolidays);
  }
  def sprints = Action { implicit request =>
    Ok(SettingsData.loadSprints);
  }

  def mainPage = Action { implicit request =>
    Ok(views.html.settings("Settings"))
  }

  def saveSettings = Action { implicit request =>
    val body = request.body
    println(body)
    Ok(views.html.settings("Settings"))
  }


}

case class SettingsObj(users: JsValue, holidays: JsValue)

object SettingsData {
  import config.Configuration._

  val Employees = "EMPLOYEES"
  val Holidays = "HOLIDAYS"
  val Sprints = "SPRINTS"

  private val dao = config.dao

  def saveSettings(data: Map[String, JsArray]): Unit = {
    dao.saveEmployees(data(Employees))
    dao.saveHolidays(data(Holidays))
    dao.saveSprints(data(Sprints))
  }

  def loadEmployees = dao.loadEmployees

  def loadHolidays = dao.loadHolidays

  def loadSprints = dao.loadSprints

  def parseToDate(date: String): Date = config.AppDateFormat.parse(date)

  def formatDate(date: Date): String = config.AppDateFormat.format(date)

}