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
    override fun onCreate() {
        super.onCreate()
        val s = System.nanoTime()
        val objectGraph = ObjectGraph()
        objectGraph.add(String::class, null, ObjectGraph.Type.NORMAL, { "normal(${Math.random()})" })
        objectGraph.add(Int::class, "named1", ObjectGraph.Type.SINGLETON, { 1 })
        objectGraph.add(Int::class, "named2", ObjectGraph.Type.SINGLETON, { 2 })
        objectGraph.add(Double::class, null, ObjectGraph.Type.SINGLETON, { Math.random() })
        this.objectGraph = objectGraph

        Log.v("App", "$normalString1")
        Log.v("App", "$normalString1")
        Log.v("App", "$normalString2")
        Log.v("App", "$normalString2")
        Log.v("App", "$namedInt1")
        Log.v("App", "$namedInt2")
        Log.v("App", "$singletonDouble1")
        Log.v("App", "$singletonDouble2")
        Log.v("App", "${System.nanoTime() - s}")
    }
}
