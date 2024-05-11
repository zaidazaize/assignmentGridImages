package tech.zaidaziz.assignmentimagesgrid.data.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class StorageModule {

    @Provides
    @Named(LOCAL_THUMBNAIL_DIR)
    fun provideThumbnailReference(@ApplicationContext applicationContext: Context): File {
        return lazy { getThumbnailDir(applicationContext) }.value
    }

    companion object {
        const val LOCAL_THUMBNAIL_DIR = "LOCAL_THUMBNAIL_DIR"
    }

}

fun getThumbnailDir(context: Context): File {
    val thumbnailDir = File(context.cacheDir, "thumbnails")
    if (!thumbnailDir.exists()) {
        thumbnailDir.mkdirs()
    }
    return thumbnailDir
}