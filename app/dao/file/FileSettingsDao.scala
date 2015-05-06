package dao.file

import dao.SettingsDao
import play.api.libs.json._

import scala.util.{Failure, Success, Try}

class FileSettingsDao extends SettingsDao {
  val EmployeesFile = "data/employees.json"
  val HolidaysFile = "data/holidays.json"
  val SprintsFile = "data/sprints.json"
  val DayAndPrecisionFile = "data/dpoptions.json"

  override def saveEmployees(employees: JsValue): Try[JsValue] = {
    println(s"saving employees to file: $employees")
    save(EmployeesFile, employees)
  }

  override def loadEmployees: Try[JsValue] = load(EmployeesFile)

  override def loadEmployeesNames: Seq[String] = {
    loadEmployees match {
      case Success(data) => {
        (data \\ "label").map {
          case s:JsString  =>  s.value
          case v:JsValue  => "Incorrect data"
        }
      }
      case Failure(_) => Seq()
    }
  }

  override def saveHolidays(holidays: JsValue): Try[JsValue] = {
    println(s"saving holidays: $holidays")
    save(HolidaysFile, holidays)
  }

  override def loadHolidays: Try[JsValue] = load(HolidaysFile)

  override def saveSprints(sprints: JsValue): Try[JsValue] = {
    println(s"saving sprints: $sprints")
    save(SprintsFile, sprints)
  }
  override def loadSprints: Try[JsValue] = load(SprintsFile)

  override def saveDayAndPrecision(daysAndPrecisionOptions: JsValue): Try[JsValue] = {
    println(s"saving days and precisions options: $daysAndPrecisionOptions")
    save(DayAndPrecisionFile, daysAndPrecisionOptions)
  }

  override def loadDayAndPrecision: Try[JsValue] = load(DayAndPrecisionFile)
}
