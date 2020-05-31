package io.github.gianpamx.pdd.clock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.github.gianpamx.pdd.R
import kotlinx.android.synthetic.main.clock_fragment.*
import javax.inject.Inject

class ClockFragment @Inject constructor(
    viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.clock_fragment) {
    private val viewModel: ClockViewModel by viewModels { viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.viewState.observe(viewLifecycleOwner, Observer {
            clockTextView.text = it
        })
    }
}
