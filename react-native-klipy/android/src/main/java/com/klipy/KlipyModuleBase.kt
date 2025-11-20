package com.klipy

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReadableMap

/**
 * Base class for the Klipy native module.
 */
abstract class KlipyModuleBase(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  companion object {
    const val NAME = "NativeKlipy"
  }

  abstract fun initialize(apiKey: String, options: ReadableMap?)

  abstract fun open()

  abstract fun setMediaPickerVisible(visible: Boolean)

  abstract fun setMediaPickerState(state: String)

  abstract fun addListener(eventName: String)

  abstract fun removeListeners(count: Double)
}
