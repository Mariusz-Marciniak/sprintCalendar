package dao

import entities.UserDefaults

import scala.util.Try

trait UserDefaultsDao {
  def saveDefaults(defaults: UserDefaults) : Try[UserDefaults]
  def loadDefaults() : Try[UserDefaults]
}