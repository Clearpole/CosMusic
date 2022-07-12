package com.clearpole.cosmusic

import android.os.Bundle
import android.text.TextUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.clearpole.cosmusic.ui.theme.CosMusicTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL


class MainActivity : ComponentActivity() {
    private var inputText = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            CosMusicTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box {
                        Column {
                            NewSongList()
                        }
                        BottomAppBarHome()
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
    fun NewSongList() {
        LazyColumn {
            item { Column(modifier = Modifier.height(100.dp)) {
                Spacer(modifier = Modifier.height(50.dp))
                Column(modifier = Modifier.height(60.dp)) {
                    Column() {
                        SmallTopAppBar(
                            title = { Text("") },
                            navigationIcon = {
                                IconButton(onClick = { /* doSomething() */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "Localized description"
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { /* doSomething() */ }) {
                                    Icon(
                                        imageVector = Icons.Filled.Favorite,
                                        contentDescription = "Localized description"
                                    )
                                }
                            }
                        )
                    }
                }
            } }
            item { Column(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                Spacer(modifier = Modifier.height(90.dp))
                Row() {
                    Spacer(modifier = Modifier.width(20.dp))
                    Text(text = "CosMusic",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold)
                }
            } }
            if (!TextUtils.isEmpty(inputText.value)) {
                val data =
                    JSONObject(inputText.value).getJSONObject("songs").getJSONArray("list")
                items(data.length()){
                    val everyInfo = data.getJSONObject(it)
                    val file = everyInfo.getString("filename")
                    val songName = file.substring(file.indexOf(" - ")+3 until file.length)
                    val artist = file.substring(0 until  file.indexOf(" -"))
                    val img = everyInfo.getString("album_sizable_cover").replace("{size}","150")
                    val hash = everyInfo.getString("hash")
                    HomeSongCard(imgUrl = img , songName = songName , artist = artist , hash = hash)
                }
            }
        }
    }
}
@Composable
fun HomeSongCard(imgUrl:String,songName:String,artist:String,hash:String){
    Row(modifier = Modifier
        .padding(all = 15.dp)
        .fillMaxSize()
        .height(65.dp)) {
        AsyncImage(model = imgUrl.replace("http","https"),
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(shape = RoundedCornerShape(15.dp)))
        Spacer(modifier = Modifier.width(15.dp))

        Column {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = songName)
            // Add a vertical space between the author and message texts
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = artist)
        }
    }
}


@Composable
fun BottomAppBarHome() {
    Row(Modifier.fillMaxSize()) {
        BottomAppBar(modifier = Modifier.align(Alignment.Bottom)) {
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
                onClick = { /*TODO*/ })
            Spacer(modifier = Modifier.weight(0.1f, true))
        }
    }
}

