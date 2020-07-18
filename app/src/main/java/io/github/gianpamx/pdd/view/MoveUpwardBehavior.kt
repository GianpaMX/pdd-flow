package io.github.gianpamx.pdd.view

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar


object MoveUpwardBehavior : CoordinatorLayout.Behavior<View>() {
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View) =
        dependency is Snackbar.SnackbarLayout

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View) =
        true.also {
            child.translationY = minOf(0f, dependency.translationY - dependency.height)
        }
}
