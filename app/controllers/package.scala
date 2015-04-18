import play.api.libs.json.{JsArray, JsValue}

package object controllers {

  def convertToJsArray(value: JsValue): JsArray = {
    value match {
      case v: JsArray => v
      case _ => JsArray(Seq())
    }
  }

}