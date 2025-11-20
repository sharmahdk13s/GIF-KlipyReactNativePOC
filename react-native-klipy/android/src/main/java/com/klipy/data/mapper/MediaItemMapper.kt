package com.klipy.data.mapper

import com.klipy.data.dto.MediaItemDto
import com.klipy.domain.models.MediaItem

interface MediaItemMapper {

    fun mapToDomain(data: MediaItemDto): MediaItem
}
