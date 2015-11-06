package sandbox.kotlin.teamlab.com.kotlinsandbox

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import butterknife.bindView
import com.jakewharton.rxbinding.view.clicks
import rx.subscriptions.CompositeSubscription

public class MainActivity : AppCompatActivity() {

    private val string: String by inject(String::class)
    private val charSequence: CharSequence by inject(CharSequence::class)

    private val text: TextView by bindView(R.id.text)

    private var subscriber: CompositeSubscription? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text.text = string
        subscriber = CompositeSubscription(
                text.clicks().subscribe { text.text = charSequence })

        savedInstanceState ?: run { // if (savedInstanceState == null) {
            fragmentManager.beginTransaction()
                    .add(R.id.container, MyFragment().apply { this.x = 1 })
                    .commit()
        }
    }

    override fun onDestroy() {
        subscriber?.unsubscribe()
        super.onDestroy()
    }
}
