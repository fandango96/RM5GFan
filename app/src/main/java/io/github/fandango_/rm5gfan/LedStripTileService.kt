package io.github.fandango_.rm5gfan

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.preference.PreferenceManager
import java.lang.reflect.Method

class LedStripTileService : TileService() {
    override fun onStartListening() {
        processTile(PreferenceManager.getDefaultSharedPreferences(this)
            .getBoolean(PREF_KEY, true))
    }

    private fun processTile(isOff: Boolean) {
        qsTile.run {
            if (isOff) {
                contentDescription = getString(R.string.cooling_fan_off)
                state = Tile.STATE_INACTIVE
            } else {
                contentDescription = getString(R.string.cooling_fan_on)
                state = Tile.STATE_ACTIVE
            }

            updateTile()
        }
    }

    override fun onClick() {
        (qsTile.state == Tile.STATE_ACTIVE).let {
            PreferenceManager.getDefaultSharedPreferences(this).edit().run {
                putBoolean(PREF_KEY, it)
                apply()
            }

            val getDeclaredMethod = Class::class.java.getDeclaredMethod("getDeclaredMethod", String::class.java, arrayOf<Class<*>>()::class.java)
            val nubiaLedClass = Class.forName("nubia.hardware.ColorfulLightManager")
            val previewColorfulLight = getDeclaredMethod.invoke(nubiaLedClass, "previewColorfulLight", arrayOf(Int::class.java, Boolean::class.java)) as Method
            previewColorfulLight.invoke(null, 0, !it)

            processTile(it)
        }
    }

    companion object {
        private const val PREF_KEY = "led_strip_off"
    }
}
