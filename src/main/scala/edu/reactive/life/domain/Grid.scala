package edu.reactive.life.domain

import edu.reactive.life.utils.PosUtils

object Grid {
    trait Factory {
        def ofDim(rows: Int, cols: Int): Grid
    }

    def ofDim(rows: Int, cols: Int): Grid =
        new Grid(rows, cols, Pos.Zero, None)

    def at(zero: Pos, root: Grid): Factory =
        (rows: Int, cols: Int) => new Grid(rows, cols, zero, Some(root))
}

class Grid private (rows: Int, cols: Int, at: Pos, parent: Option[Grid]) {
    assert(rows > 0 && cols > 0)
    import PosUtils._

    val zero: Pos = at
    val size: (Int, Int) = (rows, cols)
    private val root: Grid = parent.getOrElse(this)

    private def fits(lo: Int, hi: Int): Int => Boolean =
        (x: Int) => x >= lo && x < hi

    def valid(pos: Pos): Boolean = root.has(pos)

    def has(pos: Pos): Boolean =
        fits(at.row, at.row + rows)(pos.row) && fits(at.col, at.col + cols)(pos.col)

    def adj(pos: Pos): List[Pos] =
        for {
            dr <- List(-1, 0, 1)
            dc <- List(-1, 0, 1)
            if dr != 0 || dc != 0
            p = Pos(pos.row + dr, pos.col + dc)
            if valid(p)
        } yield p

    def split(sr: Int, sc: Int): List[Grid] = {
        assert(sr > 0 && sc > 0)
        assert(rows % sr == 0 && cols % sc == 0)

        val (qr, qc) = Pos(rows / sr, cols / sc).axis
        val quads = for {
            nr <- 0 until sc
            row = qr * nr
            nc <- 0 until sr
            col = qc * nc
            offset = row + col
        } yield Grid.at(at + offset, root).ofDim(qr.row, qc.col)

        quads.toList
    }

    def cells(): Seq[Pos] =
        for {
            dr <- 0 until rows
            dc <- 0 until cols
            p = Pos(dr, dc)
        } yield at + p

    override def toString: String = s"Grid($at,$rows,$cols,${if (root != this) root.toString else "this"})"
}
