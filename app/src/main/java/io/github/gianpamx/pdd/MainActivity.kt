package io.github.gianpamx.pdd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.FragmentFactory
import io.github.gianpamx.pdd.app.App
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var fragmentFactory : FragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).component.inject(this)
        supportFragmentManager.fragmentFactory = fragmentFactory

        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
    }
}
