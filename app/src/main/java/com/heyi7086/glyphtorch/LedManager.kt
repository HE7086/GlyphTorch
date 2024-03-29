package com.heyi7086.glyphtorch

import com.topjohnwu.superuser.Shell

const val BASE = "/sys/devices/platform/soc/984000.i2c/i2c-0/0-0020/leds/aw210xx_led"
const val BRIGHT1 = "all_white_leds_br"
const val BRIGHT2 = "glo_current"

const val SWITCH = "all_leds_effect"

// top left led around the camera
const val REAR_CAM = "rear_cam_led_br"

// top right led
const val FRONT_CAM = "front_cam_led_br"

// round led around the coil
const val ROUND = "round_leds_br"

// bottom center led line
const val VLINE = "vline_leds_br"

// bottom center led dot
const val DOT = "dot_led_br"

object LedManager {
    init {
        initialize()
    }

    fun initialize(): Boolean {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        val shell = Shell.getShell()

        return shell.isRoot
    }

    fun setBrightness(value: Int) {
        Shell.cmd("echo $value > $BASE/$BRIGHT1").exec()
    }

    fun getBrightness(): Int {
        val result = Shell.cmd("cat $BASE/$BRIGHT1").exec()
        return result.out.last().toInt()
    }

    fun toggle(): Boolean {
        val brightness = getBrightness()
        return if (brightness == 0) {
            setBrightness(4095)
            true
        } else {
            setBrightness(0)
            false
        }
    }

    fun reset() {
        Shell.cmd("echo 4095 > $BASE/$BRIGHT1").exec()
        Shell.cmd("echo 255 > $BASE/$BRIGHT2").exec()
        Shell.cmd("echo 0 > $BASE/$SWITCH").exec()
    }
}