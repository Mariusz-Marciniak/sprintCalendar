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
      "date" -> "07-13"
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
    Array(7, 13)
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

    "return exact date when partial with year is enclosed in range" in {
      val date = new LocalDate(
        completePartialDate.get(DateTimeFieldType.year()),
        completePartialDate.get(DateTimeFieldType.monthOfYear()),
        completePartialDate.get(DateTimeFieldType.dayOfMonth()))
      val datesForPartialWithYear = holidaysInRange(Seq(completePartialDate), fromDate, toDate)

      datesForPartialWithYear must haveSize(1)
      datesForPartialWithYear must contain(date)
    }

    "have inclusive range boundaries" in {
      val date = new LocalDate(
        completePartialDate.get(DateTimeFieldType.year()),
        completePartialDate.get(DateTimeFieldType.monthOfYear()),
        completePartialDate.get(DateTimeFieldType.dayOfMonth()))
      val datesForPartialWithYear = holidaysInRange(Seq(completePartialDate), date, date)

      datesForPartialWithYear must haveSize(1)
      datesForPartialWithYear must contain(date)
    }

    "return dates for each year when partial without year is enclosed in range" in {
      val datesForPartialWithYear = holidaysInRange(Seq(noYearPartialDate), fromDate, toDate)
      datesForPartialWithYear must haveSize(2)
      datesForPartialWithYear must contain(LocalDate.parse("2015-07-13"),LocalDate.parse("2016-07-13"))
    }

    "have inclusive range boundaries for partials without year" in {
      val date = new LocalDate(
        2018,
        noYearPartialDate.get(DateTimeFieldType.monthOfYear()),
        noYearPartialDate.get(DateTimeFieldType.dayOfMonth()))
      val datesForPartialWithYear = holidaysInRange(Seq(noYearPartialDate), date, date)
      datesForPartialWithYear must haveSize(1)
      datesForPartialWithYear must contain(LocalDate.parse("2018-07-13"))
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
      workdaysFromJsObject(Json.obj("workdays" -> Json.obj())) must be empty
    }

    "take days only from workdays property" in {
      val workdays = workdaysFromJsObject(workdaysJson)
      workdays must haveSize(3)
      workdays must containTheSameElementsAs(Seq(1,2,3))
    }

    "return value from 0(Sunday) to 6(Saturday)" in {
      workdaysFromJsObject(Json.obj("workdays" -> Json.obj("Sunday" -> true))) must containTheSameElementsAs(Seq(0))
      workdaysFromJsObject(Json.obj("workdays" -> Json.obj("Monday" -> true))) must containTheSameElementsAs(Seq(1))
      workdaysFromJsObject(Json.obj("workdays" -> Json.obj("Tuesday" -> true))) must containTheSameElementsAs(Seq(2))
      workdaysFromJsObject(Json.obj("workdays" -> Json.obj("Wednesday" -> true))) must containTheSameElementsAs(Seq(3))
      workdaysFromJsObject(Json.obj("workdays" -> Json.obj("Thursday" -> true))) must containTheSameElementsAs(Seq(4))
      workdaysFromJsObject(Json.obj("workdays" -> Json.obj("Friday" -> true))) must containTheSameElementsAs(Seq(5))
      workdaysFromJsObject(Json.obj("workdays" -> Json.obj("Saturday" -> true))) must containTheSameElementsAs(Seq(6))
    }
  }

  "convertWeekDayToJoda" should {
    "change index for Sunday from 0 to 7" in {
      convertWeekDayToJoda(0) mustEqual(7)
    }

    "leave unchanged indexes for other days" in {
      convertWeekDayToJoda(1) mustEqual(1)
      convertWeekDayToJoda(2) mustEqual(2)
      convertWeekDayToJoda(3) mustEqual(3)
      convertWeekDayToJoda(4) mustEqual(4)
      convertWeekDayToJoda(5) mustEqual(5)
      convertWeekDayToJoda(6) mustEqual(6)
    }
  }

  "convertWeekDayToJs" should {
    "change index for Sunday from 7 to 0" in {
      convertWeekDayToJs(7) mustEqual(0)
    }

    "leave unchanged indexes for other days" in {
      convertWeekDayToJs(1) mustEqual(1)
      convertWeekDayToJs(2) mustEqual(2)
      convertWeekDayToJs(3) mustEqual(3)
      convertWeekDayToJs(4) mustEqual(4)
      convertWeekDayToJs(5) mustEqual(5)
      convertWeekDayToJs(6) mustEqual(6)
    }
  }

  "workdaysInRange" should {
    "return empty list when no day of week is workday" in {
      workdaysInRange(fromDate, toDate, Seq()).dates must be empty
    }
    "return empty list when range incorrectly defined" in {
      workdaysInRange(toDate, fromDate, Seq(1,2)).dates must be empty
    }
    "return 3 Sundays in 10-24 May 2015" in {
      val testedObject = workdaysInRange(LocalDate.parse("2015-05-08"), LocalDate.parse("2015-05-24"), Seq(0))
      testedObject.dates must haveSize(3)
      testedObject.dates must containTheSameElementsAs(Seq(LocalDate.parse("2015-05-10"), LocalDate.parse("2015-05-17"), LocalDate.parse("2015-05-24")))
    }
    "return 22 workdays April 2015, when only Satruday and Sunday are free" in {
      val testedObject = workdaysInRange(LocalDate.parse("2015-04-01"), LocalDate.parse("2015-04-30"), Seq(1,2,3,4,5))
      testedObject.dates must haveSize(22)
    }
    "return 522 workdays from 2015 to the end 2016, when only Satruday and Sunday are free" in {
      val testedObject = workdaysInRange(fromDate, toDate, Seq(1,2,3,4,5))
      testedObject.dates must haveSize(522)
    }
  }

  "filterHolidays" should {
    "return the same list if there are no holidays" in {
      workdaysInRange(fromDate, toDate, Seq(1,2,3,4,5)).filterHolidays(Seq()).dates must haveSize(522)
    }
    "filter holidays and return new workdays" in {
      val holidays = holidaysInRange(holidaysFromJsArray(holidaysJson), fromDate, toDate)
      workdaysInRange(fromDate, toDate, Seq(1,2,3,4,5)).filterHolidays(holidays).dates must haveSize(519)
    }
  }

}
