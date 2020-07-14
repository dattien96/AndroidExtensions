package com.datnht.android_extensions.vmstatedelegate

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class IntVmState constructor(
    private val stateHandle: SavedStateHandle,
    private val key: String,
    private val defaultValue: Int
) : ReadWriteProperty<Any, Int> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Int =
        stateHandle.get<Int>(key) ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        stateHandle.set(key, value)
    }

}