package de.huehnken.concurrency

import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.scaladsl.ImplicitMaterializer
import akka.util.ByteString
import de.huehnken.concurrency.Coordinator.Result
import org.apache.commons.lang3.StringUtils

import scala.concurrent.Await

object Task {

  def props(key: String, coordinator: ActorRef) = Props(classOf[Task], key, coordinator)

  case class GetDefinition(url: String)
}

class Task(key: String, coordinator: ActorRef) extends Actor with ImplicitMaterializer {

  import Task._
  import akka.pattern.pipe
  import context.dispatcher

  val http = Http(context.system)

  def receive = {
    case GetDefinition(url) =>
      http.singleRequest(HttpRequest(uri = url)) pipeTo self
    case HttpResponse(StatusCodes.OK, headers, entity, _) =>
      entity.dataBytes.runFold(ByteString(""))(_ ++ _) pipeTo self
    case HttpResponse(code, _, _, _) =>
      println("An error occurred.")
    case text: ByteString =>
      coordinator ! Result(key, StringUtils.abbreviate(text.utf8String, 1000))
        // we don't need our http connection anymore. Or the actor.
      http.shutdownAllConnectionPools map { _ => context.stop(self) }
  }
}
