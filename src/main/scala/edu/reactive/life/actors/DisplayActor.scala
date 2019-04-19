package edu.reactive.life.actors

import akka.actor.{Actor, ActorLogging, Props}
import edu.reactive.life.domain.Pos
import edu.reactive.life.ui.{Control, Display}

object DisplayActor {
  def props: Props = Props(classOf[DisplayActor]) //.withDispatcher("akka.actor.blocking-io-dispatcher")

  case class Setup(display: Display, control: Control)

  case class Render(pos: Pos, full: Boolean)
  case class Click(pos: Pos)
}

class DisplayActor extends Actor with ActorLogging {
  import DisplayActor._

  def pending: Receive = {
    case Setup(display, control) =>
      context.become(initialized(display, control), discardOld = true)
  }

  def initialized(display: Display, control: Control): Receive = {
    case Click(pos)         => control.click(pos)
    case Render(pos, true)  =>
      log.info("render {}", pos)
      display.fill(pos)
    case Render(pos, false) =>
      log.info("render {}", pos)
      display.clear(pos)
  }

  def receive: Receive = pending
}
