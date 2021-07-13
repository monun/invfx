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

package io.github.monun.invfx

/**
 * [InvButton]추가가 가능한 [InvScene]의 구성요소
 */
interface InvPane : InvRegion {
    /**
     * 등록된 버튼목록입니다.
     */
    val buttons: List<io.github.monun.invfx.InvButton>

    /**
     * 좌표에 등록된 버튼을 가져옵니다.
     */
    fun buttonAt(x: Int, y: Int): io.github.monun.invfx.InvButton?
}