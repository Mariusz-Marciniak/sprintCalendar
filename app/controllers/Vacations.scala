package controllers

import play.api.mvc.{Action, Controller};


object Vacations extends Controller {
  def mainPage = Action { implicit request =>
    Ok(views.html.vacations())
  }
}
