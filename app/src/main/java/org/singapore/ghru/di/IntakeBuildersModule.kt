package org.singapore.ghru.di


import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.intake.cancel.CancelDialogFragment
import org.singapore.ghru.ui.intake.readings.IntakeReadingsFragment
import org.singapore.ghru.ui.intake.readings.completed.CompletedDialogFragment
import org.singapore.ghru.ui.intake.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.intake.scanbarcode.ManualEntryBarcodeFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module

abstract class IntakeBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteIntakeReadingsFragment(): IntakeReadingsFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): CompletedDialogFragment
}