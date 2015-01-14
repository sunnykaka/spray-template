package com.example

import akka.actor.Actor.Receive
import akka.actor.{ActorRef, Actor, ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http.GetStats
import spray.can.server.Stats
import scala.concurrent.duration._

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[MyServiceActor], "demo-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
//  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)

//  val actor =

  system.actorOf(Props(new Actor() {

    var httpListener: Option[ActorRef] = None

    import context.dispatcher
    context.system.scheduler.schedule(0 millisecond, 5 second) {
      httpListener map (_ ! GetStats)
      //      server ? GetStats onComplete(r => println("get stats result: " + r))
    }

    IO(Http) ! Http.Bind(system.actorOf(Props(classOf[MyActorListener])), interface = "localhost", port = 8080)

    override def receive: Receive = {
      case bound: Http.Bound =>
        println("http bound: " + bound)
        httpListener = Some(sender())

      case stats: Stats =>
        println("get stats: " + stats)
    }
  }))
//  val f =
//  import system.dispatcher
//  f onComplete {r =>
//    println("bind result: " + r)
//    r.
//  }
}
