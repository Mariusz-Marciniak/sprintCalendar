package entities

import com.github.nscala_time.time.Imports._
import config.Configuration._

object UserDefaults {
  def apply(): UserDefaults = {
    val today = DateTime.now
    new UserDefaults(
      configuration.AppDateFormatter.print(today.day(1)),
      configuration.AppDateFormatter.print(today.day(1).plusMonths(2).minusDays(1))
    )
  }
}

case class UserDefaults(timelineDateFrom: String, timelineDateTo: String)


