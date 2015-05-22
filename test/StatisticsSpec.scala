import config.InMemoryConfiguration
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{Json, JsArray}
import org.joda.time.{Days, DateTimeFieldType}
import com.github.nscala_time.time.Imports._
import entities.{Statistics, DateRange}


@RunWith(classOf[JUnitRunner])
class StatisticsSpec extends Specification {

  implicit val config = InMemoryConfiguration

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
    "return " in {
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

}
