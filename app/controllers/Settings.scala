package controllers

import dao.SettingsDao
import play.api.Routes
import play.api.libs.json.JsArray
import play.api.mvc.{Action, Controller};


object Settings extends Controller {

  import config.Configuration._

  val Employees = "employees"
  val Holidays = "holidays"
  val Sprints = "sprints"
  val DaysAndPrecisionOptions = "dayHoursOptions"

  private val settingsDao = configuration.settingsDao

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("settingsJsRoutes")(
        routes.javascript.Settings.employees,
        routes.javascript.Settings.holidays,
        routes.javascript.Settings.sprints,
        routes.javascript.Settings.dayAndPrecisionOptions,
        routes.javascript.Settings.saveSettings
      )
    ).as("text/javascript")
  }

  def employees = Action { implicit request =>
    Ok(settingsDao.loadEmployees.getOrElse(JsArray()));
  }

  def holidays = Action { implicit request =>
    Ok(settingsDao.loadHolidays.getOrElse(JsArray()));
  }

  def sprints = Action { implicit request =>
    Ok(settingsDao.loadSprints.getOrElse(JsArray()));
  }

  def dayAndPrecisionOptions = Action { implicit request =>
    Ok(settingsDao.loadDayAndPrecision.getOrElse(SettingsDao.DefaultDaysAndPrecisionOptions))
  }

  def mainPage = Action { implicit request =>
    Ok(views.html.settings())
  }

  def saveSettings = Action(parse.json) { implicit request =>
    val data = request.body
    settingsDao.saveEmployees(data \ Employees)
    settingsDao.saveHolidays(data \ Holidays)
    settingsDao.saveSprints(data \ Sprints)
    settingsDao.saveDayAndPrecision(data \ DaysAndPrecisionOptions)
    Ok(views.html.settings())
  }

}


