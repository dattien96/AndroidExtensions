package com.datnht.android_extensions.vmstatedelegate

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ParcelableVmState constructor(
    private val stateHandle: SavedStateHandle,
    private val key: String,
    private val defaultValue: Parcelable? = null
) : ReadWriteProperty<Any, Parcelable?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Parcelable? =
        stateHandle.get<Parcelable>(key) ?: defaultValue

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Parcelable?) {
        stateHandle.set(key, value)
    }

}