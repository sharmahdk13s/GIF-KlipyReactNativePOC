package com.klipy

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.klipy.domain.models.MediaItem
import com.klipy.presentation.features.conversation.model.MediaType
import java.lang.ref.WeakReference

/**
 * Android equivalent of the iOS KlipyEvents emitter.
 * Exposes addListener/removeListeners for NativeEventEmitter and
 * provides a static helper to emit "onReactionSelected" events to JS.
 */
class KlipyEvents(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  override fun getName() = "KlipyEvents"

  private var hasListeners = false

  init {
    Companion.reactContextRef = WeakReference(reactContext)
  }

  @ReactMethod
  fun addListener(eventName: String) {
    hasListeners = true
  }

  @ReactMethod
  fun removeListeners(count: Double) {
    hasListeners = false
  }

  companion object {
    private var reactContextRef: WeakReference<ReactApplicationContext>? = null

    fun emitReactionSelected(item: MediaItem) {
      val ctx = reactContextRef?.get() ?: return

      val payload = Arguments.createMap().apply {
        putString("id", item.id)
        putString("type", mapMediaType(item.mediaType))
        putString("value", selectValue(item))
        putMap("extra", buildExtra(item))
      }

      ctx.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        .emit("onReactionSelected", payload)
    }

    private fun mapMediaType(type: MediaType): String = when (type) {
      MediaType.GIF -> "gif"
      MediaType.STICKER -> "sticker"
      MediaType.CLIP -> "clip"
      else -> "gif"
    }

    private fun selectValue(item: MediaItem): String {
      val high = item.highQualityMetaData?.url
      val low = item.lowQualityMetaData?.url
      return when {
        !high.isNullOrEmpty() -> high
        !low.isNullOrEmpty() -> low
        else -> ""
      }
    }

    private fun buildExtra(item: MediaItem): WritableMap = Arguments.createMap().apply {
      putString("title", item.title ?: "")
      item.highQualityMetaData?.let { meta ->
        putString("highQualityUrl", meta.url)
        putInt("highQualityWidth", meta.width)
        putInt("highQualityHeight", meta.height)
      }
      item.lowQualityMetaData?.let { meta ->
        putString("lowQualityUrl", meta.url)
        putInt("lowQualityWidth", meta.width)
        putInt("lowQualityHeight", meta.height)
      }
    }
  }
}
