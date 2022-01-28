package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.treadmill.manualentry.ManualEntryTreadmillFragment
import org.singapore.ghru.ui.treadmill.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.treadmill.TreadmillMainFragment
import org.singapore.ghru.ui.treadmill.aftertest.TreadmillAfterTestFragment
import org.singapore.ghru.ui.treadmill.beforetest.TreadmillBeforeTestFragment
import org.singapore.ghru.ui.treadmill.bp.TreadmillBPFragment
import org.singapore.ghru.ui.treadmill.bp.validationError.MeasurementErrorDialogFragment
import org.singapore.ghru.ui.treadmill.completed.CompletedDialogFragment
import org.singapore.ghru.ui.treadmill.ecgcheck.EcgCheckDialogFragment
import org.singapore.ghru.ui.treadmill.reason.ReasonDialogFragment


@Suppress("unused")
@Module
abstract class TreadmillBuilderModule {

    @ContributesAndroidInjector
    abstract fun TreadmillMainFragment(): TreadmillMainFragment

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryFragment(): ManualEntryTreadmillFragment

    @ContributesAndroidInjector
    abstract fun StationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun ErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun TreadmillBPFragment(): TreadmillBPFragment

    @ContributesAndroidInjector
    abstract fun ReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun TreadmillAfterTestFragment(): TreadmillAfterTestFragment

    @ContributesAndroidInjector
    abstract fun TreadmillBeforeTestFragment(): TreadmillBeforeTestFragment

    @ContributesAndroidInjector
    abstract fun TreadmillEcgTestFragment(): EcgCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun ErrorMeasurementDialogFragment(): MeasurementErrorDialogFragment

}