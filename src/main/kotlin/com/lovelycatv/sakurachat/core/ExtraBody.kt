/*
 * Copyright 2026 lovelycat
 *
 * Use of this source code is governed by the Apache License, Version 2.0,
 * that can be found in the LICENSE file.
 *
 */

package com.lovelycatv.sakurachat.core

class ExtraBody(
    from: Map<String, Any?> = mapOf()
) : MutableMap<String, Any?> by from.toMutableMap() {
    fun getExtraBody(key: String): ExtraBody? {
        return when (val t = this[key]) {
            is ExtraBody -> t

            is Map<*, *> -> (if (t.keys.isEmpty()) {
                ExtraBody()
            } else {
                val key = t.keys.first()
                if (key is String) {
                    @Suppress("UNCHECKED_CAST")
                    ExtraBody(t as MutableMap<String, Any?>)
                } else {
                    ExtraBody(
                        from = t.mapNotNull {
                            it.key?.let { key ->
                                key.toString() to it.value
                            }
                        }.toMap()
                    )
                }
            }).also {
                this[key] = it
            }

            else -> null
        }
    }

    fun getNumber(key: String): Number? {
        return this[key] as Number?
    }

    fun getString(key: String): String? {
        return this[key] as String?
    }

    fun getInt(key: String): Int? {
        return this[key] as Int?
    }

    fun getLong(key: String): Long? {
        return this[key] as Long?
    }

    fun getDouble(key: String): Double? {
        return this[key] as Double?
    }

    fun getBoolean(key: String): Boolean? {
        return this[key] as Boolean?
    }

    fun getByte(key: String): Byte? {
        return this[key] as Byte?
    }

    fun getShort(key: String): Short? {
        return this[key] as Short?
    }

    fun getChar(key: String): Char? {
        return this[key] as Char?
    }

    fun getFloat(key: String): Float? {
        return this[key] as Float?
    }
}