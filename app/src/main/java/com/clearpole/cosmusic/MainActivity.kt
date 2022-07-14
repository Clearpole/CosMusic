package com.clearpole.cosmusic

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Palette.Swatch
import coil.compose.AsyncImage
import com.clearpole.cosmusic.ui.theme.CosMusicTheme
import com.smarttoolfactory.extendedcolors.util.getColorTonesList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*
import java.net.URL

class MainActivity : ComponentActivity() {
    private var inputText = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CosMusicTheme {
                val back = remember {
                    mutableStateOf("0")
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        val listState = rememberLazyListState()
                        Background(mute = back)
                        val state = rememberScrollState()
                       
                            NewSongList(back,listState)

                        BottomAppBarHome(back)
                    }
                }
            }
        }
        lifecycleScope.launchWhenCreated {
            withContext(Dispatchers.IO) {
                inputText.value = try {
                    URL("https://m.kugou.com/rank/info/?rankid=8888&page=1&json=true").readText()
                } catch (e: Exception) {
                    "你网爆了"
                }
            }
        }
    }

    @Composable
    fun Background(mute: MutableState<String>) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getColorTonesList(Color(mute.value.toInt()))[10])
        ) {
        }
    }

    @Composable
    fun NewSongList(back: MutableState<String>,listState:LazyListState) {
        Row(Modifier.padding(10.dp).clip(RoundedCornerShape(30.dp))) {
            LazyColumn(state = listState) {
                if (!TextUtils.isEmpty(inputText.value)) {
                    val data =
                        JSONObject(inputText.value).getJSONObject("songs").getJSONArray("list")
                    items(data.length()) {
                        val everyInfo = data.getJSONObject(it)
                        val file = everyInfo.getString("filename")
                        val songName = file.substring(file.indexOf(" - ") + 3 until file.length)
                        val artist = file.substring(0 until file.indexOf(" -"))
                        val img =
                            everyInfo.getString("album_sizable_cover").replace("{size}", "150")
                        val hash = everyInfo.getString("hash")
                        HomeSongCard(
                            imgUrl = img,
                            songName = songName,
                            artist = artist,
                            hash = hash,
                            act = this@MainActivity,
                            mute = back
                        )
                    }
                }
            }
        }
        remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }
        if (listState.firstVisibleItemIndex==0){
        }
    }
}

@Composable
fun HomeSongCard(
    imgUrl: String,
    songName: String,
    artist: String,
    hash: String,
    act: Activity,
    mute: MutableState<String>
) {
    Column(
        Modifier
            .clip(RoundedCornerShape(8.dp))) {
        Row(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .height(85.dp)
            .clickable {
                Thread {
                    Palette
                        .from(netToLoacalBitmap(imgUrl.replace("http", "https"))!!)
                        .generate {
                            var vibrant: Swatch? = it!!.vibrantSwatch
                            if (vibrant == null) {
                                for (swatch in it.swatches) {
                                    vibrant = swatch
                                    break
                                }
                            }
                            mute.value = vibrant!!.rgb.toString()
                        }
                }.start()
            }) {
            Spacer(modifier = Modifier.width(20.dp))
            Column(Modifier.align(Alignment.CenterVertically)) {
                AsyncImage(
                    model = imgUrl.replace("http", "https"),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(shape = RoundedCornerShape(15.dp))
                )
            }
            Spacer(modifier = Modifier.width(15.dp))
            Column(Modifier.align(Alignment.CenterVertically)) {
                Text(text = songName)
                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = artist)
            }
        }
        Spacer(modifier = Modifier.height((3).dp))
    }
}


@Composable
fun BottomAppBarHome(mute: MutableState<String>) {
    Row(Modifier.fillMaxSize()) {
        BottomAppBar(
            modifier = Modifier
                .align(Alignment.Bottom)
                .clip(RoundedCornerShape(20.dp)),
            containerColor = getColorTonesList(Color(mute.value.toInt()))[8]
        ) {
            Spacer(modifier = Modifier.weight(0.1f, true))
            IconButton(onClick = { }) {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Localized description",
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    Icons.Outlined.Search,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* doSomething() */ }) {
                Icon(
                    Icons.Outlined.Settings,
                    contentDescription = "Localized description"
                )
            }
            Spacer(modifier = Modifier.weight(1f, true))
            ExtendedFloatingActionButton(
                elevation = BottomAppBarDefaults.floatingActionButtonElevation(),
                text = { Text(text = stringResource(R.string.NowPalying)) },
                icon = { Icon(Icons.Outlined.PlayArrow, "Localized description") },
                onClick = { /*TODO*/ },
                containerColor = getColorTonesList(Color(mute.value.toInt()))[7]
            )
            Spacer(modifier = Modifier.weight(0.1f, true))
        }
    }
}

fun netToLoacalBitmap(imgUrl: String?): Bitmap? {
    val bitmap: Bitmap?
    val `in`: InputStream?
    val out: BufferedOutputStream?
    return try {
        `in` = BufferedInputStream(URL(imgUrl).openStream(), 1024)
        val dataStream = ByteArrayOutputStream()
        out = BufferedOutputStream(dataStream, 1024)
        copy(`in`, out)
        out.flush()
        val data: ByteArray? = dataStream.toByteArray()
        bitmap = BitmapFactory.decodeByteArray(data, 0, data!!.size)
        bitmap
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

@Throws(IOException::class)
private fun copy(`in`: InputStream?, out: OutputStream?) {
    val b = ByteArray(1024)
    var read: Int?
    while (`in`?.read(b).also { read = it } != -1) {
        out?.write(b, 0, read!!)
    }
}
