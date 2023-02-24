package org.jugyo.zoomable.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Screen()
            }
        }
    }
}

@Composable
private fun Screen() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        context.startActivity(Intent(context, InstagramLikeZoomableActivity::class.java))
    }

//    Box(modifier = Modifier.fillMaxSize()) {
//        Column(
//            modifier = Modifier.align(Alignment.Center),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            Button(onClick = {
//                context.startActivity(Intent(context, BasicUsageActivity::class.java))
//            }) {
//                Text("Basic Usage")
//            }
//        }
//    }
}
