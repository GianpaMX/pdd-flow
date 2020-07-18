package io.github.gianpamx.pdd.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FabFlipper(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    FrameLayout(context, attrs, defStyleAttr, defStyleRes), CoordinatorLayout.AttachedBehavior {
    constructor(context: Context) : this(context, null, 0, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0, 0)

    private var current: Int = 0

    var displayedChild: Int
        get() {
            return current
        }
        set(value) {
            if (current == value) return

            val currentButton = getChildAt(current) as FloatingActionButton
            val nextButton = getChildAt(value) as FloatingActionButton

            currentButton.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton?) {
                    nextButton.show()
                }
            })
            current = value
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        children.forEachIndexed { i, view -> if (i != 0) view.visibility = GONE }
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> = MoveUpwardBehavior
}
