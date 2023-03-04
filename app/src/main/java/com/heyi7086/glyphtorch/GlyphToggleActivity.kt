package com.heyi7086.glyphtorch

import android.app.Activity
import android.os.Bundle

class GlyphToggleActivity: Activity() {
    private val led = LedManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        led.toggle()
    }
}