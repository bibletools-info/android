package rawcomposition.bibletools.info.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import rawcomposition.bibletools.info.BibleToolsApp
import javax.inject.Singleton

@Singleton
@Component(modules = [(BibleToolsModule::class),
    (AndroidInjectionModule::class),
    (AndroidSupportInjectionModule::class),
    (ViewModelBuilder::class),
    (ActivityBuilder::class)])
interface BibleToolsAppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: BibleToolsApp): Builder

        fun build(): BibleToolsAppComponent
    }

    fun inject(app: BibleToolsApp)
}