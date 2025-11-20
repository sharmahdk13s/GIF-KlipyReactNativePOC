package com.klipy.presentation.features.conversation

import com.klipy.presentation.features.conversation.ui.MediaSelectorVisibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Simple visibility bus used to control the media selector from
 * the React Native module (KlipyModule.setMediaPickerVisible / setMediaPickerState).
 */
object MediaPickerVisibilityBus {

  private val _state = MutableStateFlow(MediaSelectorVisibility.HIDDEN)
  val state: StateFlow<MediaSelectorVisibility> = _state

  /**
   * Backwards-compatible Boolean API used by setMediaPickerVisible.
   * true -> FULLY_EXPANDED, false -> HIDDEN
   */
  fun setVisible(visible: Boolean) {
    _state.value = if (visible) {
      MediaSelectorVisibility.FULLY_EXPANDED
    } else {
      MediaSelectorVisibility.HIDDEN
    }
  }

  /**
   * Directly set the desired visibility state from native or React Native.
   */
  fun setVisibility(visibility: MediaSelectorVisibility) {
    _state.value = visibility
  }
}
