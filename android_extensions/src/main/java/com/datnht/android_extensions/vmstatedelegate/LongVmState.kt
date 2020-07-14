package com.datnht.android_extensions.vmstatedelegate

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class LongVmState constructor(
    private val stateHandle: SavedStateHandle,
    private val key: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any, Long> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Long =
        stateHandle.get<Long>(key) ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Long) {
        stateHandle.set(key, value)
    }

}