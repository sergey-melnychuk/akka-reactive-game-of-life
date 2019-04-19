package edu.reactive.life.ui

import akka.actor.{ActorRef, ActorSystem}
import edu.reactive.life.actors.GridActor
import edu.reactive.life.domain.Pos

trait Control {
  def click(pos: Pos): Unit
  def terminate(): Unit
}

object Control {
  def apply(system: ActorSystem, grid: ActorRef): Control = new Control {
    override def click(pos: Pos): Unit = grid ! GridActor.Set(pos)
    override def terminate(): Unit = {
      system.terminate()
      System.exit(0)
    }
  }
}
