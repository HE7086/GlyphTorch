package com.heyi7086.glyphtorch

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heyi7086.glyphtorch.ui.theme.GlyphTorchTheme
import com.topjohnwu.superuser.Shell

class MainActivity : ComponentActivity() {

    init {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.getShell()
    }

    private val led = LedManager
    private var glyphOn = false
    private var brightness = 255f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlyphTorchTheme {
                MainContent()
            }
        }

        if (Shell.getCachedShell() == null || !Shell.getCachedShell()!!.isRoot) {
            Toast.makeText(
                this,
                "Root access denied, check your device",
                Toast.LENGTH_LONG
            ).show()
        }

        val brand = android.os.Build.BRAND
        val model = android.os.Build.MODEL
        // are there any other models?
        if (brand != "Nothing" || model != "A063") {
            Toast.makeText(
                this,
                "It seems that you are not using Nothing Phone (1)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        led.setBrightness(0)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainContent() {
        var showMenu by remember {
            mutableStateOf(false)
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            getString(R.string.app_name),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(
                                imageVector = Icons.Filled.MoreVert,
                                contentDescription = "Localized description"
                            )
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "",
                                        )
                                        Spacer(Modifier.size(8.dp))
                                        Text("Test")
                                    }
                                },
                                onClick = { /*TODO*/ }
                            )
                        }
                    }
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    BrightnessSlider(Modifier.padding(horizontal = 16.dp))
                    FlashLightButton(Modifier.width(300.dp))
                    ActivateButton(Modifier.width(300.dp))
                }
            }
        )
    }
    
    @Composable
    fun BrightnessSlider(modifier: Modifier = Modifier) {
        var sliderValue by remember {
            mutableStateOf(brightness)
        }
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Slider(
                value = sliderValue,
                valueRange = 0f..255f,
                onValueChange = {
                    sliderValue = it
                    brightness = it
                    if (glyphOn) {
                        led.setBrightness(brightness.toInt())
                    }
                },
                onValueChangeFinished = {
                    Log.d("GlyphTorch", "Slider changed $brightness")
                },
                modifier = modifier
            )
            Text("Brightness: ${brightness.toInt()}")
        }
    }

    @Composable
    fun ActivateButton(modifier: Modifier = Modifier) {
        var flag by remember {
            mutableStateOf(glyphOn)
        }

        Button(
            onClick = {
                flag = !flag
                glyphOn = flag
                if (glyphOn) {
                    led.setBrightness(brightness.toInt())
                } else {
                    led.setBrightness(0)
                }
                Log.d("GlyphTorch", "Button clicked $flag")
            },
            shape = RoundedCornerShape(10),
            modifier = modifier
        ) {
            Icon(
                painter = rememberVectorPainter(
                    image = if (flag) {
                        Icons.Default.Check
                    } else {
                        Icons.Default.Clear
                    }
                ),
                contentDescription = "Torch Status",
                Modifier.size(40.dp)
            )
            Text(
                text = "Glyph",
                fontSize = 40.sp
            )
        }

        Image(
            painter = painterResource(
                id = if (flag) {
                    R.drawable.glyphs_on
                } else {
                    R.drawable.glyphs_off
                }
            ),
            contentDescription = "Glyph Preview",
            modifier = modifier,
        )
    }

    @Composable
    fun FlashLightButton(modifier: Modifier = Modifier) {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            var flag by remember {
                mutableStateOf(false)
            }
            Button(
                modifier = modifier,
                onClick = {
                    flag = !flag
                    getSystemService(Context.CAMERA_SERVICE)?.let {
                        val cameraManager = it as CameraManager
                        val cameraId = cameraManager.cameraIdList[0]
                        cameraManager.setTorchMode(cameraId, flag)
                    }
                    Log.d("GlyphTorch", "Flashlight clicked $flag")
                },
                shape = RoundedCornerShape(10),
            ) {
                Icon(
                    painter = rememberVectorPainter(
                        image = if (flag) {
                            Icons.Default.Check
                        } else {
                            Icons.Default.Clear
                        }
                    ),
                    contentDescription = "Flashlight Status",
                    Modifier.size(40.dp)
                )
                Text(
                    text = "Flashlight",
                    fontSize = 40.sp
                )
            }
        }
    }
}