package io.github.fandango_.rm5gfan

import android.graphics.Color
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.TypedValue
import androidx.core.graphics.drawable.toBitmap
import com.amulyakhare.textdrawable.TextDrawable
import java.util.*

class RefreshTileService : TileService() {
    override fun onStartListening() {
        processTile(getRefreshRate())
    }

    private fun getRefreshRate() =
        Scanner(Runtime.getRuntime().exec(
        arrayOf(
            "su",
            "-c",
            "content",
            "query",
            "--uri",
            "content://settings/system",
            "--projection",
            "value",
            "--where",
            "\"name='db_screen_rate'\""
        )
    ).inputStream).nextLine().last() - '0'

    private fun processTile(refreshRate: Int) {
        qsTile.run {
            state = Tile.STATE_INACTIVE
            val refreshString = when (refreshRate) {
                0 -> getString(R.string.sixtyHz)
                1 -> getString(R.string.ninetyHz)
                2 -> getString(R.string.oneTwentyHz)
                else -> getString(R.string.oneFortyFourHz)
            }
            contentDescription = refreshString
            val px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                24f,
                resources.displayMetrics
            )
            icon = Icon.createWithBitmap(TextDrawable.builder().beginConfig().apply {
                width(px.toInt())
                height(px.toInt())
            }.endConfig().buildRound(refreshString, Color.TRANSPARENT).toBitmap())

            updateTile()
        }
    }

    private fun setRefreshRate(refreshRate: Int) {
        Runtime.getRuntime().exec(
            arrayOf(
                "su",
                "-c",
                "content",
                "insert",
                "--uri",
                "content://settings/system",
                "--bind",
                "name:s:db_screen_rate",
                "--bind",
                "value:i:$refreshRate"
            )
        )
    }

    override fun onClick() {
        val newRefreshRate = (getRefreshRate() + 1) % 4
        setRefreshRate(newRefreshRate)
        processTile(newRefreshRate)
    }
}
