package io.github.fandango_.rm5gfan

import android.nfc.NfcAdapter
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class NfcTileService : TileService() {
    override fun onStartListening() {
        NfcAdapter.getDefaultAdapter(this).isEnabled.let {
            processTile(!it)
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
        (qsTile.state == Tile.STATE_ACTIVE).let {
            Runtime.getRuntime().exec(
                arrayOf(
                    "su",
                    "-c",
                    "svc",
                    "nfc",
                    if (it) "disable" else "enable"
                )
            )
            processTile(it)
        }
    }
}
