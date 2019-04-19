package edu.reactive.life

import akka.actor.ActorSystem
import edu.reactive.life.actors.{DisplayActor, GridActor}
import edu.reactive.life.domain.Grid
import edu.reactive.life.ui.{Control, Display}
import edu.reactive.life.utils.GridUtils

object Main extends App {
  val system = ActorSystem("reactive-life")

  val side = 640
  val cell = 20

  val threshold = 16
  val scale = 2
  val grid = Grid.ofDim(side / cell, side / cell)

  val displayActor = system.actorOf(DisplayActor.props, "display")
  val gridActor = system.actorOf(GridActor.props, "grid")

  val control = Control(system, gridActor)
  val display = Display(side, cell, "Reactive Life", control)

  gridActor ! GridActor.Setup(grid, scale, threshold, displayActor)
  displayActor ! DisplayActor.Setup(display, control)

  {
    import GridUtils._
    val (rows, cols) = grid.size
    grid.withRandomCells(rows * cols / 2)(control.click)
  }
}
