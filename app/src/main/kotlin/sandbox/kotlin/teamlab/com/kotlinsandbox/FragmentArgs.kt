package sandbox.kotlin.teamlab.com.kotlinsandbox

import android.app.Fragment
import android.os.Bundle
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Fragment.intArg(): ReadWriteProperty<Fragment, Int> {
    return BundleProperty({ argumentsSafe }, Bundle::getInt, Bundle::putInt)
}

fun Fragment.stringArg(): ReadWriteProperty<Fragment, String> {
    return BundleProperty({ argumentsSafe }, Bundle::getString, Bundle::putString)
}

private val Fragment.argumentsSafe: Bundle
    get() = arguments ?: Bundle().apply { arguments = this }

private class BundleProperty<T, V>(private val bundleGetter: () -> Bundle,
                                   private val get: (Bundle, String) -> V,
                                   private val put: (Bundle, String, V) -> Unit)
: ReadWriteProperty<T, V> {
    private object EMPTY

    @Suppress("UNCHECKED_CAST")
    private var value: V = EMPTY as V

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (value == EMPTY) {
            value = get(bundleGetter(), property.name)
        }
        return value
    }

    override fun setValue(thisRef: T, property: KProperty<*>, value: V) {
        this.value = value
        put(bundleGetter(), property.name, value)
    }
}
