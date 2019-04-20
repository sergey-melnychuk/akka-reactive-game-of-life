package edu.reactive.life.ui

import de.h2b.scala.lib.simgraf.event.{KeyEvent, LeftButton, MouseEvent, Subscriber}
import de.h2b.scala.lib.simgraf.{Color, Pixel, Point, World}
import de.h2b.scala.lib.simgraf.layout.Cell
import de.h2b.scala.lib.simgraf.shapes.{Rectangle, Square}
import edu.reactive.life.domain.Pos

trait Display {
  def fill(pos: Pos): Unit
  def clear(pos: Pos): Unit
}

object Display {
  def apply(side: Int, cell: Int, title: String, control: Control): Display = new Display {
    private val world = World.withEvents(Rectangle(Point(0,0), Point(side, side)))(Cell(side, side, Pixel(0,0)), title)
    world.clear(Color.White)

    Subscriber.to(world) {
      case MouseEvent(LeftButton, _, 1, pixel) => control.click(pixel2pos(pixel))
      case KeyEvent('q') => control.terminate()
      case _ => ()
    }

    override def fill(pos: Pos): Unit = {
      world.activeColor = Color.Black
      world.fill(Square(pos2point(pos), cell))
    }

    override def clear(pos: Pos): Unit = {
      world.activeColor = Color.White
      world.fill(Square(pos2point(pos), cell))
    }

    private def pos2point(pos: Pos): Point = Point(pos.col * cell + cell/2, side - (pos.row * cell + cell/2))

    private def pixel2pos(pixel: Pixel): Pos = Pos((side - pixel.y) / cell, pixel.x / cell)
  }
}
