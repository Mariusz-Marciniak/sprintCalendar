package dao

import java.io.{FileWriter, BufferedWriter}

import play.api.libs.json.{JsValue, Json, JsArray}

import scala.io.{BufferedSource, Source}
import scala.util.{Try,Success,Failure}

package object file {
  def using[A, B <: { def close(): Unit }](_try: Try[B])(f: B => A): Try[A] = {
    _try match {
      case Success(closeable) => {
          try {
            Success(f(closeable))
          } catch {
            case e:Exception => Failure(e)
          } finally {
            closeable.close()
          }
      }
      case Failure(e) => Failure(e)
    }
  }

  def save(destFile: String, data: JsValue): Try[JsValue] =  {
    val in = new BufferedWriter(new FileWriter(destFile))
    using(Try(in))(writeToFile(data))
  }

  def load(srcFile: String) : Try[JsValue] = using(Try(Source.fromFile(srcFile)))(loadFromFile)

  def writeToFile(data: JsValue)(stream: BufferedWriter): JsValue = {
    stream.write(Json.stringify(data))
    stream.flush()
    data
  }

  def loadFromFile(stream: BufferedSource): JsValue = {
    Json.parse(stream mkString)
  }

}