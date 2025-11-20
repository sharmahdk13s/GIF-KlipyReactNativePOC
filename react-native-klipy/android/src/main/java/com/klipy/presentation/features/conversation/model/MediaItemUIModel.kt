package com.klipy.presentation.features.conversation.model

import androidx.compose.runtime.Stable
import com.klipy.domain.models.MediaItem

@Stable
data class MediaItemUIModel(
    val mediaItem: MediaItem,
    var measuredWidth: Int,
    var measuredHeight: Int
)