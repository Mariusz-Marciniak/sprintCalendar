package controllers

import config.Configuration
import play.api.libs.json.JsObject
import play.api.mvc.{Action, Controller}
import java.util.Date;


object Settings extends Controller {
  def mainPage = Action{
    Ok("aqq")
  }
}


class Settings(implicit config: Configuration) {
  val Employees = "EMPLOYEES"
  val Holidays = "HOLIDAYS"
  val Sprints = "SPRINTS"

  private val dao = config.dao

  def saveSettings(data: Map[String,JsObject]) : Unit = {
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