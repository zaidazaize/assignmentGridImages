package tech.zaidaziz.assignmentimagesgrid.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tech.zaidaziz.assignmentimagesgrid.ui.theme.AssignmentImagesGridTheme

@Composable
fun MainApp() {
    AssignmentImagesGridTheme {
        Scaffold {innerPadding ->
            Surface(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                Home()
            }

        }
    }

}