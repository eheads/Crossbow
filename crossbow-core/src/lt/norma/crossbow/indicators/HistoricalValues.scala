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

/** List of indicator's historical values.
  *
  * Internally historical values are stored in a reversed list. Due to this, accessing the most
  * recent historical values (by methods `last`, `lastSet`, `take`, `takeSet`) is relatively fast,
  * but performing operations on entire list may be expensive as the list will be reversed every
  * time it's accessed. */
final class HistoricalValues[Value](optionalValue: () => Option[Value],
                                    valueToString: Option[Value] => String) {
  private var reversedValues = List[Option[Value]]()

  /** List of historical values. */
  def values: List[Option[Value]] = reversedValues.reverse

  /** Adds the result of `optionalValue` to the list of historical values. */
  def update() {
    reversedValues = optionalValue() :: reversedValues
  }

  /** Number of historical values stored in this list. */
  def size = reversedValues.size

  /** Returns true if the list stores no historical values. */
  def isEmpty = reversedValues.isEmpty

  /** Returns historical value at the specified index. Index 0 being the last value, 1 - previous
    * value, etc. Returns `None` if the index is out of bounds. */
  def valueAt(index: Int) = {
    if (index >= 0 && index < size) {
      reversedValues(index)
    } else {
      None
    }
  }

  /** Returns the most recent historical value. */
  def last: Option[Value] = valueAt(0)

  /** Searches for the most recent not `None` historical value. Return `None` if such value cannot
    * be found. */
  def lastSet = reversedValues.find(_.isDefined).map(_.get)

  /** Returns up to `n` latest historical values. */
  def take(n: Int): List[Option[Value]] = reversedValues.take(n).reverse

  /** Returns up to `n` latest historical values, where value is not `None`. */
  def takeSet(n: Int): List[Option[Value]] = {
    reversedValues.iterator.filter(_.isDefined).take(n).toList.reverse
  }

  /** Removes historical values, except for the most recent items specified by `leave`. */
  def truncate(leave: Int) {
    reversedValues = reversedValues.take(leave)
  }

  /** Returns list strings representing values. Uses `valueToString` function parameter, passed to
    * the constructor. */
  def valuesToStrings: List[String] = values map {
    valueToString
  }
}
