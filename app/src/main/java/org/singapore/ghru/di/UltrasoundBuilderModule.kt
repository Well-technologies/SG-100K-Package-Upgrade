package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.ultrasound.completed.CompletedDialogFragment
import org.singapore.ghru.ui.ultrasound.manualentry.ManualEntryUltrasoundFragment
import org.singapore.ghru.ui.ultrasound.reason.ReasonDialogFragment
import org.singapore.ghru.ui.ultrasound.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.ultrasound.UltraSoundReadingFragment
import org.singapore.ghru.ui.ultrasound.missingvalue.UltraMissingDialogFragment

@Suppress("unused")
@Module
abstract class UltrasoundBuilderModule {

    @ContributesAndroidInjector
    abstract fun UltraSoundReadingFragment(): UltraSoundReadingFragment

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryFragment(): ManualEntryUltrasoundFragment

    @ContributesAndroidInjector
    abstract fun StationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun ErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun ReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun UltraMissingDialogFragment(): UltraMissingDialogFragment

}