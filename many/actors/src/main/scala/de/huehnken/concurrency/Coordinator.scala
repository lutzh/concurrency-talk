package de.huehnken.concurrency

import akka.actor.{ActorRef, Props, Actor}

import scala.collection.mutable.ArrayBuffer

object Coordinator {

  case class Result(value: Int)

  case object StartCommand

  def props(num: Int, startTime: Long) = Props(classOf[Coordinator], num, startTime)
}

class Coordinator(target: Int, startTime: Long) extends Actor {

  import Coordinator._
  import Task.Do

  val actorStore = new Array[ActorRef](target)
  val results = ArrayBuffer[Long]()

  override def preStart() =
    for (i <- 0 until target) {
      actorStore(i) = context.actorOf(Task.props(i+1))
    }


  def receive = {
    case StartCommand =>
      actorStore.foreach(_ ! Do)
    case Result(x) =>
      results += x
      if (results.size == target) {
        println(s"Sum is ${results.sum}, took ${System.currentTimeMillis - startTime} milliseconds")
        context.system.terminate
      }
  }

}
