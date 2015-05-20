package controllers

import entities.WorkingDays
import play.api.Routes
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import com.github.nscala_time.time.Imports._
import entities.DateRange

object Sprints extends Controller {

  import config.Configuration._
  import config.JsonImplicits._

  private val settingsDao = configuration.settingsDao
  private val vacationsDao = configuration.vacationsDao
  private val sprintsDao = configuration.sprintsDao

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
      val sprintData = sprintsDao.loadSprintData(sprintId).getOrElse(Json.obj())
      val fromDate = LocalDate.parse(castToJsString(sprint.get \ "from").value)
      val toDate = LocalDate.parse(castToJsString(sprint.get \ "to").value)
      val workingDays = workingDaysWithoutHolidays(DateRange(fromDate,toDate))
      val multiplier = hoursMultiplier

      val employeeCapacity = settingsDao.loadEmployeesNames map {
        case employee => {
          val maxAvailability = workingDays.filterEmployeeVacations(
            vacationsFromJsArray(vacationsDao.loadVacations(employee).getOrElse(JsArray()))
          ).dates.size * multiplier

          (employee,availability(employee, sprintData, maxAvailability),maxAvailability)
        }
      }
      Ok(views.html.components.sprintpanel(sprintId, storyPoints(sprintData),  employeeCapacity))
    } else NotFound
  }

  private def storyPoints(inSprint: JsValue) : Int = {
    try {
      castToJsNumber(inSprint \ "storyPoints").value.toInt
    } catch {
      case e:NumberFormatException => 0
    }
  }

  private def availability(employee: String, inSprint: JsValue, maximum: Int) : Int = {
    try {
      println(castToJsArray(inSprint \ "workload"))
      println(castToJsArray(inSprint \ "workload").findRow("employee",employee))
      castToJsArray(inSprint \ "workload").findRow("employee",employee) match {
        case Some(v) => castToJsNumber(v \ "availability").value.toInt
        case None => maximum
      }

    } catch {
      case e:NumberFormatException => maximum
    }
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
