import spray.json._
import DefaultJsonProtocol._ // if you don't supply your own Protocol (see below)

object JsonWorksheet {

  val source = """{ "some": "JSON source" }"""

  val jsonAst = source.parseJson // or JsonParser(source)

  val json = jsonAst.prettyPrint // or .compactPrint

  val jsonAst2 = List(1, 2, 3).toJson

  case class Color(name: String, red: Int, green: Int, blue: Int)

  object MyJsonProtocol extends DefaultJsonProtocol {
    implicit val colorFormat = jsonFormat4(Color)
  }

  import MyJsonProtocol._

  val json2 = Color("CadetBlue", 95, 158, 160).toJson
  val color = json2.convertTo[Color]



  class TempObject(val a: Int, val b: Int) {
    override def toString = s"a: $a, b: $b"
  }

  object TempObject {
    def apply(a:Int, b:Int) = new TempObject(a, b)
    //  def unapply(o: TempObject): Option[Seq[Int]] = Some(Seq(o.a, o.b))
  }

  //case class TempObject(a: Int, b: Int)

  TempObject(1, 2)

  val f: (Int, Int) => TempObject = TempObject.apply
  f(1,2)



  case class NamedList[A](name: String, items: List[A])

  object MyJsonProtocol2 extends DefaultJsonProtocol {
    implicit def namedListFormat[A :JsonFormat] = jsonFormat2(NamedList.apply[A])
  }

  import MyJsonProtocol2.namedListFormat

  val nameListJson = NamedList("张三", List(1,2,3)).toJson
  val nameList = nameListJson.convertTo[NamedList[Int]]

  val nameListStr2 = """{"name":"张三","items":[1,2,3]}"""
  val nameListJson2 = nameListStr2.parseJson
  nameListJson2 == nameListJson

  val nameListStr3 = """{"name":"张三","items":[]}"""
  val nameListJson3 = nameListStr3.parseJson
  val nameList3 = nameListJson3.convertTo[NamedList[Int]]


  case class OptionNamedList[A](name: Option[String], items: List[A])

  object MyJsonProtocol3 extends DefaultJsonProtocol {
    implicit def optionNamedListFormat[A :JsonFormat] = jsonFormat2(OptionNamedList.apply[A])
  }

  import MyJsonProtocol3.optionNamedListFormat

  val optionNameListJson = OptionNamedList(Some("张三"), List(1,2,3)).toJson
  val optionNameList = optionNameListJson.convertTo[OptionNamedList[Int]]

  val optionNameListStr2 = """{"items":[]}"""
  val optionNameListJson2 = optionNameListStr2.parseJson
  val optionNameList2 = optionNameListJson2.convertTo[OptionNamedList[Int]]

}


