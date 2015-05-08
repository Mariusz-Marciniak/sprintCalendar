import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{Json, JsArray}
import org.joda.time.{Days, DateTimeFieldType}
import com.github.nscala_time.time.Imports._


@RunWith(classOf[JUnitRunner])
class WorkingDaysSpec extends Specification {

  import entities.WorkingDays._

  val holidaysJson = Json.arr(
    Json.obj(
      "date" -> "2015-11-23",
      "unused" -> "additional unused value"
    ),
    Json.obj(
      "date" -> "07-12"
    )
  )

  val fromDate = LocalDate.parse("2015-01-01")
  val toDate = LocalDate.parse("2016-12-31")

  val unsupportedHolidays = Json.arr(Json.obj("incorrect" -> true))

  val completePartialDate = new Partial(
    Array(DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()),
    Array(2015, 11, 23))

  val noYearPartialDate = new Partial(
    Array(DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()),
    Array(7, 12)
  )
  
  val workdaysJson = Json.obj(
    "workdays" -> Json.obj(
      "Monday" -> true,
      "Tuesday" -> true,
      "Wednesday" -> true,
      "Saturday" -> false,
      "Sunday" -> false),
    "otherdata" -> Json.obj(
      "Monday" -> false,
      "Sunday" -> true)
  )

  "holidaysFromJsArray" should {
    "return empty sequence for no holidays" in {
      holidaysFromJsArray(JsArray()) must be empty
    }

    "return dates from JSON" in {
      val holidays = holidaysFromJsArray(holidaysJson)
      holidays must haveSize(2)
      holidays must containAllOf(Seq(completePartialDate, noYearPartialDate))
    }

    "throw exception for unsupported partial" in {
      holidaysFromJsArray(unsupportedHolidays) must throwA[IllegalArgumentException]
    }
  }

  "compareDatePartials" should {
    "return zero for the same partials" in {
      compareDatePartials(completePartialDate, completePartialDate) mustEqual(0)
      compareDatePartials(noYearPartialDate, noYearPartialDate) mustEqual(0)
    }

    "return positive value if first is after second" in {
      compareDatePartials(completePartialDate, completePartialDate.minus(Days.ONE)) must beGreaterThan(0)
      compareDatePartials(noYearPartialDate, noYearPartialDate.minus(Days.ONE)) must beGreaterThan(0)
    }

    "return negative value if first is before second" in {
      compareDatePartials(completePartialDate, completePartialDate.plus(Days.ONE)) must beLessThan(0)
      compareDatePartials(noYearPartialDate, noYearPartialDate.plus(Days.ONE)) must beLessThan(0)
    }

    "return zero for the partial with year and without, when day and month are the same" in {
      val tmpPartial = new Partial(
        Array(DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth()),
        Array(1900, noYearPartialDate.get(DateTimeFieldType.monthOfYear()), noYearPartialDate.get(DateTimeFieldType.dayOfMonth())))
      compareDatePartials(noYearPartialDate, tmpPartial) mustEqual(0)
    }

    "ignore year field when partial with year and without are compared" in {
      compareDatePartials(noYearPartialDate, completePartialDate) must beLessThan(0)
    }

    "return negative/positive value respectively to arguments order" in {
      compareDatePartials(noYearPartialDate, completePartialDate) must beLessThan(0)
      compareDatePartials(completePartialDate, noYearPartialDate) must beGreaterThan(0)
    }
  }

  "holidaysInRange" should {
    "return empty sequence if no element was passed" in {
      holidaysInRange(Seq(), fromDate, toDate) must be empty
    }

    "return partial with year when enclosed in range" in {
      val datesForPartialWithYear = holidaysInRange(Seq(completePartialDate), fromDate, toDate)
      datesForPartialWithYear must haveSize(1)
      datesForPartialWithYear must contain(completePartialDate)
    }

    "return partial with year when range is defined exacly for this day" in {
      val date = new LocalDate(
        completePartialDate.get(DateTimeFieldType.year()),
        completePartialDate.get(DateTimeFieldType.monthOfYear()),
        completePartialDate.get(DateTimeFieldType.dayOfMonth()))
      val datesForPartialWithYear = holidaysInRange(Seq(completePartialDate), date, date)
      datesForPartialWithYear must haveSize(1)
      datesForPartialWithYear must contain(completePartialDate)
    }

    "return partial without year when enclosed in range" in {
      val datesForPartialWithYear = holidaysInRange(Seq(noYearPartialDate), fromDate, toDate)
      datesForPartialWithYear must haveSize(1)
      datesForPartialWithYear must contain(noYearPartialDate)
    }

    "return partial without year when range is defined exacly for this day" in {
      val date = new LocalDate(
        2018,
        noYearPartialDate.get(DateTimeFieldType.monthOfYear()),
        noYearPartialDate.get(DateTimeFieldType.dayOfMonth()))
      val datesForPartialWithYear = holidaysInRange(Seq(noYearPartialDate), date, date)
      datesForPartialWithYear must haveSize(1)
      datesForPartialWithYear must contain(noYearPartialDate)
    }

    "return empty sequence when partials are not enclosed in range" in {
      val datesForPartialWithYear = holidaysInRange(Seq(completePartialDate, noYearPartialDate), fromDate, fromDate.plusMonths(3))
      datesForPartialWithYear must be empty
    }
  }

  "workdaysFromJsObject" should {
    "throw exception if object doesn't contain workdays property" in {
      workdaysFromJsObject(Json.obj()) must throwA[IllegalArgumentException]
    }

    "treat all days as not working days by default" in {
      workdaysFromJsObject(Json.obj("workdays" -> true)) must be empty

    }
  }

}
