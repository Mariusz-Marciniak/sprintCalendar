package dao.file

import org.apache.commons.codec.binary.Base32

trait FileNameConverter {

  def toFilename(identifier:String) : String = {
    val base32codec = new Base32()
    base32codec.encodeAsString(identifier.getBytes())
  }
  def toIdentifier(filename:String) : String = {
    val base32codec = new Base32()
    new String(base32codec.decode(filename))
  }


}
