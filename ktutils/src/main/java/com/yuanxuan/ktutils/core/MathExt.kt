package com.yuanxuan.ktutils.core

fun lerp(value: Int, lowerValue: Int, upperValue: Int): Int {
    if (value < lowerValue) {
        return lowerValue
    } else if (value > upperValue) {
        return upperValue
    }
    return value
}

fun lerp(value: Float, lowerValue: Float, upperValue: Float): Float {
    if (value < lowerValue) {
        return lowerValue
    } else if (value > upperValue) {
        return upperValue
    }
    return value
}