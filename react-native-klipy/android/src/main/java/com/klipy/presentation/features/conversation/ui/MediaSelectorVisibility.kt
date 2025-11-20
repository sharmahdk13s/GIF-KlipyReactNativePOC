package com.klipy.presentation.features.conversation.ui

/**
 * Shared visibility state for the Klipy media selector bottom sheet.
 */
enum class MediaSelectorVisibility {
  HIDDEN,
  PARTIALLY_EXPANDED,
  FULLY_EXPANDED;

  fun isVisible() = this != HIDDEN
}
