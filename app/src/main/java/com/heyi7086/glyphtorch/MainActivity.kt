package com.heyi7086.glyphtorch

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.heyi7086.glyphtorch.ui.theme.GlyphTorchTheme
import com.topjohnwu.superuser.Shell

class MainActivity : ComponentActivity() {

    init {
        Shell.enableVerboseLogging = true
        Shell.getShell()
    }

    private val led = LedManager
    private var glyphOn = false
    private var brightness = 255f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlyphTorchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column {
                        BrightnessSlider(Modifier.padding(16.dp))
                        ActivateButton(Modifier.fillMaxSize().padding(40.dp))
                    }
                }
            }
        }

        val brand = android.os.Build.BRAND
        val model = android.os.Build.MODEL
        // are there any other models?
        if (brand != "Nothing" || model != "A063") {
            Toast.makeText(this,
                "It seems that you are not using Nothing Phone (1).\n" +
                        "This app will unlikely to work.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        led.setBrightness(0)
    }

    @Composable
    fun BrightnessSlider(modifier: Modifier = Modifier) {
        var sliderValue by remember {
            mutableStateOf(brightness)
        }
        Slider(
            value = sliderValue,
            valueRange = 0f..255f,
            steps = 10,
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
                    }),
                contentDescription = "Torch Status",
                Modifier.size(40.dp)
            )
            Text(
                text = "Torch",
                fontSize = 40.sp
            )
        }
    }
}