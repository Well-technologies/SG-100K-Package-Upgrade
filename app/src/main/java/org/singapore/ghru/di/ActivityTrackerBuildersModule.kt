package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.activitytracker.activitytracker.ActivityTackeFragment
import org.singapore.ghru.ui.activitytracker.activitytracker.reason.ReasonDialogFragment
import org.singapore.ghru.ui.activitytracker.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.activitytracker.scanbarcode.manualentry.ManualEntryBarcodeFragment
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class ActivityTrackerBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contrivuteManualEntryBarcode(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contrivuteActivityTackeFragment(): ActivityTackeFragment

    @ContributesAndroidInjector
    abstract fun contrivuteReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.singapore.ghru.ui.activitytracker.activitytracker.completed.CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment
}
