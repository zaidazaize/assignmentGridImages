package tech.zaidaziz.assignmentimagesgrid.data.home

import android.graphics.BitmapFactory
import org.junit.jupiter.api.Assertions.*

import org.junit.Test

class HomeRepositoryKtTest {

    @Test
    fun testCalculateInSampleSize() {
        val options = BitmapFactory.Options()
        options.outWidth = 100
        options.outHeight = 100
        val reqWidth = 50
        val reqHeight = 50
        val result = calculateInSampleSize(options, reqWidth, reqHeight)
        assertEquals(2, result)
    }

    @Test
    fun `test calculateSample size with height and width less than requested height and width`() {
        val options = BitmapFactory.Options()
        options.outWidth = 50
        options.outHeight = 50
        val reqWidth = 100
        val reqHeight = 100
        val result = calculateInSampleSize(options, reqWidth, reqHeight)
        assertEquals(1, result)
    }
}