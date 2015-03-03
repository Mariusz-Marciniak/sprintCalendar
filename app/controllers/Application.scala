package controllers

import play.api.mvc._

object Application extends Controller {

  def index = Settings.mainPage

}