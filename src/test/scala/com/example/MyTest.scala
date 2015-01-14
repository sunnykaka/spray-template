package com.example

import org.scalatest.{Matchers, FlatSpecLike}
import spray.http._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._
import spray.util._


class MyTest extends FlatSpecLike with Matchers {

  val `application/vnd.acme.person` = MediaTypes.register(MediaType.custom("application/vnd.acme.person"))


  "MyTest" should "test" in {

    marshal(Person("Bob", "Parr", 32)) shouldBe Right(HttpEntity(`application/vnd.acme.person`, "Person: Bob, Parr, 32"))

    HttpEntity(`application/vnd.acme.person`, "Person: Bob, Parr, 32").as[Person] shouldBe Right(Person("Bob", "Parr", 32))

    HttpEntity(`application/vnd.acme.person`, "").as[Person].isLeft shouldBe true
  }

  case class Person(name: String, firstName: String, age: Int)

  object Person {
    implicit val PersonMarshaller: Marshaller[Person] =
      Marshaller.of[Person](`application/vnd.acme.person`) { (value, contentType, ctx) =>
        val Person(name, first, age) = value
        val string = "Person: %s, %s, %s".format(name, first, age)
        ctx.marshalTo(HttpEntity(contentType, string))
      }

//    implicit val PersonUnmarshaller: Unmarshaller[Person] =
//      Unmarshaller[Person](`application/vnd.acme.person`) {
//        case HttpEntity.NonEmpty(contentType, data) =>
//          // unmarshal from the string format used in the marshaller example
//          val Array(_, name, first, age) =
//            data.asString.split(":,".toCharArray).map(_.trim)
//          Person(name, first, age.toInt)
//
//        // if we had meaningful semantics for the HttpEntity.Empty
//        // we could add a case for the HttpEntity.Empty:
//        // case HttpEntity.Empty => ...
//      }

    implicit val SimplerPersonUnmarshaller: Unmarshaller[Person]  =
      Unmarshaller.delegate[String, Person](`application/vnd.acme.person`) { string =>
        val Array(_, name, first, age) = string.split(":,".toCharArray).map(_.trim)
        Person(name, first, age.toInt)
      }


  }

}


