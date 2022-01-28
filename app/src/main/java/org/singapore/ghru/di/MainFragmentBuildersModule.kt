package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.datamanagement.DataManagementListFragment
import org.singapore.ghru.ui.devices.DevicesFragment
import org.singapore.ghru.ui.enumeration.EnumerationFragment
import org.singapore.ghru.ui.home.HomeFragment
import org.singapore.ghru.ui.homeenumeration.HomeEnumerationFragment
import org.singapore.ghru.ui.homeenumerationlist.HomeEmumerationListFragment
import org.singapore.ghru.ui.logout.LogoutDialogFragment
import org.singapore.ghru.ui.samplemanagement.SampleMangementFragment
import org.singapore.ghru.ui.station.StationFragment
import org.singapore.ghru.ui.usersetting.UserSettingFragment

@Suppress("unused")
@Module
abstract class MainFragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeFragment(): HomeFragment

    @ContributesAndroidInjector
    abstract fun contributeEnumerationFragment(): EnumerationFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeEmumerationListFragment(): HomeEmumerationListFragment

    @ContributesAndroidInjector
    abstract fun contributeHomeEnumerationFragment(): HomeEnumerationFragment

    @ContributesAndroidInjector
    abstract fun contributeStationFragment(): StationFragment

    @ContributesAndroidInjector
    abstract fun contributeDevicesFragment(): DevicesFragment


    @ContributesAndroidInjector
    abstract fun contributeSampleMangementFragment(): SampleMangementFragment

    @ContributesAndroidInjector
    abstract fun contributeUserSettingFragment(): UserSettingFragment

    @ContributesAndroidInjector
    abstract fun contributeLogoutDialogFragment(): LogoutDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeDataManagementListFragment() : DataManagementListFragment

}
