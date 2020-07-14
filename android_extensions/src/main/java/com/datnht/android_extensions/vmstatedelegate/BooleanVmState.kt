package com.datnht.android_extensions.vmstatedelegate

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class BooleanVmState constructor(
    private val stateHandle: SavedStateHandle,
    private val key: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean =
        stateHandle.get<Boolean>(key) ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        stateHandle.set(key, value)
    }

}