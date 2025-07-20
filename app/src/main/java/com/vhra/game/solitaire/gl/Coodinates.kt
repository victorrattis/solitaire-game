package com.vhra.game.solitaire.gl

data class Vec2(val x: Float, val y: Float) {
    operator fun minus(a: Vec2): Vec2 = Vec2(x - a.x, y - a.y)
    fun toArray(): FloatArray = floatArrayOf(x, y)
}

data class Vec3(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f) {
    fun toArray(): FloatArray = floatArrayOf(x, y, z)
    operator fun minus(a: Vec3): Vec3 = Vec3(x - a.x, y - a.y, z - a.z)
    constructor(xy: Vec2, z: Float) : this(xy.x, xy.y, z)
}

data class Vec4(val x: Float = 0f, val y: Float = 0f, val z: Float = 0f, val w: Float = 0f) {
    companion object {
        fun toArray() = FloatArray(4)
    }
    operator fun minus(a: Vec4): Vec4 = Vec4(x - a.x, y - a.y, z - a.z, w - a.w)
}

data class Vec4i(val x: Int = 0, val y: Int = 0, val z: Int = 0, val w: Int = 0) {
    companion object {
        fun toArray() = IntArray(4)
    }
}

fun FloatArray.x(): Float = this[0]
fun FloatArray.y(): Float = this[1]
fun FloatArray.z(): Float = this[2]
fun FloatArray.w(): Float = this[3]

fun FloatArray.toVec2() = Vec2(this[0], this[1])
fun FloatArray.toVec3() = Vec3(this[0], this[1], this[2])
fun FloatArray.toVec4() = Vec4(this[0], this[1], this[2], this[3])
