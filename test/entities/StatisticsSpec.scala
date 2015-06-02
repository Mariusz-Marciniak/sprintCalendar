package entities

import com.github.nscala_time.time.Imports._
import config.InMemoryConfiguration
import dao.SettingsDao
import dao.memory.{InMemorySettingsDao, InMemorySprintsDao, InMemoryUserDefaultsDao, InMemoryVacationsDao}
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsObject, JsValue, Json}

import scala.util.{Failure, Success, Try}


@RunWith(classOf[JUnitRunner])
class StatisticsSpec extends Specification {

  import config.JsonImplicits._
  implicit val conf = InMemoryConfiguration

  "sprintsNames" should {
    "return empty list when no sprint is enclosed in statistics range" in {
      Statistics(DateRange(LocalDate.parse("2015-12-13"),LocalDate.parse("2015-12-31"))).sprintsNames must be empty

      Statistics(DateRange(LocalDate.parse("2015-12-17"),LocalDate.parse("2016-01-16"))).sprintsNames must be empty

      Statistics(DateRange(LocalDate.parse("2015-08-17"),LocalDate.parse("2015-09-10"))).sprintsNames must be empty

      Statistics(DateRange(LocalDate.parse("2015-12-15"),LocalDate.parse("2015-12-31"))).sprintsNames must be empty
    }

    "return sprint that exacly fits to range" in {
      val stats = Statistics(DateRange(LocalDate.parse("2015-12-14"),LocalDate.parse("2016-01-01")))
      stats.sprintsNames must haveSize(1)
      stats.sprintsNames must contain("Sprint 1 2015-12-14::2016-01-01")
    }

    "return all sprints in range" in {
      val stats = Statistics(DateRange(LocalDate.parse("2015-12-10"),LocalDate.parse("2016-03-01")))
      stats.sprintsNames must haveSize(2)
      stats.sprintsNames must contain("Sprint 1 2015-12-14::2016-01-01")
      stats.sprintsNames must contain("Sprint 2 2016-01-02::2016-01-18")
    }

  }

  "totalUnitsInSprint" should {
    "return sum of availabilities in chosen sprint no matter what is statistics range" in {
      val sprintData: JsObject = conf.sprintsDao.loadSprintData("Sprint 1 2015-12-14::2016-01-01").get
      Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2015-01-05"))).totalUnitsInSprint(sprintData) must beEqualTo(12)
    }
  }

  "amountOfWorkdaysInWeek" should {
    "return zero when no workdays defined" in {
      val stats = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2015-12-31")))(EmptyConfig)
      stats.amountOfWorkdaysInWeek must beEqualTo(0)
    }
    "return 5 workdays when only Saturday and Sunday are free" in {
      val stats = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2015-12-31")))
      stats.amountOfWorkdaysInWeek must beEqualTo(5)
    }
  }

  "calculateVelocities" should {
    "return empty sequence when no sprint in range" in {
      Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2015-12-31"))).calculateVelocities must be empty
    }
    "return velocities for all sprints days precision" in {
      val expectedVelocities = List(
        VelocityEntry("Sprint 1 2015-12-14::2016-01-01",None,11.08,55.40),
        VelocityEntry("Sprint 2 2016-01-02::2016-01-18",None,9.75,48.75)
      )

      val stats = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-20"))).calculateVelocities

      stats must containTheSameElementsAs(expectedVelocities)
    }
    "return velocities for all sprints hours precision" in {
      val expectedVelocities = List(
        VelocityEntry("Sprint 1 2015-12-14::2016-01-01",Some(BigDecimal("11.08")),BigDecimal("44.32"),BigDecimal("221.60")),
        VelocityEntry("Sprint 2 2016-01-02::2016-01-18",Some(BigDecimal("9.75")),BigDecimal("39.00"),BigDecimal("195.00"))
      )

      val stats = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-20")))(InMemoryHoursPecisionConfiguration).calculateVelocities

      stats must containTheSameElementsAs(expectedVelocities)
    }
  }

  "totalVelocity" should {
    "return summary with zeros when no sprint is in range" in {
      val expectedVelocity = VelocityEntry("Total velocity",None,BigDecimal("0.00"),BigDecimal("0.00"))

      val total = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2015-12-31"))).totalVelocity

      total must beEqualTo(expectedVelocity)
    }

    "return average from all sprints for days precision" in {
      val expectedVelocity = VelocityEntry("Total velocity",None,BigDecimal("10.42"),BigDecimal("52.08"))

      val total = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-20"))).totalVelocity

      total must beEqualTo(expectedVelocity)
    }

    "return average from all sprints for hours precision" in {
      val expectedVelocity = VelocityEntry("Total velocity",Some(BigDecimal("10.42")),BigDecimal("41.66"),BigDecimal("208.30"))

      val total = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-20")))(InMemoryHoursPecisionConfiguration).totalVelocity

      total must beEqualTo(expectedVelocity)
    }

  }

  "globalVelocity" should {
    "be the same no matter what range statistics are using" in {
      val globalEmptyRange = Statistics(DateRange(LocalDate.parse("2011-12-13"), LocalDate.parse("2012-12-31"))).globalVelocity
      val globalIncorrectRange = Statistics(DateRange(LocalDate.parse("2016-12-13"), LocalDate.parse("2015-12-31"))).globalVelocity
      val globalRangeWithOneSprint = Statistics(DateRange(LocalDate.parse("2011-12-13"), LocalDate.parse("2016-01-01"))).globalVelocity
      val globalRangeWithAllSprints = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-18"))).globalVelocity

      globalEmptyRange must beEqualTo(globalIncorrectRange)
      globalEmptyRange must beEqualTo(globalRangeWithOneSprint)
      globalEmptyRange must beEqualTo(globalRangeWithAllSprints)
    }
    "be the same (expect label) as total velocity for all sprints" in {
      val expectedVelocity = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-18"))).totalVelocity

      val globalVelocity = Statistics(DateRange(LocalDate.parse("2011-12-13"), LocalDate.parse("2012-12-31"))).globalVelocity

      globalVelocity.perHour must beEqualTo(expectedVelocity.perHour)
      globalVelocity.perDay must beEqualTo(expectedVelocity.perDay)
      globalVelocity.perWeek must beEqualTo(expectedVelocity.perWeek)
    }

  }

  "employeeVelocity" should {
    "count velocity as average for sprints in which employee participated" in {
      val expectedMaryVelocity = VelocityEntry("Mary velocity",None,BigDecimal("10.42"),BigDecimal("52.08"))
      val maryVelocity = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-18"))).employeeVelocity("Mary")
      maryVelocity must beEqualTo(expectedMaryVelocity)
      val expectedThomasVelocity = VelocityEntry("Thomas velocity",None,BigDecimal("11.08"),BigDecimal("55.40"))
      val thomasVelocity = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-18"))).employeeVelocity("Thomas")
      thomasVelocity must beEqualTo(expectedThomasVelocity)
    }
    "count velocity as average for sprints in which employee participated (with hours)" in {
      val expectedMaryVelocity = VelocityEntry("Mary velocity",Some(BigDecimal("10.42")),BigDecimal("41.66"),BigDecimal("208.30"))
      val maryVelocity = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-18")))(InMemoryHoursPecisionConfiguration).
        employeeVelocity("Mary")
      maryVelocity must beEqualTo(expectedMaryVelocity)
      val expectedThomasVelocity = VelocityEntry("Thomas velocity",Some(BigDecimal("11.08")),BigDecimal("44.32"),BigDecimal("221.60"))
      val thomasVelocity = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-18")))(InMemoryHoursPecisionConfiguration).
        employeeVelocity("Thomas")
      thomasVelocity must beEqualTo(expectedThomasVelocity)
    }
    "be the same for employee no matter what range statistics are using" in {
      val maryVelocity1 = Statistics(DateRange(LocalDate.parse("2015-12-13"), LocalDate.parse("2016-01-18"))).employeeVelocity("Mary")
      val maryVelocity2 = Statistics(DateRange(LocalDate.parse("2015-07-01"), LocalDate.parse("2015-07-01"))).employeeVelocity("Mary")
      maryVelocity1 must beEqualTo(maryVelocity2)
    }
  }

}

object EmptyConfig extends config.Configuration {
  lazy val settingsDao = new SettingsDao {
    val exception = new UnsupportedOperationException("This operation is unsupported")
    def saveEmployees(employees: JsValue): Try[JsValue] = Failure(exception)
    def loadEmployees: Try[JsValue] = Success(Json.obj())
    def loadEmployeesNames: Seq[String] = Seq()
    def saveHolidays(holidays: JsValue): Try[JsValue] = Failure(exception)
    def loadHolidays: Try[JsValue] = Success(Json.obj())
    def saveSprints(sprints: JsValue): Try[JsValue] = Failure(exception)
    def loadSprints: Try[JsValue] = Success(Json.obj())
    def saveDayAndPrecision(sprints: JsValue): Try[JsValue] = Failure(exception)
    def loadDayAndPrecision: Try[JsValue] = Success(Json.obj())
  }
  lazy val vacationsDao = null
  lazy val userDefaultsDao = null
  lazy val sprintsDao = null
}

object InMemoryHoursPecisionConfiguration extends config.Configuration {
  lazy val settingsDao = new InMemorySettingsDao
  lazy val vacationsDao = new InMemoryVacationsDao
  lazy val userDefaultsDao = new InMemoryUserDefaultsDao
  lazy val sprintsDao = new InMemorySprintsDao

  settingsDao.saveDayAndPrecision(
    Json.obj(
      "workdays" -> Json.obj(
        WorkingDays.DaysNames(0) -> false,
        WorkingDays.DaysNames(1) -> true,
        WorkingDays.DaysNames(2) -> true,
        WorkingDays.DaysNames(3) -> true,
        WorkingDays.DaysNames(4) -> true,
        WorkingDays.DaysNames(5) -> true,
        WorkingDays.DaysNames(6) -> false),
      "precision" -> Json.obj(
        "type" -> "hours",
        "perDay" -> 4
      )
    )
  )
}

