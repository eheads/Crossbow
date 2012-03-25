package lt.norma.crossbow.indicators

/*
 * Copyright 2010-2011 Vilius Normantas <code@norma.lt>
 *
 * This file is part of Crossbow library.
 *
 * Crossbow is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Crossbow is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Crossbow.  If not,
 * see <http://www.gnu.org/licenses/>.
 */

import lt.norma.crossbow.core._
import org.scalatest.FunSuite
import lt.norma.crossbow.indicators.{History, HistoryLast, MutableIndicator}

class HistoryLastTest extends FunSuite {
  test("name") {
    val target = new MutableIndicator[Double] with History {
      def name = "T"

      def dependencies = Empty
    }
    val historyLast = new HistoryLast(target)
    expect("HistoryLast(T)") {
      historyLast.name
    }
  }

  test("dependencies") {
    val target = new MutableIndicator[Double] with History {
      def name = "T"

      def dependencies = Empty
    }
    val historyLast = new HistoryLast(target)
    expect(Set(target)) {
      historyLast.dependencies
    }
  }

  test("calculation") {
    val target = new MutableIndicator[Int] with History {
      def name = "T"

      def dependencies = Empty
    }
    val historyLast = new HistoryLast(target)
    expect(None) {
      historyLast()
    }
    target.set(1)
    target.history.update()
    expect(Some(1)) {
      historyLast()
    }
    target.set(2)
    target.history.update()
    expect(Some(2)) {
      historyLast()
    }
    target.unset()
    target.history.update()
    expect(None) {
      historyLast()
    }
  }
}