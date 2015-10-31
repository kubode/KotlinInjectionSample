package sandbox.kotlin.teamlab.com.kotlinsandbox

import android.support.v4.app.Fragment
import kotlin.reflect.KClass

private val Fragment.objectGraph: ObjectGraph by Lazy { it.activity.findObjectGraph() }
public fun <V : Any> Fragment.inject(clazz: KClass<V>, name: String? = null)
        = lazy { objectGraph.get(clazz, name) }
