package rawcomposition.bibletools.info.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import rawcomposition.bibletools.info.ui.home.HomeActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}