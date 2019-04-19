package edu.reactive.life.domain

// https://en.wikipedia.org/wiki/Conway's_Game_of_Life

object Cell {
    def apply(alive: Boolean, around: Int): Boolean = {
        assert(around >= 0)
        (alive, around) match {
            case (true, n) if n < 2 => false
            case (true, n) if n == 2 || n == 3 => true
            case (true, n) if n > 3 => false
            case (false, n) if n == 3 => true
            case _ => false
        }
    }
}
