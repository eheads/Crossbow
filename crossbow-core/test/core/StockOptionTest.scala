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

import org.joda.time.DateMidnight
import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.scalatest.FunSuite

class StockOptionTest extends FunSuite {
  test("invert") {
    val s = Stock("XOM", Exchange.nyse, "USD")
    val expiration = new DateMidnight(2011, 3, 5, Settings.timeZone)
    val call = StockOption(s, OptionRight.Call, 30.01, expiration, Exchange.cboe, "USD")
    val put = StockOption(s, OptionRight.Put, 30.01, expiration, Exchange.cboe, "USD")
    expect(put) { call.invert }
    expect(call) { put.invert }
    expect(call) { call.invert.invert }
    expect(put) { put.invert.invert }
  }
  test("comparison") {
    val s = Stock("MSFT", Exchange.nasdaq, "USD")
    val sWrong = Stock("MSFT-", Exchange.nasdaq, "USD")
    val expiration = new DateMidnight(2011, 3, 5, Settings.timeZone)
    val expirationWrong = new DateMidnight(2011, 3, 4, Settings.timeZone)
    val so = StockOption(s, OptionRight.Call, 30, expiration, Exchange.cboe, "USD")
    assert { so == StockOption(s, OptionRight.Call, 30, expiration, Exchange.cboe, "USD") }
    assert { so != StockOption(sWrong, OptionRight.Call, 30, expiration, Exchange.cboe, "USD") }
    assert { so != StockOption(s, OptionRight.Put, 30, expiration, Exchange.cboe, "USD") }
    assert { so != StockOption(s, OptionRight.Call, 30.000001, expiration, Exchange.cboe, "USD") }
    assert { so != StockOption(s, OptionRight.Call, 30, expirationWrong, Exchange.cboe, "USD") }
    assert { so != StockOption(s, OptionRight.Call, 30, expiration, Exchange.nasdaq, "USD") }
    assert { so != StockOption(s, OptionRight.Call, 30, expiration, Exchange.cboe, "EUR") }
  }
}