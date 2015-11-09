package de.huehnken.concurrency

import akka.actor.{Props, Actor}


object Task {
  case object Do
  def props(num: Int) = Props(classOf[Task], num)
}

class Task(num: Int) extends Actor {
  import Task._
  import Coordinator.Result

  def receive = {
    case Do => sender ! Result(num)
  }
}
