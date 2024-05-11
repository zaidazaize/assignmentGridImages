package tech.zaidaziz.assignmentimagesgrid.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest.Builder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class NetworkConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectivityObserver {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isOnline(): Flow<Boolean> = callbackFlow {

        val callback = object : ConnectivityManager.NetworkCallback() {
            private val networks = mutableSetOf<Network>()
            override fun onAvailable(network: Network) {
                networks += network
                trySend(true)
            }

            override fun onLost(network: Network) {
                networks -= network
                trySend(networks.isNotEmpty())
            }
        }
        val request =
            Builder().addCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET).build()

        connectivityManager.registerNetworkCallback(request, callback)
        channel.trySend(connectivityManager.isCurrentlyConnected())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun ConnectivityManager.isCurrentlyConnected(): Boolean {
        @Suppress("DEPRECATION") return activeNetworkInfo?.isConnected == true
    }
}
