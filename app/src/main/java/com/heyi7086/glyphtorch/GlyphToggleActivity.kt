package com.heyi7086.glyphtorch

import android.app.Activity
import android.os.Bundle
import android.util.Log

class GlyphToggleActivity: Activity() {
    private val led = LedManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val result = led.toggle()
        Log.d("GlyphTorch", "Glyph toggled: $result")
        finish()
    }
}
