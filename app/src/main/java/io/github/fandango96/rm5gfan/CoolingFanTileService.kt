package io.github.fandango96.rm5gfan

import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class CoolingFanTileService : TileService() {
    override fun onStartListening() {
        processTile(Settings.Global.getInt(contentResolver, "game_fan_off_on") == 0)
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
        (if (qsTile.state == Tile.STATE_ACTIVE) 0 else 1).let {
            Settings.Global.putInt(contentResolver, "game_fan_off_on", it)
            processTile(it == 0)
        }
    }
}
