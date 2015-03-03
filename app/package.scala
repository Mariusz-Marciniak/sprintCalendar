import scala.util.{Try,Success,Failure,Either}

package object calendar {
  def using[A, B <: { def close(): Unit }](_try: Try[B])(f: B => A): Either[A, Throwable] = {
    _try match {
      case Success(closeable) => {
          try { Left(f(closeable)) } finally { closeable.close() }
      }
      case Failure(e) => {
        Right(e)
      }
    }
  }
}