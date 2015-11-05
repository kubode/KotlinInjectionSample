package sandbox.kotlin.teamlab.com.kotlinsandbox

import android.app.Fragment
import android.os.Bundle
import android.util.Log

class MyFragment : Fragment() {
    var x: Int by intArg()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v("MyFragment", "$x")
    }
}
