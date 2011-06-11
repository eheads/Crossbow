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

import org.joda.time.DateTime
import org.scalatest.FunSuite

class IndicatorTest extends FunSuite {
  class Dummy extends Indicator[Int] {
    def name = "D"
    def dependencies = Empty
    def calculate = Empty
  }

  class DummyWithDefault extends Indicator[Int] {
    def name = "DD"
    def dependencies = Empty
    def calculate = Empty
    override def default = 5
  }

  test("Accessing value") {
    val i = new Dummy
    intercept[Indicator.ValueNotSetException] { i.value }
    i.set(8)
    expect(8) { i.value }
  }

  test("Accessing optional value") {
    val i = new Dummy
    expect(None) { i.optionalValue }
    i.set(9)
    expect(Some(9)) { i.optionalValue }
  }

  test("Default value") {
    val i = new DummyWithDefault
    expect(5) { i.value }
    expect(Some(5)) { i.optionalValue }
  }

  test("Setting, unset value, method isSet") {
    val i = new Dummy
    intercept[Indicator.ValueNotSetException] { i.value }
    expect(None) { i.optionalValue }
    assert { !i.isSet }

    i.set(15)
    expect(15) { i.value }
    expect(Some(15)) { i.optionalValue }
    assert { i.isSet }

    i.set(Some(16))
    expect(16) { i.value }
    expect(Some(16)) { i.optionalValue }
    assert { i.isSet }

    i.unset()
    intercept[Indicator.ValueNotSetException] { i.value }
    expect(None) { i.optionalValue }
    assert { !i.isSet }

    i.set(17)
    expect(17) { i.value }
    expect(Some(17)) { i.optionalValue }
    assert { i.isSet }
  }

  test("Name") {
    class MyIndicator extends Indicator[Int] {
      def dependencies = Empty
      def calculate = Empty
      def name = "Fancy Name"
    }
    expect("Fancy Name") { (new MyIndicator).name }
    expect("No Fancy Names, Please") {
      (new MyIndicator {
        override def name = "No Fancy Names, Please"
      }).name
    }
  }

  test("Value to string conversion") {
    class MyIndicator extends Indicator[Int] {
      def name = "M"
      def dependencies = Set.empty
      def calculate = Empty
    }

    val i = new MyIndicator
    expect("N/A") { i.valueToString }
    expect("N/A") { i.valueToString(i.optionalValue) }
    i.set(8)
    expect("8") { i.valueToString }
    expect("8") { i.valueToString(i.optionalValue) }

    class MyIndicatorCustomFormat extends Indicator[Int] {
      def name = "M"
      def dependencies = Empty
      def calculate = Empty
      override def valueToString(v: Option[Int]): String =
        v map { "["+_.toString+"]" } getOrElse("no val.")
    }

    val ic = new MyIndicatorCustomFormat
    expect("no val.") { ic.valueToString:String }
    expect("no val.") { ic.valueToString(ic.optionalValue) }
    ic.set(123)
    expect("[123]") { ic.valueToString:String }
    expect("[123]") { ic.valueToString(ic.optionalValue) }
  }

  test("Empty dependencies") {
    class MyIndicator extends Indicator[Int] {
      def name = "M"
      def dependencies = Empty
      def calculate = Empty
    }
    val i = new MyIndicator
    expect(Set()) { i.dependencies }
    i.send(new Data { })
  }
}