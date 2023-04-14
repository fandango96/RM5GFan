package io.github.fandango_.rm5gfan

import android.graphics.Color
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.TypedValue
import androidx.core.graphics.drawable.toBitmap
import com.amulyakhare.textdrawable.TextDrawable
import java.util.*

class GSIFanTileService : TileService() {
    override fun onStartListening() {
        processTile(getFanSpeed())
    }

    private fun getFanSpeed(): Int {
        return Scanner(Runtime.getRuntime().exec(
            arrayOf(
                "su",
                "-c",
                "cat",
                "/sys/kernel/fan/fan_speed_level"
            )
        ).inputStream).nextInt()
    }

    private fun processTile(fanSpeed: Int) {
        qsTile.run {
            state = if (fanSpeed == 0) Tile.STATE_INACTIVE else Tile.STATE_ACTIVE
            val fanSpeedString = when (fanSpeed) {
                0 -> getString(R.string.cooling_fan_off)
                3 -> getString(R.string.low)
                4 -> getString(R.string.medium)
                else -> getString(R.string.high)
            }
            contentDescription = fanSpeedString
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                24f,
                resources.displayMetrics
            )
            icon = Icon.createWithBitmap(TextDrawable.builder().beginConfig().apply {
                width(px.toInt())
                height(px.toInt())
            }.endConfig().buildRound(fanSpeedString.substring(0, 1), Color.TRANSPARENT).toBitmap())

            updateTile()
        }
    }

    private fun setFanSpeed(fanSpeed: Int) {
        Runtime.getRuntime().exec(
            arrayOf(
                "su",
                "-c",
                "echo",
                "$fanSpeed",
                ">",
                "/sys/kernel/fan/fan_speed_level"
            )
        )
    }

    override fun onClick() {
        val newFanSpeed = when(val oldFanSpeed = getFanSpeed()) {
            0 -> 3
            else -> (oldFanSpeed + 1) % 6
        }
        setFanSpeed(newFanSpeed)
        processTile(newFanSpeed)
    }
}
