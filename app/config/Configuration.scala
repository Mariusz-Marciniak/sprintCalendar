package config

import dao.file.{FileSprintsDao, FileUserDefaultsDao, FileSettingsDao, FileVacationsDao}
import dao.memory.{InMemorySprintsDao, InMemoryUserDefaultsDao, InMemorySettingsDao, InMemoryVacationsDao}
import dao.{SprintsDao, UserDefaultsDao, SettingsDao, VacationsDao}
import org.joda.time.format.DateTimeFormat

trait Configuration {
  val settingsDao: SettingsDao
  val vacationsDao: VacationsDao
  val userDefaultsDao: UserDefaultsDao
  val sprintsDao: SprintsDao
}

object Configuration {
  val AppDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  implicit val configuration = InMemoryConfiguration
}

object InMemoryConfiguration extends Configuration {
  lazy val settingsDao = new InMemorySettingsDao
  lazy val vacationsDao = new InMemoryVacationsDao
  lazy val userDefaultsDao = new InMemoryUserDefaultsDao
  lazy val sprintsDao = new InMemorySprintsDao
}

object FileConfiguration extends Configuration {
  lazy val settingsDao = new FileSettingsDao
  lazy val vacationsDao = new FileVacationsDao
  lazy val userDefaultsDao = new FileUserDefaultsDao
  lazy val sprintsDao = new FileSprintsDao
}

