package com.klipy

import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.module.annotations.ReactModule
import com.klipy.presentation.KlipyMediaPickerActivity
import com.klipy.presentation.features.conversation.MediaPickerVisibilityBus
import com.klipy.presentation.features.conversation.ui.MediaSelectorVisibility

@ReactModule(name = KlipyModuleBase.NAME)
class KlipyModule(reactContext: ReactApplicationContext) : KlipyModuleBase(reactContext) {

  companion object {
    @JvmStatic
    var apiKey: String? = null
  }

  override fun getName() = KlipyModuleBase.NAME

  @ReactMethod
  override fun initialize(apiKey: String, options: ReadableMap?) {
    Companion.apiKey = apiKey
    // TODO: Use apiKey to configure your Klipy networking layer (base URL, etc.)
  }

  @ReactMethod
  override fun open() {
    try {
      val context = reactApplicationContext
      val intent = Intent(context, KlipyMediaPickerActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
      context.startActivity(intent)
    } catch (e: Exception) {
      e.printStackTrace()
      throw RuntimeException("Failed to open Klipy media picker: ${e.message}", e)
    }
  }

  @ReactMethod
  override fun setMediaPickerVisible(visible: Boolean) {
    MediaPickerVisibilityBus.setVisible(visible)
  }

  @ReactMethod
  override fun setMediaPickerState(state: String) {
    val visibility = when (state.lowercase()) {
      "hidden" -> MediaSelectorVisibility.HIDDEN
      "partial", "partially_expanded" -> MediaSelectorVisibility.PARTIALLY_EXPANDED
      "full", "fully_expanded" -> MediaSelectorVisibility.FULLY_EXPANDED
      else -> MediaSelectorVisibility.FULLY_EXPANDED
    }
    MediaPickerVisibilityBus.setVisibility(visibility)
  }

  @ReactMethod
  override fun addListener(eventName: String) {
    // Required for NativeEventEmitter
  }

  @ReactMethod
  override fun removeListeners(count: Double) {
    // Required for NativeEventEmitter
  }
}


