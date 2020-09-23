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

/**
 * 목록을 표시 가능한 [InvScene]의 구성요소
 */
interface InvListView<T> : InvRegion {
    val items: List<T>
    var index: Int
    var page: Double
        get() = index.toDouble() / size.toDouble()
        set(value) {
            val count = items.count()
            val size = size
            val lastPage = (count - 1) / size
            val newPage = value.coerceIn(0.0, lastPage.toDouble())
            index = (newPage * size).toInt()
        }
    val displayItems: List<T>
    val trim: Boolean

    /**
     * 표시 목록을 새로고침합니다.
     */
    fun refresh() {
        index = index
    }

    /**
     * 표시 목록을 첫번째 페이지로 넘깁니다.
     */
    fun first() {
        index = 0
    }

    /**
     * 표시 목록을 마지막 페이지로 넘깁니다.
     */
    fun last() {
        index = Int.MAX_VALUE
    }
}