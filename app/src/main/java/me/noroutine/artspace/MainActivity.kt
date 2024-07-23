package me.noroutine.artspace

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.collection.CircularArray
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.noroutine.artspace.ui.theme.ArtSpaceTheme
import kotlin.math.abs


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArtSpaceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ArtworkScreen(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

val primaryColor = Color(0xffecebf3)
val secondaryColor = Color(0xffecebf3)

@Composable
fun ArtworkScreen(modifier: Modifier = Modifier) {
    val artItems = listOf(

        ArtItem(
            artPainter = painterResource(R.drawable.claude_monet___woman_with_a_parasol),
            title = "Woman with a Parasol",
            author = "Claude Monet",
            year = "1875"
        ),

        ArtItem(
            artPainter = painterResource(R.drawable.olexandr_murashko_divchyna_v_chervonim_kapeliusi),
            title = "Girl in a Red Hat",
            author = "Oleksandr Murashko",
            year = "1902-1903"
        ),
        ArtItem(
            artPainter = painterResource(R.drawable.mona_lisa),
            title = "Mona Lisa",
            author = "Leonardo da Vinci",
            year = "1503"
        ),
        ArtItem(
            artPainter = painterResource(R.drawable.crowdstrike),
            title = "A Girl at Work",
            author = "CrowdStrike",
            year = "2024"
        ),
    )

    var artItemIndex by remember {
        mutableIntStateOf(0)
    }

    val artItem = artItems[artItemIndex]

    // do not rotate
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = stringResource(R.string.app_name),
            textAlign = TextAlign.Center,
            lineHeight = 48.sp,
            fontWeight = FontWeight.Thin,
            fontSize = 24.sp,
            modifier = Modifier
                .background(primaryColor)
                .fillMaxWidth()

        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Surface(
                shadowElevation = 20.dp, modifier = Modifier
//                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = artItem.artPainter,
                    contentDescription = "Artwork",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .border(16.dp, Color.White)
                        .fillMaxSize(),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(secondaryColor)
                    .padding(12.dp),
                horizontalAlignment = Alignment.Start
            ) {
                BasicText(
                    text = artItem.title, style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Thin, fontSize = 24.sp,
                        lineHeight = 30.sp
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row() {
                    BasicText(
                        text = artItem.author,
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )

                    BasicText(
                        text = "(${artItem.year})",
                        modifier = Modifier.padding(start = 2.dp),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Light)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    artItemIndex = (artItems.size + artItemIndex - 1) % artItems.size
                }, modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.previous))
            }
            Button(
                onClick = {
                    artItemIndex = (artItems.size + artItemIndex + 1) % artItems.size
                }, modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                Text(stringResource(R.string.next))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArtworkScreenPreview() {
    MaterialTheme {
        ArtworkScreen()
    }
}

// https://stackoverflow.com/a/69083009
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}