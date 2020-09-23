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

package com.github.noonmaru.invfx.builder

import com.github.noonmaru.invfx.internal.InvRegionImpl

/**
 * [InvRegion]를 사전설정 할 수 있는 클래스
 */
abstract class InvRegionBuilder internal constructor() {

    internal abstract val instance: InvRegionImpl

    internal abstract fun build(): InvRegionImpl
}