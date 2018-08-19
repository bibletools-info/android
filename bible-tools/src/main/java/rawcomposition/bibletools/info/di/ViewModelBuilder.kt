package rawcomposition.bibletools.info.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import rawcomposition.bibletools.info.ui.home.HomeViewModel
import rawcomposition.bibletools.info.ui.home.strongs.StrongsViewModel

@Module
internal abstract class ViewModelBuilder {
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun bindHomeViewModel(homeViewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(StrongsViewModel::class)
    internal abstract fun bindStrongsViewModel(strongsViewModel: StrongsViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFractory(factory: ViewModelFactory): ViewModelProvider.Factory
}