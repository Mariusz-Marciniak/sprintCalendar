package controllers

import entities.WorkingDays
import play.api.Routes
import play.api.libs.json.JsArray
import play.api.mvc.{Action, Controller}
import com.github.nscala_time.time.Imports._
import entities.DateRange

object Sprints extends Controller {

  import config.Configuration._
  import config.JsonImplicits._

  private val settingsDao = configuration.settingsDao
  private val vacationsDao = configuration.vacationsDao

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("sprintsJsRoutes")(
        routes.javascript.Sprints.saveSprintData,
        routes.javascript.Sprints.sprintData,
        routes.javascript.Sprints.sprints
      )
    ).as("text/javascript")
  }

  def sprintData(sprintId: String) = Action { implicit request =>
    import entities.WorkingDays._
    val sprint = castToJsArray(settingsDao.loadSprints.getOrElse(JsArray())).findRow("label", sprintId)
    if (sprint.isDefined) {
      val fromDate = LocalDate.parse(castToJsString(sprint.get \ "from").value)
      val toDate = LocalDate.parse(castToJsString(sprint.get \ "to").value)
      val workingDays = workingDaysWithoutHolidays(DateRange(fromDate,toDate))
      val multiplier = hoursMultiplier

      val employeeCapacity = settingsDao.loadEmployeesNames map { case employee => (
        employee,
        workingDays.filterEmployeeVacations(
          vacationsFromJsArray(vacationsDao.loadVacations(employee).getOrElse(JsArray()))
        ).dates.size * multiplier)
      }
      Ok(views.html.components.sprintpanel(sprintId, employeeCapacity))
    } else NotFound
  }

  private def hoursMultiplier: Int = {
    val settings = settingsDao.loadDayAndPrecision.getOrElse(settingsDao.DefaultDaysAndPrecisionOptions)
    val precisionType = castToJsString(settings \ "precision" \ "type")
    if("hours".equals(precisionType.value))
      castToJsString(settings \ "precision" \ "perDay").value.toInt
    else
      1
  }

  def workingDaysWithoutHolidays(range: DateRange): WorkingDays = {
    import entities.WorkingDays._
    workdaysInRange(
      range,
      workdaysFromJsObject(settingsDao.loadDayAndPrecision.getOrElse(settingsDao.DefaultDaysAndPrecisionOptions))
    ) filterHolidays(
      holidaysInRange(holidaysFromJsArray(settingsDao.loadHolidays.getOrElse(JsArray())),range)
    )
  }


  def saveSprintData(sprintId: String) = Action(parse.json) { implicit request =>
    Ok("aqq")
  }

  def mainPage = Action { implicit request => {
    Ok(views.html.sprints())
  }
  }

  def sprints = Action { implicit request => {
    Ok(settingsDao.loadSprints.getOrElse(JsArray()))
  }
  }


  private def sprintsNames(): Seq[String] = {
    val sprints = settingsDao.loadSprints.getOrElse(JsArray())
    (sprints \\ "name") map {
      castToJsString(_).value
    }
  }
}
