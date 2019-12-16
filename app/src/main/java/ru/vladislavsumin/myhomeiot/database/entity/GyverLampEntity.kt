package ru.vladislavsumin.myhomeiot.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "iot_gyver_lamps")
data class GyverLampEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = DEFAULT_NAME,
    val host: String = "",
    val port: Int = 8888
) {
    companion object {
        const val DEFAULT_NAME = "GyverLamp"
    }
}
