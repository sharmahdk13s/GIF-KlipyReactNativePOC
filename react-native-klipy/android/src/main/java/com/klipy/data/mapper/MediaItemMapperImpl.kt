package com.klipy.data.mapper

import com.klipy.data.base64toBitmap
import com.klipy.data.dto.FileMetaDataDto
import com.klipy.data.dto.MediaItemDto
import com.klipy.domain.models.MediaItem
import com.klipy.domain.models.MetaData
import com.klipy.presentation.features.conversation.model.MediaType
import java.util.UUID

class MediaItemMapperImpl : MediaItemMapper {
    override fun mapToDomain(data: MediaItemDto): MediaItem {
        return when (data) {
            is MediaItemDto.ClipMediaItemDto -> {
                val selector = data.file?.gif?.let {
                    MetaData(
                        url = it,
                        width = data.fileMeta!!.gif!!.width!!,
                        height = data.fileMeta.gif.height!!
                    )
                }
                val preview = data.file?.mp4?.let {
                    MetaData(
                        url = it,
                        width = data.fileMeta!!.mp4!!.width!!,
                        height = data.fileMeta.mp4.height!!
                    )
                }
                MediaItem(
                    id = data.slug!!,
                    title = data.title,
                    placeHolder = data.placeHolder?.base64toBitmap(),
                    lowQualityMetaData = selector,
                    highQualityMetaData = preview,
                    mediaType = MediaType.CLIP
                )
            }

            is MediaItemDto.GeneralMediaItemDto -> {
                val fileType = data.file?.run {
                    md ?: hd ?: xs
                }
                val highDefFileType = data.file?.run {
                    hd ?: md ?: sm
                }
                MediaItem(
                    id = data.slug!!,
                    title = data.title,
                    placeHolder = data.placeHolder?.base64toBitmap(),
                    lowQualityMetaData = fileType?.gif?.mapToDomain(),
                    highQualityMetaData = highDefFileType?.gif?.mapToDomain(),
                    mediaType = if (data.type == "gif") {
                        MediaType.GIF
                    } else {
                        MediaType.STICKER
                    }
                )
            }

            is MediaItemDto.AdMediaItemDto -> {
                val metaData = MetaData(
                    url = data.content!!,
                    width = data.width!!,
                    height = data.height!!
                )
                MediaItem(
                    id = "ad-${UUID.randomUUID()}",
                    title = null,
                    placeHolder = null,
                    lowQualityMetaData = metaData,
                    highQualityMetaData = null,
                    mediaType = MediaType.AD
                )
            }
        }
    }

    private fun FileMetaDataDto.mapToDomain() =
        MetaData(
            url = url!!,
            width = width!!,
            height = height!!
        )
}