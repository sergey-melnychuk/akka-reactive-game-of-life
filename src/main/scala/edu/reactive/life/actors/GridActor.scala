package edu.reactive.life.actors

import scala.language.postfixOps
import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import edu.reactive.life.domain.{Grid, Pos}
import edu.reactive.life.utils.PosUtils

object GridActor {
  def props: Props = Props[GridActor]

  case class Setup(grid: Grid, scale: Int, threshold: Int, display: ActorRef)

  case class Full(pos: Pos)
  case class Empty(pos: Pos)

  sealed trait Update { val pos: Pos }
  case class Set(pos: Pos) extends Update
  case class Inc(pos: Pos) extends Update
  case class Dec(pos: Pos) extends Update
}

class GridActor extends Actor with ActorLogging {
  import GridActor._
  import PosUtils._
  import context.dispatcher

  private val cellCycle = 200 millis

  def pending: Receive = {
    case Setup(grid, k, n, display) =>
      val (rows, cols) = grid.size
      val cells = rows * cols
      if (cells > n) {
        val nodes = grid.split(k, k).map(g => {
          val ref = context.actorOf(GridActor.props)
          ref ! Setup(g, k, n, display)
          (g, ref)
        })
        context.become(fork(grid, nodes), discardOld = true)
      } else {
        val cells = Array.ofDim[ActorRef](rows, cols)
        grid.cells().foreach(p => {
          val ref = context.actorOf(CellActor.props(p))
          ref ! CellActor.Setup(display)
          val (row, col) = (p - grid.zero).toTuple
          cells(row)(col) = ref
        })
        context.system.scheduler.schedule(cellCycle, cellCycle, context.self, CellActor.Tick)
        context.become(leaf(grid, cells), discardOld = true)
      }
  }

  def fork(grid: Grid, nodes: List[(Grid, ActorRef)]): Receive = {
    case e: Update if grid.has(e.pos) =>
      log.info("grid at {},{} inner", grid.zero.row, grid.zero.col)
      nodes.find(_._1.has(e.pos)).foreach(_._2 ! e)
    case e: Update =>
      log.info("grid at {},{} outer", grid.zero.row, grid.zero.col)
      context.parent ! e
  }

  def leaf(grid: Grid, cells: Array[Array[ActorRef]]): Receive = {
    case Inc(pos) =>
      val (row, col) = (pos - grid.zero).toTuple
      cells(row)(col) ! CellActor.Inc
    case Dec(pos) =>
      val (row, col) = (pos - grid.zero).toTuple
      cells(row)(col) ! CellActor.Dec
    case Set(pos) =>
      val (row, col) = (pos - grid.zero).toTuple
      cells(row)(col) ! CellActor.Set
    case Full(pos) =>
      fanOut(grid, pos, cells, p => Inc(p), CellActor.Inc)
      log.info("leaf at {},{} full cell", grid.zero.row, grid.zero.col)
    case Empty(pos) =>
      fanOut(grid, pos, cells, p => Dec(p), CellActor.Dec)
      log.info("leaf at {},{} clear cell", grid.zero.row, grid.zero.col)
    case CellActor.Tick =>
      for {
        row <- cells
        ref <- row
      } {
        ref ! CellActor.Tick
      }
  }

  private def fanOut(grid: Grid, pos: Pos, cells: Array[Array[ActorRef]], f: Pos => Update, upd: CellActor.Update): Unit = {
    grid.adj(pos).foreach(p => {
      if (grid.has(p)) {
        val (row, col) = (p - grid.zero).toTuple
        cells(row)(col) ! upd
      } else {
        context.parent ! f(p)
      }
    })
  }

  def receive: Receive = pending
}
