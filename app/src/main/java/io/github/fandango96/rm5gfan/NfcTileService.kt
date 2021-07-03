package io.github.fandango96.rm5gfan

import android.nfc.NfcAdapter
import android.service.quicksettings.Tile

class NfcTileService : BaseTileService() {
    override fun onStartListening() {
        processTile(!checkNfcPowerStatus())
    }

    private fun checkNfcPowerStatus() =
        NfcAdapter.getDefaultAdapter(this)?.isEnabled ?: false

    override fun onClick() {
        (qsTile.state != Tile.STATE_ACTIVE).let {
            NfcAdapter.getDefaultAdapter(this)?.run {
                Class.forName(javaClass.name).getDeclaredMethod(if (it) "enable" else "disable")
                    .apply {
                        isAccessible = true
                        invoke(this@run)
                    }
            }
            processTile(!it)
        }
    }
}
