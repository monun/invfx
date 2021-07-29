/*
 * InvFX
 * Copyright (C) 2021 Monun
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.monun.invfx.frame

import io.github.monun.invfx.InvDSL

@InvDSL
interface InvRegion {
    val x: Int
    val y: Int
    val width: Int
    val height: Int

    val minX
        get() = x
    val minY
        get() = y
    val maxX
        get() = minX + width
    val maxY
        get() = minY + height
    val size: Int
        get() = width * height

    fun overlaps(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
        return this.minX <= maxX && this.maxX >= minX && this.minY <= maxY && this.maxY >= minY
    }

    fun overlaps(region: InvRegion) = overlaps(region.minX, region.minY, region.maxX, region.maxY)

    fun contains(x: Int, y: Int): Boolean {
        return x in minX until maxX && y in minY until maxY
    }
}