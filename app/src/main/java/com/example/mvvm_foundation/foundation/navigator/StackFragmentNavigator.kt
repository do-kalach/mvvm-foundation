package com.example.mvvm_foundation.foundation.navigator

import android.os.Bundle
import android.view.View
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.mvvm_foundation.foundation.utils.Event
import com.example.mvvm_foundation.foundation.views.BaseFragment
import com.example.mvvm_foundation.foundation.views.BaseScreen
import com.example.mvvm_foundation.foundation.views.BaseScreen.Companion.ARG_SCREEN
import com.example.mvvm_foundation.foundation.views.HasScreenTitle

class StackFragmentNavigator(
    private val activity: AppCompatActivity,
    @IdRes private val containerId: Int,
    private val defaultTitle: String,
    private val animations: Animations,
    private val initialScreenCreator: () -> BaseScreen
) : Navigator {

    private var result: Event<Any>? = null

    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            launchFragment(
                screen = initialScreenCreator(),
                addToBackStack = false
            )
        }
    }

    fun onDestroy() {
        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
    }

    fun notifyScreenUpdates() {
        val f = activity.supportFragmentManager.findFragmentById(containerId)

        if (activity.supportFragmentManager.backStackEntryCount > 0)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        else
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        if (f is HasScreenTitle && f.getScreenTitle() != null) {
            activity.supportActionBar?.title = f.getScreenTitle()
        } else
            activity.supportActionBar?.title = defaultTitle
    }

    private fun publishResults(fragment: Fragment) {
        val result = result?.getValue() ?: return
        if (fragment is BaseFragment)
            fragment.viewModel.onResult(result)
    }

    private fun launchFragment(screen: BaseScreen, addToBackStack: Boolean = true) {
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment
        fragment.arguments = bundleOf(ARG_SCREEN to screen)

        val transaction = activity.supportFragmentManager.beginTransaction()
        if (addToBackStack) transaction.addToBackStack(null)
        transaction
            .setCustomAnimations(
                animations.enterAnim,
                animations.exitAnim,
                animations.popEnterAnim,
                animations.popExitAnim
            )
            .replace(containerId, fragment)
            .commit()
    }

    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            notifyScreenUpdates()
            publishResults(f)
        }
    }

    class Animations(
        @AnimRes val enterAnim: Int,
        @AnimRes val exitAnim: Int,
        @AnimRes val popEnterAnim: Int,
        @AnimRes val popExitAnim: Int,
    )

    override fun launch(screen: BaseScreen) {
        launchFragment(screen)
    }

    override fun goBack(result: Any?) {
        if (result != null) {
            this.result = Event(result)
        }
        activity.onBackPressed()
    }
}