package tech.zaidaziz.assignmentimagesgrid.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import javax.inject.Named

@InstallIn(SingletonComponent::class)
@Module
class DispatcherModule {
    companion object{
        const val IO_DISPATCHER = "io_dispatcher"
    }
    @Named(IO_DISPATCHER)
    @Provides
    fun provideDispatcherProvider() : CoroutineDispatcher {
        return Dispatchers.IO
    }

}