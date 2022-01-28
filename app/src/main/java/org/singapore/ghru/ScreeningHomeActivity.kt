package org.singapore.ghru

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import org.singapore.ghru.util.LocaleManager
import javax.inject.Inject

class ScreeningHomeActivity : BaseActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.screening_home_activity)
    }

    override fun supportFragmentInjector() = dispatchingAndroidInjector
    override fun onSupportNavigateUp(): Boolean {
        val currentDestination = Navigation.findNavController(this, R.id.container).currentDestination
        val parent = currentDestination?.parent
        if (parent == null || currentDestination.id != parent.id)
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
            //super.onBackPressed()
        else
        {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
            //onSupportNavigateUp()

        return true
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleManager(base).setLocale())
    }


}