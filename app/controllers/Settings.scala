package controllers

import config.Configuration
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.{JsValue, Json, JsObject}
import play.api.mvc.{Action, Controller}
import java.util.Date;


object Settings extends Controller {
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


  def mainPage = Action {
    Ok(views.html.settings("Settings"))
  }


}

case class Settings(users: JsValue, holidays: JsValue)

class SettingsS(implicit config: Configuration) {
  val Employees = "EMPLOYEES"
  val Holidays = "HOLIDAYS"
  val Sprints = "SPRINTS"

  private val dao = config.dao

  def saveSettings(data: Map[String, JsObject]): Unit = {
    dao.saveEmployees(data(Employees))
    dao.saveHolidays(data(Holidays))
    dao.saveSprints(data(Sprints))
  }

  def loadSettings: Map[String, JsObject] = Map(
    Employees -> dao.loadEmployees,
    Holidays -> dao.loadHolidays,
    Sprints -> dao.loadSprints
  )

  def parseToDate(date: String): Date = config.AppDateFormat.parse(date)

  def formatDate(date: Date): String = config.AppDateFormat.format(date)

}