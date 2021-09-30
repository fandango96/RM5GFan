package io.github.fandango96.rm5gfan

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.provider.Settings
import android.service.quicksettings.Tile

class ChargeSeparationTileService : BaseTileService() {
    object Power {
        fun isConnected(context: Context): Boolean {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
            return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
        }
    }

    override fun onStartListening() {
        if (!Power.isConnected(this)) {
            qsTile.run {
                state = Tile.STATE_UNAVAILABLE
                updateTile()
            }
        } else {
            processTile(Settings.Global.getInt(contentResolver, "charge_separation_switch") == 0)
        }
    }

    override fun onClick() {
        (if (qsTile.state == Tile.STATE_ACTIVE) 0 else 1).let {
            Settings.Global.putInt(contentResolver, "charge_separation_switch", it)
            processTile(it == 0)
        }
    }
}
