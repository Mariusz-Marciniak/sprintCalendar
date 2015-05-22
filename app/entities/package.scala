import com.github.nscala_time.time.Imports._
import org.joda.time.ReadablePartial

package object entities {
  type Holidays = Seq[ReadablePartial]
  type Dates = Seq[LocalDate]
}
