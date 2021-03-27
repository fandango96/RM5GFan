package io.github.fandango_.rm5gfan

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.preference.PreferenceManager

class SmartFanTileService : TileService() {
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

            Runtime.getRuntime().exec(
                    arrayOf(
                            "su",
                            "-c",
                            "echo",
                            "${if (it) 0 else 1}",
                            ">",
                            "/sys/kernel/fan/fan_smart"
                    )
            )

            processTile(it)
        }
    }

    companion object {
        private const val PREF_KEY = "smart_fan_off"
    }
}
