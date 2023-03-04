package com.heyi7086.glyphtorch

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class GlyphTileService: TileService() {

    private val led = LedManager

    // Called when your app can update your tile.
    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()

        led.toggle()
        updateTile()
    }

    private fun updateTile() {
        if (led.getBrightness() == 0) {
            qsTile.state = Tile.STATE_INACTIVE
            qsTile.icon = Icon.createWithResource(this, R.drawable.baseline_flash_off_24)
        } else {
            qsTile.state = Tile.STATE_ACTIVE
            qsTile.icon = Icon.createWithResource(this, R.drawable.baseline_flash_on_24)
        }
        qsTile.updateTile()
    }
}