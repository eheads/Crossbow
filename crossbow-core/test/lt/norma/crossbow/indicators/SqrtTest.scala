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

import lt.norma.crossbow.core.{ IndicatorList }
import lt.norma.crossbow.messages.EmptyMessage
import lt.norma.crossbow.core.testutils.approx
import org.scalatest.FunSuite

class SqrtTest extends FunSuite {
  val e = 0.00005

  test("name") {
    val target = new Variable(5.0) {
      override def name = "A"
    }
    val indicator = new Sqrt(target)
    expect("Sqrt(A)") {
      indicator.name
    }
  }

  test("dependencies") {
    val target = new Variable(5.0)
    val indicator = new Sqrt(target)
    expect(Set(target)) {
      indicator.dependencies
    }
  }

  test("calculation") {
    val target = new Variable[Double]
    val indicator = new Sqrt(target)
    val list = new IndicatorList(indicator)
    approx(2, e) {
      target.set(4)
      list.send(EmptyMessage)
      indicator.value
    }
    approx(3.872983346, e) {
      target.set(15)
      list.send(EmptyMessage)
      indicator.value
    }
    approx(1, e) {
      target.set(1)
      list.send(EmptyMessage)
      indicator.value
    }
    approx(0, e) {
      target.set(0)
      list.send(EmptyMessage)
      indicator.value
    }
  }

  test("calculation - invalid values") {
    val target = new Variable[Double]
    val indicator = new Sqrt(target)
    val list = new IndicatorList(indicator)
    expect(None) {
      target.set(-0.0001)
      list.send(EmptyMessage)
      indicator()
    }
    expect(None) {
      target.set(-1)
      list.send(EmptyMessage)
      indicator()
    }
  }

  test("calculation - empty target") {
    val target = new Variable[Double]
    val indicator = new Sqrt(target)
    val list = new IndicatorList(indicator)
    expect(None) {
      list.send(EmptyMessage)
      indicator()
    }
  }
}
