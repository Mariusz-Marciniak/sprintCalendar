import play.api.libs.json._

package object controllers {

  implicit def convertToJsArray(value: Any): JsArray = {
    try {
      convertTo[JsArray](value)
    } catch {
      case e:ClassCastException => JsArray(Seq())
    }
  }

  implicit def convertToJsString(value: Any): JsString = {
    try {
      convertTo[JsString](value)
    } catch {
      case e:ClassCastException => JsString("")
    }
  }

  implicit def convertToJsObject(value: Any): JsObject = {
    try {
      convertTo[JsObject](value)
    } catch {
      case e:ClassCastException => JsObject(Seq())
    }
  }

  implicit def wrapJsArray(arr : JsArray): JsArrayWrapper = new JsArrayWrapper(arr)

  implicit def unwrapJsArray(wrp : JsArrayWrapper): JsArray = wrp.jsArray

  def convertTo[A](value: Any): A = {
    value match {
      case v: A => v
      case _ => throw new ClassCastException(s"Value $value is not of required type")
    }
  }

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