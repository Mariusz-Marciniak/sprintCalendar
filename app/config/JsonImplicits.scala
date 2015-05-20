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

  implicit def castToJsNumber(value: Any): JsNumber = {
    value match {
      case v: JsNumber => v
      case _ => JsNumber(0)
    }
  }

  implicit def wrapJsArray(arr : JsArray): JsArrayWrapper = new JsArrayWrapper(arr)

  implicit def unwrapJsArray(wrp : JsArrayWrapper): JsArray = wrp.jsArray

}

class JsArrayWrapper(_jsArray: JsArray) {

  def jsArray = _jsArray

  final def foreach(f: JsValue => Unit): Unit = {
    def applyFor(index: Int) : Unit = {
      jsArray(index) match {
        case _:JsUndefined => {}
        case v: JsValue => {
          f(v)
          applyFor(index+1)
        }
      }
    }
    applyFor(0)
  }

  final def map[A](f: JsValue => A): Seq[A] = {
    def convert(index: Int, seq: Seq[A]) : Seq[A] = {
      jsArray(index) match {
        case u: JsUndefined => seq
        case v: JsValue => {
          convert(index+1,seq :+ f(v))
        }
      }

    }
    convert(0, Seq())
  }

  final def findRow(key : String, value: JsValue) : Option[JsValue] = {
    val index = jsArray \\ key indexWhere(_.equals(value))
    if(index >= 0)
      Some(jsArray(index))
    else
      None
  }

  final def findRow(key : String, value: String) : Option[JsValue] = findRow(key, JsString(value))

}

