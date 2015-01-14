import spray.json._
import spray.json.DefaultJsonProtocol
import spray.httpx.unmarshalling._
import spray.httpx.marshalling._
import spray.http._
import HttpCharsets._
import MediaTypes._
import spray.util._


import DefaultJsonProtocol._ // if you don't supply your own Protocol (see below)

object UserDefinedClassJsonWorksheet  {

  class Color(val name: String, val red: Int, val green: Int, val blue: Int) {
    override def toString = s"name: $name, red: $red, green: $green, blue: $blue"

    override def equals(o: Any) = o match {
      case that: Color => that.name == this.name && that.red == this.red && that.green == this.green && that.blue == this.blue
      case _ => false
    }

    override def hashCode = name.toUpperCase.hashCode
  }

  object MyJsonProtocol extends DefaultJsonProtocol {
    //    implicit object ColorJsonFormat extends RootJsonFormat[Color] {
    //      def write(c: Color) =
    //        JsArray(JsString(c.name), JsNumber(c.red), JsNumber(c.green), JsNumber(c.blue))
    //
    //      def read(value: JsValue) = value match {
    //        case JsArray(Vector(JsString(name), JsNumber(red), JsNumber(green), JsNumber(blue))) =>
    //          new Color(name, red.toInt, green.toInt, blue.toInt)
    //        case _ => deserializationError("Color expected")
    //      }
    //    }

    implicit object ColorJsonFormat extends RootJsonFormat[Color] {
      def write(c: Color) = JsObject(
        "name" -> JsString(c.name),
        "red" -> JsNumber(c.red),
        "green" -> JsNumber(c.green),
        "blue" -> JsNumber(c.blue)
      )
      def read(value: JsValue) = {
        value.asJsObject.getFields("name", "red", "green", "blue") match {
          case Seq(JsString(name), JsNumber(red), JsNumber(green), JsNumber(blue)) =>
            new Color(name, red.toInt, green.toInt, blue.toInt)
          case _ => throw new DeserializationException("Color expected")
        }
      }
    }

  }

  import MyJsonProtocol._
  val json = new Color("CadetBlue", 95, 158, 160).toJson
  val color = json.convertTo[Color]


  import spray.httpx.SprayJsonSupport._

  val body = HttpEntity(
    contentType = ContentType(`application/json`, `UTF-8`),
    string =
      """|{
        |  "name": "CadetBlue",
        |  "red": 95,
        |  "green": 158,
        |  "blue": 160
        |}""".stripMarginWithNewline("\n")
  )

  marshal(color)
  Right(body)
  marshal(color) == Right(body)
  body.as[Color] == Right(color)




}


