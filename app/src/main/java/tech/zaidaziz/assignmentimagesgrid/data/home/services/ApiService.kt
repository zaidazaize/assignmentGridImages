package tech.zaidaziz.assignmentimagesgrid.data.home.services

import retrofit2.Response
import retrofit2.http.GET
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel

interface ApiService {

    @GET("api/v2/content/misc/media-coverages?limit=220")
    suspend fun getMediaCoverages( ): Response<List<ImageModel>>
}