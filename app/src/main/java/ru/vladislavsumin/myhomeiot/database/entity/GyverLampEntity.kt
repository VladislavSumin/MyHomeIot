package ru.vladislavsumin.myhomeiot.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "iot_gyver_lamps")
@TypeConverters(value = [GyverLampEntity.DeviceTypeConverter::class])
data class GyverLampEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = DEFAULT_NAME,
    val host: String = "",
    val port: Int = 8888,
    val deviceType: DeviceType
) {
    companion object {
        const val DEFAULT_NAME = "GyverLamp"
    }

    enum class DeviceType(val id: Int) {
        GYVER_LAMP_ORIGIN(1),
        GYVER_LAMP_GUNNER47(2),

        UNKNOWN(0)
    }

    class DeviceTypeConverter {
        @TypeConverter
        fun toDeviceType(value: Int): DeviceType {
            val deviceType = DeviceType.values().find { it.id == value }
            return deviceType ?: DeviceType.UNKNOWN
        }

        @TypeConverter
        fun fromDeviceType(value: DeviceType): Int {
            return value.id
        }
    }
}
