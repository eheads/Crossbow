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

package lt.norma.crossbow.indicators

import lt.norma.crossbow.core._
import lt.norma.crossbow.messages.BarClose
import lt.norma.crossbow.core.testutils.approx
import org.joda.time.DateTime
import org.scalatest.FunSuite

class EmaContinuousTest extends FunSuite {

  class I(n: String) extends MutableIndicator[Double] {
    def name = n

    def dependencies = Empty
  }

  test("EmaContinuous indicator") {
    val i1 = new I("A")
    val i = new EmaContinuous(5, i1)
    val l = new IndicatorList(i)
    val e = 0.000005

    expect("EMA_C(5; A)") {
      i.name
    }
    expect(Set(i1)) {
      i.dependencies
    }
    expect(None) {
      i()
    }

    i1.set(0.5)
    l.send(new BarClose(new DateTime))
    expect(0.5) {
      i.value
    }

    i1.set(1)
    l.send(new BarClose(new DateTime))
    approx(0.66667, e) {
      i.value
    }

    i1.set(2)
    l.send(new BarClose(new DateTime))
    approx(1.11111, e) {
      i.value
    }

    i1.set(1.5)
    l.send(new BarClose(new DateTime))
    approx(1.24074, e) {
      i.value
    }

    i1.set(0.5)
    l.send(new BarClose(new DateTime))
    approx(0.99383, e) {
      i.value
    }

    i1.set(6)
    l.send(new BarClose(new DateTime))
    approx(2.66255, e) {
      i.value
    }

    i1.set(3)
    l.send(new BarClose(new DateTime))
    approx(2.77503, e) {
      i.value
    }

    i1.set(2)
    l.send(new BarClose(new DateTime))
    approx(2.51669, e) {
      i.value
    }

    i1.set(1)
    l.send(new BarClose(new DateTime))
    approx(2.01113, e) {
      i.value
    }

    i1.set(-25)
    l.send(new BarClose(new DateTime))
    approx(-6.99258, e) {
      i.value
    }

    i1.unset()
    l.send(new BarClose(new DateTime))
    approx(-6.99258, e) {
      i.value
    }
    l.send(new BarClose(new DateTime))
    approx(-6.99258, e) {
      i.value
    }
    l.send(new BarClose(new DateTime))
    approx(-6.99258, e) {
      i.value
    }
    l.send(new BarClose(new DateTime))
    approx(-6.99258, e) {
      i.value
    }

    i1.set(10)
    l.send(new BarClose(new DateTime))
    approx(-1.32839, e) {
      i.value
    }

    i1.set(100)
    l.send(new BarClose(new DateTime))
    approx(32.44774, e) {
      i.value
    }

    i1.set(75)
    l.send(new BarClose(new DateTime))
    approx(46.63183, e) {
      i.value
    }

    i1.set(-1)
    l.send(new BarClose(new DateTime))
    approx(30.75455, e) {
      i.value
    }

    i1.set(200)
    l.send(new BarClose(new DateTime))
    approx(87.16970, e) {
      i.value
    }

    i1.set(199)
    l.send(new BarClose(new DateTime))
    approx(124.44647, e) {
      i.value
    }
  }
  test("EmaContinuous indicator - invalid period") {
    intercept[IllegalArgumentException] {
      new EmaContinuous(0, new I("A"))
    }
    intercept[IllegalArgumentException] {
      new EmaContinuous(-5, new I("A"))
    }
  }
}
