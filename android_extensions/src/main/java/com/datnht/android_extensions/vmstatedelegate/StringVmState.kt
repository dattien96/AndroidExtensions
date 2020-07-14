package com.datnht.android_extensions.vmstatedelegate

import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class StringVmState constructor(
    private val stateHandle: SavedStateHandle,
    private val key: String,
    private val defaultValue: String
) : ReadWriteProperty<Any, String> {

    override fun getValue(thisRef: Any, property: KProperty<*>): String =
        stateHandle.get<String>(key) ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        stateHandle.set(key, value)
    }

}