package com.klipy.data.di

import com.klipy.data.KlipyRepositoryImpl
import com.klipy.data.datasource.ClipsDataSource
import com.klipy.data.datasource.GifsDataSource
import com.klipy.data.datasource.MediaDataSource
import com.klipy.data.datasource.MediaDataSourceFactory
import com.klipy.data.datasource.MediaDataSourceFactoryImpl
import com.klipy.data.datasource.MediaDataSourceSelector
import com.klipy.data.datasource.MediaDataSourceSelectorImpl
import com.klipy.data.datasource.StickersDataSource
import com.klipy.data.infrastructure.AdvertisingInfoProvider
import com.klipy.data.infrastructure.AdvertisingInfoProviderImpl
import com.klipy.data.infrastructure.ApiCallHelper
import com.klipy.data.infrastructure.DeviceInfoProvider
import com.klipy.data.infrastructure.DeviceInfoProviderImpl
import com.klipy.data.infrastructure.ScreenMeasurementsProvider
import com.klipy.data.infrastructure.ScreenMeasurementsProviderImpl
import com.klipy.data.mapper.MediaItemMapper
import com.klipy.data.mapper.MediaItemMapperImpl
import com.klipy.data.service.ClipsService
import com.klipy.data.service.GifService
import com.klipy.data.service.StickersService
import com.klipy.domain.KlipyRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {
    single<GifService> {
        get<Retrofit>().create(GifService::class.java)
    }
    single<StickersService> {
        get<Retrofit>().create(StickersService::class.java)
    }
    single<ClipsService> {
        get<Retrofit>().create(ClipsService::class.java)
    }

    factory<KlipyRepository> {
        KlipyRepositoryImpl(get())
    }

    single<MediaItemMapper> { MediaItemMapperImpl() }

    factory<MediaDataSource>(named(GIFS_DS)) {
        GifsDataSource(get(), get(), get(), get())
    }
    factory<MediaDataSource>(named(STICKERS_DS)) {
        StickersDataSource(get(), get(), get(), get())
    }
    factory<MediaDataSource>(named(CLIPS_DS)) {
        ClipsDataSource(get(), get(), get(), get())
    }

    factory<MediaDataSourceSelector> {
        MediaDataSourceSelectorImpl(get())
    }
    single<MediaDataSourceFactory> {
        MediaDataSourceFactoryImpl(getAll())
    }

    single<DeviceInfoProvider> { DeviceInfoProviderImpl(get()) }
    single<ScreenMeasurementsProvider> {
        ScreenMeasurementsProviderImpl(androidApplication().applicationContext)
    }
    single<AdvertisingInfoProvider> {
        AdvertisingInfoProviderImpl(androidApplication().applicationContext)
    }
    single<ApiCallHelper> { ApiCallHelper() }
}

private const val GIFS_DS = "gifsDataSource"
private const val CLIPS_DS = "stickersDataSource"
private const val STICKERS_DS = "clipsDataSource"