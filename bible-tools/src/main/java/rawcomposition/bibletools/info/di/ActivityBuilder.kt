package rawcomposition.bibletools.info.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import rawcomposition.bibletools.info.ui.home.HomeActivity

@Module
internal abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract fun bindHomeActivity(): HomeActivity
}