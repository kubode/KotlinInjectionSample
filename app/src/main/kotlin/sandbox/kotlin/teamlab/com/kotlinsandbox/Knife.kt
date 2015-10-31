package sandbox.kotlin.teamlab.com.kotlinsandbox

import android.app.Activity
import android.app.Application
import android.app.Fragment
import android.content.Context
import android.view.View
import java.util.*
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

public interface HasObjectGraph {
    var objectGraph: ObjectGraph
}

private class NotFoundException(detailMessage: String?) : RuntimeException(detailMessage)

public class ObjectGraph(private val parent: ObjectGraph? = null) {
    private val graph: MutableMap<Class<*>, MutableMap<String?, Wrapper<*>>> = HashMap()

    private fun <V : Any> add(clazz: KClass<V>, name: String? = null, type: Type, initializer: () -> V) {
        graph.getOrPut(clazz.java, { HashMap() }).put(name, type.wrap(initializer))
    }

    public fun <V : Any> provide(clazz: KClass<V>, initializer: () -> V, name: String? = null) {
        add(clazz, name, Type.NORMAL, initializer)
    }

    public fun <V : Any> provideSingleton(clazz: KClass<V>, initializer: () -> V, name: String? = null) {
        add(clazz, name, Type.SINGLETON, initializer)
    }

    public fun <V : Any> get(clazz: KClass<V>, name: String? = null): V {
        @Suppress("UNCHECKED_CAST")
        return graph[clazz.java]?.get(name)?.value as V?
                ?: parent?.get(clazz, name)
                ?: throw NotFoundException("${clazz.java}(${name ?: "No name"}) is not found.")
    }

    private enum class Type(internal val wrap: (() -> Any) -> Wrapper<*>) {
        NORMAL({ NormalWrapper(it) }), SINGLETON({ SingletonWrapper(it) })
    }

    private interface Wrapper<V> {
        val value: V
    }

    private class NormalWrapper<V>(private val creator: () -> V) : Wrapper<V> {
        override val value: V
            get() = creator()

        override fun toString(): String {
            return "$value"
        }
    }

    private class SingletonWrapper<V>(creator: () -> V) : Wrapper<V> {
        override val value: V by lazy { creator() }
        override fun toString(): String {
            return "$value(Singleton)"
        }
    }

    override fun toString(): String {
        return "{parent: $parent, graph: {$graph}}"
    }
}

public fun Context.findObjectGraph(): ObjectGraph {
    if (this is HasObjectGraph) {
        return this.objectGraph
    }
    val application = this.applicationContext
    if (application is HasObjectGraph) {
        return application.objectGraph
    }
    throw  NotFoundException("${ObjectGraph::class.java.simpleName} is not found in $this.")
}

private val Application.objectGraph: ObjectGraph by Lazy { it.findObjectGraph() }
private val Activity.objectGraph: ObjectGraph by Lazy { it.application.findObjectGraph() }
private val Fragment.objectGraph: ObjectGraph by Lazy { it.activity.findObjectGraph() }
private val View.objectGraph: ObjectGraph by Lazy { it.context.findObjectGraph() }

public fun <V : Any> Application.inject(clazz: KClass<V>, name: String? = null)
        = lazy { objectGraph.get(clazz, name) }

public fun <V : Any> Activity.inject(clazz: KClass<V>, name: String? = null)
        = lazy { objectGraph.get(clazz, name) }

public fun <V : Any> Fragment.inject(clazz: KClass<V>, name: String? = null)
        = lazy { objectGraph.get(clazz, name) }

public fun <V : Any> View.inject(clazz: KClass<V>, name: String? = null)
        = lazy { objectGraph.get(clazz, name) }

// Like Kotlin's lazy delegate but the initializer gets the target and metadata passed to it
internal class Lazy<T, V>(private val initializer: (T) -> V) : ReadOnlyProperty<T, V> {
    private object EMPTY

    @Suppress("UNCHECKED_CAST")
    private var value: V = EMPTY as V

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (value == EMPTY) {
            value = initializer(thisRef)
        }
        return value
    }
}
