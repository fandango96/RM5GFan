package io.github.fandango96.rm5gfan

import android.provider.Settings
import android.service.quicksettings.Tile

class CoolingFanTileService : BaseTileService() {
    override fun onStartListening() {
        processTile(Settings.Global.getInt(contentResolver, "game_fan_off_on") == 0)
    }

    override fun onClick() {
        (if (qsTile.state == Tile.STATE_ACTIVE) 0 else 1).let {
            Settings.Global.putInt(contentResolver, "game_fan_off_on", it)
            processTile(it == 0)
        }
    }
}
