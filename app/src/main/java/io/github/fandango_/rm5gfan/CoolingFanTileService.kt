package io.github.fandango_.rm5gfan

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import java.util.*

class CoolingFanTileService : TileService() {
    override fun onStartListening() {
        Scanner(Runtime.getRuntime().exec(
            arrayOf(
                "su",
                "-c",
                "content",
                "query",
                "--uri",
                "content://settings/global",
                "--projection",
                "value",
                "--where",
                "\"name='game_fan_off_on'\""
            )
        ).inputStream).nextLine().let {
            processTile(it.last() == '0')
        }
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
            Runtime.getRuntime().exec(
                arrayOf(
                    "su",
                    "-c",
                    "content",
                    "insert",
                    "--uri",
                    "content://settings/global",
                    "--bind",
                    "name:s:game_fan_off_on",
                    "--bind",
                    "value:i:$it"
                )
            )
            processTile(it == 0)
        }
    }
}
