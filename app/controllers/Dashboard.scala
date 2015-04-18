package controllers

import play.api.mvc.{Action, Controller}

object Dashboard extends Controller {

  def mainPage = Action {
    Ok(views.html.dashboard())
  }

}
