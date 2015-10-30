package sandbox.kotlin.teamlab.com.kotlinsandbox

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import butterknife.bindView
import com.jakewharton.rxbinding.view.clicks
import rx.subscriptions.CompositeSubscription

public class MainActivity : AppCompatActivity() {

    private val test: String by inject(String::class)

    private val text: TextView by bindView(R.id.text)

    private var subscriber: CompositeSubscription? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text.text = test
        text.text = test
        text.text = test
        subscriber = CompositeSubscription(
                text.clicks().subscribe { text.text = test })
    }

    override fun onDestroy() {
        subscriber?.unsubscribe()
        super.onDestroy()
    }
}
