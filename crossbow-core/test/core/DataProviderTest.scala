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

package lt.norma.crossbow.core

import org.scalatest.FunSuite

class DataProviderTest extends FunSuite {
  object Counter { var n = 0 }
  class DummyListener(deps: DataListener*) extends DataListener {
    def dependencies = deps.toSet
    def reset() { order = 0; lastData = None }
    var lastData: Option[Data] = None
    var order = 0
    def receive = {
      case d: Data =>
        lastData = Some(d)
        Counter.n += 1
        order = Counter.n
    }
  }
  case class DummyData(value: Int) extends Data

  test("Dispatching data") {
    val l1 = new DummyListener()
    val l2 = new DummyListener(l1)
    val l3 = new DummyListener(l2, l1)
    val dp = new DataProvider { }

    dp.dispatch(DummyData(1))
    expect(None) { l1.lastData }
    expect(None) { l2.lastData }
    expect(None) { l3.lastData }
    expect(0) { l1.order }
    expect(0) { l2.order }
    expect(0) { l3.order }
    expect(0) { Counter.n }
    Counter.n = 0
    l1.reset()
    l2.reset()
    l3.reset()

    dp.add(l2)
    dp.dispatch(DummyData(2))
    expect(Some(DummyData(2))) { l1.lastData }
    expect(Some(DummyData(2))) { l2.lastData }
    expect(None) { l3.lastData }
    expect(1) { l1.order }
    expect(2) { l2.order }
    expect(0) { l3.order }
    expect(2) { Counter.n }
    Counter.n = 0
    l1.reset()
    l2.reset()
    l3.reset()

    dp.add(l3)
    dp.add(l3)
    dp.dispatch(DummyData(3))
    expect(Some(DummyData(3))) { l1.lastData }
    expect(Some(DummyData(3))) { l2.lastData }
    expect(Some(DummyData(3))) { l3.lastData }
    expect(1) { l1.order }
    expect(2) { l2.order }
    expect(3) { l3.order }
    expect(3) { Counter.n }
    Counter.n = 0
    l1.reset()
    l2.reset()
    l3.reset()

    dp.remove(l3)
    dp.remove(l3)
    dp.dispatch(DummyData(4))
    expect(Some(DummyData(4))) { l1.lastData }
    expect(Some(DummyData(4))) { l2.lastData }
    expect(None) { l3.lastData }
    expect(1) { l1.order }
    expect(2) { l2.order }
    expect(0) { l3.order }
    expect(2) { Counter.n }
    Counter.n = 0
    l1.reset()
    l2.reset()
    l3.reset()

    dp.remove(l2)
    dp.dispatch(DummyData(5))
    expect(None) { l1.lastData }
    expect(None) { l2.lastData }
    expect(None) { l3.lastData }
    expect(0) { l1.order }
    expect(0) { l2.order }
    expect(0) { l3.order }
    expect(0) { Counter.n }
    Counter.n = 0
    l1.reset()
    l2.reset()
    l3.reset()
  }

  test("Try to remove items with and without dependants") {
    val l0 = new DummyListener()
    val l1 = new DummyListener()
    val l2 = new DummyListener(l1)
    val l3 = new DummyListener(l2)
    val dp = new DataProvider { }
    dp.add(l1)
    dp.add(l2)
    dp.add(l3)

    dp.remove(l0)
    intercept[Exception] { dp.remove(l1) }
    intercept[Exception] { dp.remove(l2) }
    dp.remove(l3)
    dp.remove(l2)
    dp.remove(l1)
  }
}