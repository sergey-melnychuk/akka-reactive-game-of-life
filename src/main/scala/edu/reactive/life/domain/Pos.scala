package edu.reactive.life.domain

object Pos {
  val Zero = Pos(0, 0)
}

case class Pos(row: Int, col: Int)
