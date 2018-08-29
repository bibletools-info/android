package rawcomposition.bibletools.info.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import rawcomposition.bibletools.info.ui.home.HomeActivity
import rawcomposition.bibletools.info.ui.home.picker.VersePickerActivity
import rawcomposition.bibletools.info.ui.home.strongs.StrongsDialogFragment
import rawcomposition.bibletools.info.ui.settings.SettingsActivity

@Module
internal abstract class ActivityBuilder {
    @ContributesAndroidInjector
    abstract fun bindHomeActivity(): HomeActivity

    @ContributesAndroidInjector
    abstract fun bindVersePickerActivity(): VersePickerActivity

    @ContributesAndroidInjector
    abstract fun bindSettingsActivity(): SettingsActivity

    @ContributesAndroidInjector
    abstract fun bindStrongsFragment(): StrongsDialogFragment
}