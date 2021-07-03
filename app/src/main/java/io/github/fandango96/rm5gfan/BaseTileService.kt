package io.github.fandango96.rm5gfan

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

open class BaseTileService : TileService() {
    protected fun processTile(isOff: Boolean) {
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
}
