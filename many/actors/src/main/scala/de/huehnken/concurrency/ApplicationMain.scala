package de.huehnken.concurrency

import akka.actor.ActorSystem

import concurrent.Await
import concurrent.duration._
import language.postfixOps

object ApplicationMain extends App {

  val actorSystem = ActorSystem("Devoxx")

  val coordinator = actorSystem.actorOf(Coordinator.props(100000, System.currentTimeMillis))

  coordinator ! Coordinator.StartCommand

  Await.result(actorSystem.whenTerminated, 30 seconds)
}