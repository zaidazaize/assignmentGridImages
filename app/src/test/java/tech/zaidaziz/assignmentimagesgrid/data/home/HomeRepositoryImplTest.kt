package tech.zaidaziz.assignmentimagesgrid.data.home

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ImageModel
import tech.zaidaziz.assignmentimagesgrid.data.home.models.Result
import tech.zaidaziz.assignmentimagesgrid.data.home.models.ThumbnailDetail


class HomeRepositoryImplTest {

    val networkDataSource: HomeNetworkDataSource = mock()
    val localDataSource: HomeLocalDataSource = mock()
    lateinit var homeRepository: HomeRepositoryImpl

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        homeRepository = HomeRepositoryImpl(networkDataSource, localDataSource)
    }

    @Test
    fun `getMediaCoverage localCacheNull make requestWithSuccess`() = runTest {
        // Arrange
        val imageList = getListOfImages()
        val networkResponse = Response.success(imageList)
        val expectedResponse = Result.Success(imageList)

        // Act
        whenever(networkDataSource.getMediaCoverages()).thenReturn(networkResponse)
        val result = homeRepository.getMediaCoverages()

        // Assert
        assert(result is Result.Success)
        assert((result as Result.Success).data == expectedResponse.data)
    }

    @Test
    fun `getMediaCoverage localCacheNull make requestWithFailure`() = runTest {
        // Arrange
        val networkResponse = Response.error<List<ImageModel>>(404, mock())
        val expectedResponse = Result.Error(Exception("404"))

        // Act
        whenever(networkDataSource.getMediaCoverages()).thenReturn(networkResponse)
        val result = homeRepository.getMediaCoverages()
        // Assert
        assert(result is Result.Error)
    }

    @Test
    fun `getMediaCoverage localCacheNotNull`() = runTest{
        val imageList = getListOfImages()
        homeRepository.mediaCoverages = imageList
        val result = homeRepository.getMediaCoverages()
        val expectedResult = Result.Success(imageList)
        assert(result is Result.Success)
        assert((result as Result.Success).data == imageList)
    }

    fun getListOfImages(): List<ImageModel> {
        return listOf(
            ImageModel(
                "a",
                thumbnail = ThumbnailDetail(
                    id = "ta"
                )
            ),
            ImageModel(
                "b",
                thumbnail = ThumbnailDetail(
                    id = "tb"
                )
            ),
            ImageModel(
                "c",
                thumbnail = ThumbnailDetail(
                    id = "tc"
                )
            ),
            ImageModel(
                "d",
                thumbnail = ThumbnailDetail(
                    id = "td"
                )
            ),
        )
    }
}

class MainDispatcherRule @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}