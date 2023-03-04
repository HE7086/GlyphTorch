package com.heyi7086.glyphtorch

import com.topjohnwu.superuser.Shell

const val BRIGHT1 = "/sys/devices/platform/soc/984000.i2c/i2c-0/0-0020/leds/aw210xx_led/all_white_leds_br"
const val BRIGHT2 = "/sys/devices/platform/soc/984000.i2c/i2c-0/0-0020/leds/aw210xx_led/glo_current"

object LedManager {
    init {
        Shell.getShell()
        Shell.cmd("echo 4095 > $BRIGHT1").exec()
    }

    fun setBrightness(value: Int) {
        Shell.cmd("echo $value > $BRIGHT2").exec()
    }

    fun getBrightness(): Int {
        val result = Shell.cmd("cat $BRIGHT2").exec()
        return result.out.last().toInt()
    }

    fun toggle() {
        val brightness = getBrightness()
        if (brightness == 0) {
            setBrightness(255)
        } else {
            setBrightness(0)
        }
    }
}