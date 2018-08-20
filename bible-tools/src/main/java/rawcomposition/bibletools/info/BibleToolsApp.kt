package rawcomposition.bibletools.info

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.core.CrashlyticsCore
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import io.fabric.sdk.android.Fabric
import rawcomposition.bibletools.info.di.DaggerBibleToolsAppComponent
import timber.log.Timber
import javax.inject.Inject

class BibleToolsApp : Application(), HasActivityInjector, HasSupportFragmentInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = fragmentInjector


    override fun onCreate() {
        super.onCreate()

        AppInjector.init(this)
    }

    object AppInjector {

        fun init(app: BibleToolsApp) {

            val core = CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
            Fabric.with(app, Crashlytics.Builder().core(core).build(), Answers())

            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }

            DaggerBibleToolsAppComponent.builder()
                    .application(app)
                    .build()
                    .inject(app)
        }
    }
}
