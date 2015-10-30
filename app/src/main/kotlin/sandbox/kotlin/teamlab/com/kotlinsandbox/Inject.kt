package sandbox.kotlin.teamlab.com.kotlinsandbox

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.util.Log
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import android.support.v4.app.Fragment as SupportFragment

public interface HasObjectGraph {
    var objectGraph: ObjectGraph
}

public class ObjectGraph(private val parent: ObjectGraph? = null) {
    private val graph: MutableMap<Class<*>, MutableMap<String?, Wrapper<Any>>> = HashMap()

    public fun <V : Any> add(clazz: KClass<V>, name: String? = null, type: Type, creator: () -> V) {
        graph.getOrPut(clazz.java, { hashMapOf() }).put(name, type.createWrapper(creator))
    }

    fun <V : Any> get(clazz: KClass<V>, name: String? = null): V? {
        Log.v("Inj", "$graph")
        Log.v("Inj", "$parent")
        @Suppress("UNCHECKED_CAST")
        return graph[clazz.java]?.get(name)?.value as V? ?: parent?.get(clazz, name)
    }

    public enum class Type(val createWrapper: (() -> Any) -> Wrapper<Any>) {
        NORMAL({ NormalWrapper(it) }), SINGLETON({ SingletonWrapper(it) })
    }

    private interface Wrapper<V : Any> {
        val value: V
    }

    private class NormalWrapper<V : Any>(private val creator: () -> V) : Wrapper<V> {
        override val value: V
            get() = creator()

        override fun toString(): String {
            return "Normal($value)"
        }
    }

    private class SingletonWrapper<V : Any>(private val creator: () -> V) : Wrapper<V> {
        override val value: V by lazy { creator() }
        override fun toString(): String {
            return "Singleton($value)"
        }
    }
}

public fun <V : Any> Application.inject(clazz: KClass<V>, name: String? = null): ReadOnlyProperty<Application, V>
        = NotNullLazy { type, property -> objectGraph.get(clazz, name) }

public fun <V : Any> Activity.inject(clazz: KClass<V>): ReadOnlyProperty<Activity, V>
        = NotNullLazy { type, property -> objectGraph.get(clazz) }

public fun <V : Any> Fragment.inject(clazz: KClass<V>): ReadOnlyProperty<Fragment, V>
        = NotNullLazy { type, property -> objectGraph.get(clazz) }

public fun <V : Any> SupportFragment.inject(clazz: KClass<V>): ReadOnlyProperty<SupportFragment, V>
        = NotNullLazy { type, property -> objectGraph.get(clazz) }

private fun find(application: Application): ObjectGraph? {
    if (application is HasObjectGraph) {
        return application.objectGraph
    }
    return null
}

private fun find(activity: Activity): ObjectGraph? {
    if (activity is HasObjectGraph) {
        return activity.objectGraph
    }
    return find(activity.application)
}

private val Application.objectGraph: ObjectGraph by NotNullLazy { application, property ->
    find(application)
}

private val Activity.objectGraph: ObjectGraph by NotNullLazy { activity, property ->
    find(activity.application)
}

private val Fragment.objectGraph: ObjectGraph by NotNullLazy { fragment, property ->
    find(fragment.activity)
}

private val SupportFragment.objectGraph: ObjectGraph by NotNullLazy { fragment, property ->
    find(fragment.activity)
}

private class NotNullLazy<T, V>(private val initializer: (T, KProperty<*>) -> V?) : ReadOnlyProperty<T, V> {
    private var value: V? = null

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (value == null) {
            value = initializer(thisRef, property)
            if (value == null) {
                throw RuntimeException("$thisRef#${property.name} initialized by null.")
            }
        }
        return value!!
    }
}
