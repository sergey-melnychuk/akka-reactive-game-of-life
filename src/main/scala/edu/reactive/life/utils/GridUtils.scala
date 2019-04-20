package edu.reactive.life.utils

import edu.reactive.life.domain.{Grid, Pos}

import scala.util.Random

object GridUtils {
  private def index2pos(grid: Grid, idx: Int): Pos = {
    val (rows, cols) = grid.size
    Pos(idx / rows, idx % cols)
  }

  private def kRandom(k: Int, n: Int, seed: Long = 42L): List[Int] = {
    val random = new Random(seed)

    @annotation.tailrec
    def sub(i: Int, acc: List[Int] = Nil): List[Int] =
      if (i == 0) acc
      else {
        val next = i + random.nextInt(n - i)
        sub(i - 1, next :: acc)
      }

    sub(k)
  }

  implicit class GridSupport(grid: Grid) {
    def withRandomCells(k: Int)(f: Pos => Unit): Unit = {
      val (rows, cols) = grid.size
      val n = rows * cols
      kRandom(k, n, System.currentTimeMillis()).foreach(i => f(index2pos(grid, i)))
    }
  }
}
