package tech.zaidaziz.assignmentimagesgrid.data.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import tech.zaidaziz.assignmentimagesgrid.data.home.services.ApiService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkingModule {

    @Provides
    fun providesMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    fun providesRetrofit(moshi :Moshi ): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://acharyaprashant.org/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofitModule: Retrofit): ApiService {
        return retrofitModule.create(ApiService::class.java)
    }

}