package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}

/**
 * @author mariusz marciniak
 */
object Dashboard extends Controller {

  case class User(name: String)

  val tmpForm: Form[User] = Form(
    mapping(
      "login" -> nonEmptyText
    ) (User.apply)(User.unapply)
  )

  def register = Action {
    Ok("xxx")
  }

  def index = Action {
    Ok(views.html.dashboard("Dashboard",tmpForm))
  }

}
