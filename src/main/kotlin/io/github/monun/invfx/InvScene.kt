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
 * [InvPane]과 [InvListView]로 구성 가능한 [InvWindow]클래스
 */
interface InvScene : InvWindow {
    /**
     * 등록된 [InvRegion] 목록
     */
    val regions: List<InvRegion>

    /**
     * 좌표에 등록된 [InvRegion]을 반환합니다.
     */
    fun regionAt(x: Int, y: Int): InvRegion?
}