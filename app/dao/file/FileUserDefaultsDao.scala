package dao.file

import dao.{UserDefaultsDao, VacationsDao}
import entities.UserDefaults
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.util.{Success, Failure, Try}

class FileUserDefaultsDao extends UserDefaultsDao {
  val DefaultsFile = "data/defaults.json"
  implicit val UserDefaultsFormat = (
    (__ \ "timelineFrom").format[String] and
    (__ \ "timelineTo").format[String]
  )(UserDefaults.apply, unlift(UserDefaults.unapply))

  private def convertJsResultToTry[A](res: JsResult[A]):Try[A] = {
    res match {
      case JsSuccess(v, jsPath) => Success(v)
      case JsError(jsPathErrors) => Failure(new UnsupportedOperationException)
    }
  }

  override def saveDefaults(defaults: UserDefaults): Try[UserDefaults] = {
    save(DefaultsFile, Json.toJson(defaults))
    Success(defaults)
  }

  override def loadDefaults(): Try[UserDefaults] = {
    load(DefaultsFile) match {
      case Success(d) => convertJsResultToTry(Json.fromJson[UserDefaults](d))
      case Failure(e) => Failure(e)
    }
  }

}
