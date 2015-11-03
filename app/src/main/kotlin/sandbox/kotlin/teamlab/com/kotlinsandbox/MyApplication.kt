package sandbox.kotlin.teamlab.com.kotlinsandbox

import android.app.Application
import android.util.Log

public class MyApplication : Application(), HasObjectGraph {
    override lateinit var objectGraph: ObjectGraph

    val normalString1: String by inject(String::class)
    val normalString2: String by inject(String::class)
    val namedInt1: Int by inject(Int::class, "named1")
    val namedInt2: Int by inject(Int::class, "named2")
    val singletonDouble1: Double by inject(Double::class)
    val singletonDouble2: Double by inject(Double::class)
    val singletonCharSeq: CharSequence by inject(CharSequence::class)
    override fun onCreate() {
        super.onCreate()

        val timer = { System.currentTimeMillis() }
        val s = timer()

        objectGraph = ObjectGraph(ObjectGraph().add(Module1())).add(Module2())

        Log.v("App", "$normalString1")
        Log.v("App", "$normalString1")
        Log.v("App", "$normalString2")
        Log.v("App", "$normalString2")
        Log.v("App", "$namedInt1")
        Log.v("App", "$namedInt2")
        Log.v("App", "$singletonDouble1")
        Log.v("App", "$singletonDouble2")
        Log.v("App", "$singletonCharSeq")
        Log.v("App", "${timer() - s}")
        Log.v("App", "$objectGraph")
    }
}

class Module1 : Module() {
    init {
        provide(String::class, { "normal(${Math.random()})" })
        provideSingleton(Int::class, { 1 }, "named1")
        provideSingleton(Int::class, { 2 }, "named2")
        provideSingleton(Double::class, { Math.random() })
        provideSingleton(CharSequence::class, { "${it.get(String::class)}aaa" })
    }
}

class Module2 : Module() {
    init {
        provideSingleton(Int::class, { 22222 }, "named2")
        provideSingleton(Double::class, { Math.random() })
        provideSingleton(CharSequence::class, { "${it.get(String::class)}aaa" })
    }
}
