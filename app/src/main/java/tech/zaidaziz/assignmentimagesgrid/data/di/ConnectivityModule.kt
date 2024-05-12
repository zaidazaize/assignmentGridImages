package tech.zaidaziz.assignmentimagesgrid.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import tech.zaidaziz.assignmentimagesgrid.data.home.HomeLocalDataSource
import tech.zaidaziz.assignmentimagesgrid.data.home.HomeLocalDataSourceImpl
import tech.zaidaziz.assignmentimagesgrid.data.home.HomeNetworkDataSource
import tech.zaidaziz.assignmentimagesgrid.data.home.HomeNetworkDataSourceImpl
import tech.zaidaziz.assignmentimagesgrid.data.home.HomeRepository
import tech.zaidaziz.assignmentimagesgrid.data.home.HomeRepositoryImpl
import tech.zaidaziz.assignmentimagesgrid.util.ConnectivityObserver
import tech.zaidaziz.assignmentimagesgrid.util.NetworkConnectivityObserver

@Module
@InstallIn(SingletonComponent::class)
abstract class ConnectivityModule {

    @Binds
    abstract fun bindConnectivityObserver(connectivityObserverImpl: NetworkConnectivityObserver): ConnectivityObserver

    @Binds
    abstract fun bindHomeLocalDataSource(homeLocalDataSourceImpl: HomeLocalDataSourceImpl): HomeLocalDataSource

    @Binds
    abstract fun bindHomeLocalDataSource(homeNetworkDataSourceImpl: HomeNetworkDataSourceImpl): HomeNetworkDataSource

    @Binds
    abstract fun bindHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository
}