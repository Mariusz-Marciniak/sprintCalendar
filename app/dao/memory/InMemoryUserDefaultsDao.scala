package dao.memory

import dao.UserDefaultsDao
import entities.UserDefaults

import scala.util.{Success, Try}

class InMemoryUserDefaultsDao extends UserDefaultsDao {

  private var defaults: UserDefaults = UserDefaults()

  override def saveDefaults(defaults: UserDefaults): Try[UserDefaults] = {
    this.defaults = defaults
    Success(this.defaults)
  }

  override def loadDefaults(): Try[UserDefaults] = {
    Success(this.defaults)
  }
}
