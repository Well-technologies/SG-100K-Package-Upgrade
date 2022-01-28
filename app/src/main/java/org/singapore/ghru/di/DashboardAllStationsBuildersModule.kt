package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.dashboard.AllStationsFragment
import org.singapore.ghru.ui.dashboard.selectedstation.SelectedStationFragment

@Suppress("unused")
@Module
abstract class DashboardAllStationsBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): AllStationsFragment

    @ContributesAndroidInjector
    abstract fun contributeSelectedStationFragment(): SelectedStationFragment

}
