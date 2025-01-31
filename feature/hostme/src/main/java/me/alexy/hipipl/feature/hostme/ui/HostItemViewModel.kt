/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.alexy.hipipl.feature.hostme.ui

//@HiltViewModel
//class HostItemViewModel @Inject constructor(
//    private val hostRepository: HostRepository
//) : ViewModel() {
//
//    val uiState: StateFlow<HostItemUiState> = hostRepository
//        .hostItems.map<List<String>, HostItemUiState> { Success(data = it) }
//        .catch { emit(Error(it)) }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)
//
//    fun addHostItem(name: String) {
//        viewModelScope.launch {
//            hostRepository.add(name)
//        }
//    }
//}
//
//sealed interface HostItemUiState {
//    object Loading : HostItemUiState
//    data class Error(val throwable: Throwable) : HostItemUiState
//    data class Success(val data: List<String>) : HostItemUiState
//}
