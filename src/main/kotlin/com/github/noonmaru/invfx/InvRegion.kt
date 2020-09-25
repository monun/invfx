/*
 * Copyright (c) 2020 Noonmaru
 *
 *  Licensed under the General Public License, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/gpl-3.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.noonmaru.invfx

import org.bukkit.inventory.ItemStack

/**
 * 구역을 가지는 [InvScene]의 구성요소 상위 인터페이스
 *
 * @see InvListView
 * @see InvButton
 * @see InvScene
 */
interface InvRegion : InvNode {
    /**
     * 자신이 소속된 부모 [InvScene]
     */
    val scene: InvScene

    /**
     * 세로길이
     *
     * @see height
     */
    val width: Int

    /**
     * 가로길이
     *
     * @see width
     */
    val height: Int

    val minX: Int
        get() = x
    val minY: Int
        get() = y
    val maxX: Int
        get() = minX + width - 1
    val maxY: Int
        get() = minY + height - 1

    /**
     * 구역의 크기입니다. [width] * [height]
     */
    val size: Int
        get() = width * height

    /**
     * 구역과 겹치는지 확인합니다.
     */
    fun overlaps(minX: Int, minY: Int, maxX: Int, maxY: Int): Boolean {
        return this.minX <= maxX && this.maxX >= minX && this.minY <= maxY && this.maxY >= minY
    }

    /**
     * 구역에 포함되어있는 좌표인지 확인합니다.
     */
    fun contains(x: Int, y: Int): Boolean {
        return x in minX..maxX && y in minY..maxY
    }

    /**
     * 좌표의 아이템을 가져옵니다.
     *
     * @exception IllegalArgumentException 범위를 벗어날 경우
     */
    fun getItem(x: Int, y: Int): ItemStack? {
        return scene.inventory.getItem(toInvIndex(x, y))
    }

    /**
     * 좌표의 아이템을 설정합니다.
     *
     * @exception IllegalArgumentException 범위를 벗어날 경우
     */
    fun setItem(x: Int, y: Int, item: ItemStack?) {
        return scene.inventory.setItem(toInvIndex(x, y), item)
    }

    /**
     * 좌표를 인벤토리 슬롯 인덱스로 변환합니다.
     *
     * @exception IllegalArgumentException 범위를 벗어날 경우
     */
    fun toInvIndex(x: Int, y: Int): Int {
        require(x in 0 until width && y in 0 until height) { "Out of range" }

        return x + y * 9 + minX + minY * 9
    }
}