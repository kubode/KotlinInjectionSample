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
        val s = System.nanoTime()
        val parent = ObjectGraph()
        parent.provide(String::class, { "normal(${Math.random()})" })
        parent.provideSingleton(Int::class, { 1 }, "named1")
        parent.provideSingleton(Int::class, { 2 }, "named2")
        val objectGraph = ObjectGraph(parent)
        objectGraph.provideSingleton(Int::class, { 22222 }, "named2")
        objectGraph.provideSingleton(Double::class, { Math.random() })
        objectGraph.provideSingleton(CharSequence::class, { "${objectGraph.get(String::class)}aaa" })
        this.objectGraph = objectGraph

        Log.v("App", "$normalString1")
        Log.v("App", "$normalString1")
        Log.v("App", "$normalString2")
        Log.v("App", "$normalString2")
        Log.v("App", "$namedInt1")
        Log.v("App", "$namedInt2")
        Log.v("App", "$singletonDouble1")
        Log.v("App", "$singletonDouble2")
        Log.v("App", "$singletonCharSeq")
        Log.v("App", "${System.nanoTime() - s}")
        Log.v("App", "$objectGraph")
    }
}
