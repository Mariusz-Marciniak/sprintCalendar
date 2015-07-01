package controllers

import entities._
import play.api.Routes
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.collection.mutable.ArrayBuffer

object Dashboard extends Controller {
  import config.Configuration._
  import config.JsonImplicits._

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
        "entries" -> entries.map(entry => entry + ("employee" -> Json.toJson(employee)))
      )
    }
    Ok(Json.toJson(timelines));
  }

  private def employeeWorkdays(range: DateRange): Seq[Tuple2[String, Int]] = {
    import entities.WorkingDays._
    val workingDays = CustomUtil.workingDaysWithoutHolidays(range)
    settingsDao.loadEmployeesNames.map { employee =>
      (employee, workingDays.filterEmployeeVacations(
        vacationsFromJsArray(vacationsDao.loadVacations(employee).getOrElse(JsArray()))
      ).dates.size)
    }
  }


  def mainPage = Action {
    val defaults = loadDefaults()
    val range = DateRange(AppDateFormatter.parseLocalDate(defaults.timelineDateFrom), AppDateFormatter.parseLocalDate(defaults.timelineDateTo))
    Ok(views.html.dashboard(Statistics(range), employeeWorkdays(range)))
  }

}

