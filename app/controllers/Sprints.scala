package controllers

import com.github.nscala_time.time.Imports._
import dao.SettingsDao
import entities.{DateRange, EmployeeInSprint, Statistics, WorkingDays}
import play.api.Routes
import play.api.libs.json._
import play.api.mvc.{Action, Controller}


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

  def saveSprintData(sprintId: String) = Action(parse.json) { implicit request => {
      sprintsDao.saveSprintData(sprintId, request.body)
      loadSprintData(sprintId)
    }
  }

  def mainPage = Action { implicit request => {
      Ok(views.html.sprints())
    }
  }

  def sprints = Action { implicit request => {
      Ok(settingsDao.loadSprints.getOrElse(JsArray()))
    }
  }

  def sprintData(sprintId: String) = Action { implicit request =>
    loadSprintData(sprintId)
  }

  private def loadSprintData(sprintId:String) = {
    val sprint = castToJsArray(settingsDao.loadSprints.getOrElse(JsArray())).findRow("label", sprintId)
    if (sprint.isDefined) {
      val sprintDataOption = sprintsDao.loadSprintData(sprintId).toOption
      val fromDate = LocalDate.parse(castToJsString(sprint.get \ "from").value)
      val toDate = LocalDate.parse(castToJsString(sprint.get \ "to").value)
      val workingDays = CustomUtil.workingDaysWithoutHolidays(DateRange(fromDate,toDate))
      val confirmed = castToJsBoolean(sprintDataOption.getOrElse(Json.obj("confirmed"->false)) \ "confirmed").value

      if(confirmed) {
        val employeeCapacity = readEmployeesCapacities(sprintDataOption)
        Ok(views.html.components.sprintpanel(sprintId, confirmed, storyPoints(sprintDataOption), employeeCapacity))
      } else {
        val employeeCapacity = settingsDao.loadEmployeesNames map {
          case employee => calculateEmployeeCapacity(employee, workingDays, sprintDataOption)
        }
        Ok(views.html.components.sprintpanel(sprintId, confirmed, storyPoints(sprintDataOption), employeeCapacity))
      }
    } else NotFound
  }

  private def readEmployeesCapacities(sprintDataOption: Option[JsValue]):Seq[EmployeeInSprint] = {
    def read(entries: JsArray) :Seq[EmployeeInSprint] = {
      entries map {case v =>
        EmployeeInSprint(
          castToJsString(v \ "employee").value,
          castToJsNumber(v \ "availability").value.intValue(),
          castToJsNumber(v \ "maxAvailability").value.intValue(),
          0)
      }
    }

    sprintDataOption match {
      case Some(sprintData) => read(castToJsArray(sprintData \ "workload"))
      case None => Seq()
    }
  }

  private def calculateEmployeeCapacity(employee: String, workingDays: WorkingDays, sprintDataOption: Option[JsValue]): EmployeeInSprint = {
    import entities.WorkingDays._

    val maxAvailability = workingDays.filterEmployeeVacations(
      vacationsFromJsArray(vacationsDao.loadVacations(employee).getOrElse(JsArray()))
    ).dates.size * hoursMultiplier
    val velocity = Statistics().employeeVelocity(employee)
    sprintDataOption match {
      case Some(sprintData) =>
        EmployeeInSprint(employee, availability(employee, sprintData, maxAvailability),maxAvailability, velocity.perHour.getOrElse(velocity.perDay))
      case None =>
        EmployeeInSprint(employee, maxAvailability, maxAvailability, velocity.perHour.getOrElse(velocity.perDay))
    }

  }

  private def storyPoints(inSprint: Option[JsValue]) : Int = {
    inSprint match {
      case Some(sprintData) => {
        try {
          castToJsNumber(sprintData \ "storyPoints").value.toInt
        } catch {
          case e:NumberFormatException => 0
        }
      }
      case None => 0
    }
  }

  private def availability(employee: String, inSprint: JsValue, maximum: Int) : Int = {
    try {
      castToJsArray(inSprint \ "workload").findRow("employee",employee) match {
        case Some(v) => castToJsNumber(v \ "availability").value.toInt
        case None => maximum
      }
    } catch {
      case e:NumberFormatException => maximum
    }
  }

  private lazy val hoursMultiplier: Int = {
    val settings = settingsDao.loadDayAndPrecision.getOrElse(SettingsDao.DefaultDaysAndPrecisionOptions)
    val precisionType = castToJsString(settings \ "precision" \ "type")
    if("hours".equals(precisionType.value))
      castToJsString(settings \ "precision" \ "perDay").value.toInt
    else
      1
  }


  private def sprintsNames(): Seq[String] = {
    val sprints = settingsDao.loadSprints.getOrElse(JsArray())
    (sprints \\ "name") map {
      castToJsString(_).value
    }
  }
}
