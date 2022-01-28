package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.dashboard.AllStationsFragment
import org.singapore.ghru.ui.dashboard.allparticipants.AllParticipantsFragment
import org.singapore.ghru.ui.dashboard.selectedstation.SelectedStationFragment

@Suppress("unused")
@Module
abstract class DashboardAllParticipantsBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): AllParticipantsFragment

}
