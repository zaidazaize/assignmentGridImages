package tech.zaidaziz.assignmentimagesgrid.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.zaidaziz.assignmentimagesgrid.util.ConnectivityObserver
import tech.zaidaziz.assignmentimagesgrid.util.NetworkConnectivityObserver

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {
    @Binds
   abstract fun bindConnectivityObserver(connectivityObserverImpl: NetworkConnectivityObserver): ConnectivityObserver
}