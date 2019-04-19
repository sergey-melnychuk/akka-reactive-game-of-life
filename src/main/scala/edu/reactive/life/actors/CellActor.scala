package edu.reactive.life.actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.LoggingReceive
import edu.reactive.life.domain.{Cell, Pos}

object CellActor {
  def props(pos: Pos): Props = Props(classOf[CellActor], pos)

  case class Setup(display: ActorRef)

  sealed trait Update
  case object Set extends Update
  case object Inc extends Update
  case object Dec extends Update

  case object Tick
}

class CellActor(pos: Pos) extends Actor with ActorLogging {
  import CellActor._
  var around: Int = 0
  var alive: Boolean = false

  def uninitialized: Receive = LoggingReceive {
    case Setup(display) =>
      context.become(initialized(display), discardOld = true)
  }

  def initialized(display: ActorRef): Receive = LoggingReceive {
    case Inc =>
      log.info("cell at {} inc", pos)
      around += 1
    case Dec =>
      log.info("cell at {} dec", pos)
      around -= 1
    case Set =>
      alive = true
      context.parent ! GridActor.Full(pos)
      display ! DisplayActor.Render(pos, full = true)
    case Tick =>
      val survives = Cell(alive, around)
      if (survives != alive) {
        val update = if (survives) GridActor.Full(pos) else GridActor.Empty(pos)
        context.parent ! update
        display ! DisplayActor.Render(pos, survives)
      }
      alive = survives
  }

  def receive: Receive = uninitialized
}
