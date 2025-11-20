package com.klipy.data.datasource

import com.klipy.data.infrastructure.ApiCallHelper
import com.klipy.data.infrastructure.DeviceInfoProvider
import com.klipy.data.mapper.MediaItemMapper
import com.klipy.data.service.StickersService

/**
 * This class is implementing the interface MediaDataSource
 * by delegating all of its public members to an instance of MediaDataSourceImpl
 */
class StickersDataSource(
    private val apiCallHelper: ApiCallHelper,
    private val stickersService: StickersService,
    private val mapper: MediaItemMapper,
    private val deviceInfoProvider: DeviceInfoProvider
) : MediaDataSource by MediaDataSourceImpl(apiCallHelper, stickersService, mapper, deviceInfoProvider)