package edu.reactive.life.utils

import edu.reactive.life.domain.Pos

object PosUtils {

  implicit class PosMethods(pos: Pos) {
    def +(that: Pos): Pos = Pos(pos.row + that.row, pos.col + that.col)

    def -(that: Pos): Pos = Pos(pos.row - that.row, pos.col - that.col)

    def /(d: Int): Pos = Pos(pos.row / d, pos.col / d)

    def *(d: Int): Pos = Pos(pos.row * d, pos.col * d)

    def axis: (Pos, Pos) = (Pos(pos.row, 0), Pos(0, pos.col))

    def toTuple: (Int, Int) = (pos.row, pos.col)
  }

}
