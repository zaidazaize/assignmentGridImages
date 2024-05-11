package tech.zaidaziz.assignmentimagesgrid.data.home

import javax.inject.Inject

class HomeLocalDataSource @Inject constructor(
    @ApplicationContext val context: Context,
    @Named(LOCAL_THUMBNAIL_DIR)
    private val thumbnailDir: File
) {

    val applicationScopeCoroutines =
        (context.applicationContext as MainApplication).applicationScopeCoroutines


class HomeLocalDataSource  @Inject constructor(){
}