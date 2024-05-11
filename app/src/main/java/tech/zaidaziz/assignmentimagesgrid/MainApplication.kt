package tech.zaidaziz.assignmentimagesgrid

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class MainApplication : Application(){
    val applicationScopeCoroutines = CoroutineScope(SupervisorJob()+ Dispatchers.Main)

}