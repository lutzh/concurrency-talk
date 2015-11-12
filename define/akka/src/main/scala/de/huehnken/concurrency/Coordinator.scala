package de.huehnken.concurrency

import akka.actor.{Actor, Props}
import com.typesafe.config.ConfigFactory

object Coordinator {

  def props(searchTerm: String) = Props(classOf[Coordinator], searchTerm)

  case class Result(key: String, definition: String)

  case object StartCommand

  case object Done

}

class Coordinator(searchTerm: String) extends Actor {

  import Coordinator._
  import Task.GetDefinition
  import concurrent.duration._
  import context.dispatcher

  val conf = ConfigFactory.load
  val merriamWebsterApiKey = conf.getString("mw.key")
  val sources = Map(
    "Merriam Webster" -> s"http://www.dictionaryapi.com/api/v1/references/collegiate/xml/${searchTerm}?key=${merriamWebsterApiKey}",
    "Wiktionary" -> s"https://en.wiktionary.org/w/api.php?format=xml&action=query&rvprop=content&prop=revisions&redirects=1&titles=${searchTerm}",
    "Urban Dictionary" -> s"http://api.urbandictionary.com/v0/define?term=${searchTerm}"
  )

  var results = Map[String, String]()

  def receive = {
    case StartCommand =>
      sources foreach {
        case (key, url) => context.actorOf(Task.props(key, self)) ! GetDefinition(url)
      }
      context.system.scheduler.scheduleOnce(5 seconds, self, Done)
    case Result(key, definition) =>
      results += key -> definition
      if (results.size == sources.size) {
        self ! Done
      }
    case Done =>
      results foreach {
        case (key, definition) =>
          println("=========================================");
          println(key);
          println("=========================================");
          println(definition);
          println("=========================================");
      }
    context.system.terminate
  }
}
