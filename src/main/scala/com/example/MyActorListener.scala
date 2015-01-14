package com.example

import akka.actor.{ActorRef, Actor}
import akka.actor.Actor.Receive
import spray.can.Http
import spray.can.Http.GetStats
import spray.can.server.Stats
import spray.http.{HttpEntity, HttpResponse, HttpResponsePart, HttpRequestPart}
import scala.concurrent.duration._

/**
 * User: Liub
 * Date: 2015/1/4
 */
class MyActorListener extends Actor{

//  var httpListener: Option[ActorRef] = None

//  override def preStart(): Unit = {
//    import context.dispatcher
//    context.system.scheduler.schedule(0 millisecond, 500 millisecond) {
//      httpListener map (ref => {ref.tell(GetStats, self); println("here")})
//    }
//  }


  override def receive: Receive = {

    case Http.Connected(remoteAddress, localAddress) =>
//      httpListener = Some(sender())
      println(s" connected! remoteAddress: $remoteAddress, localAddress: $localAddress")
      sender ! Http.Register(self)

    case req: HttpRequestPart =>
      println(" request info!" + req)
      sender ! HttpResponse(200, HttpEntity("你好"))

    case closed: Http.ConnectionClosed =>
//      httpListener = None
      println(" closed!" + closed)

    case stats: Stats =>
      println(" stats info: " + stats)

    case msg => println("unknown: " + msg)
  }
}
