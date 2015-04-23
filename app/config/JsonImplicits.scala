package config

import play.api.libs.json._

object JsonImplicits {

  implicit def castToJsArray(value: Any): JsArray = {
    value match {
      case v: JsArray => v
      case _ => JsArray(Seq())
    }
  }

  implicit def castToJsString(value: Any): JsString = {
    value match {
      case v: JsString => v
      case _ => JsString("")
    }
  }

  implicit def castToJsObject(value: Any): JsObject = {
    value match {
      case v: JsObject => v
      case _ => JsObject(Seq())
    }
  }

  implicit def wrapJsArray(arr : JsArray): JsArrayWrapper = new JsArrayWrapper(arr)

  implicit def unwrapJsArray(wrp : JsArrayWrapper): JsArray = wrp.jsArray

}

class JsArrayWrapper(_jsArray: JsArray) {

  def jsArray = _jsArray

  final def foreach[U](f: JsValue => U): Unit = {
    def applyFor(index: Int) : JsValue = {
      jsArray(index) match {
        case u: JsUndefined => u
        case v: JsValue => {
          f(v)
          applyFor(index+1)
        }
      }

    }
    applyFor(0)
  }

}