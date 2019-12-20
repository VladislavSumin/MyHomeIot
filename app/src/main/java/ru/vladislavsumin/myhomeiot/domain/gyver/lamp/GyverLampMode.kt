package ru.vladislavsumin.myhomeiot.domain.gyver.lamp

enum class GyverLampMode(
    id: Int
) {
    sparklesRoutine(0),
    fireRoutine(1),
    rainbowVertical(2),
    rainbowHorizontal(3),
    colorsRoutine(4),
    madnessNoise(5),
    cloudNoise(6),
    lavaNoise(7),
    plasmaNoise(8),
    rainbowNoise(9),
    rainbowStripeNoise(10),
    zebraNoise(11),
    forestNoise(12),
    oceanNoise(13),
    colorRoutine(14),
    snowRoutine(15),
    matrixRoutine(16),
    lightersRoutine(17);
}