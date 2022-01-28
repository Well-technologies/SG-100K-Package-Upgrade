package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.bodymeasurements.bp.BPFragment
import org.singapore.ghru.ui.bodymeasurements.bp.checkmeasurement.CheckMeasurementDialogFragment
import org.singapore.ghru.ui.bodymeasurements.bp.info.InfoFragment
import org.singapore.ghru.ui.bodymeasurements.bp.main.BPMainFragment
import org.singapore.ghru.ui.bodymeasurements.bp.manual.one.BPManualOneFragment
import org.singapore.ghru.ui.bodymeasurements.bp.manual.three.BPManualThreeFragment
import org.singapore.ghru.ui.bodymeasurements.bp.manual.two.BPManualTwoFragment
import org.singapore.ghru.ui.bodymeasurements.bp.skip.SkipDialogFragment
import org.singapore.ghru.ui.bodymeasurements.manualentrybp.ManualEntryBodyMeasurement
import org.singapore.ghru.ui.bodymeasurements.measurements.MeasurementsFragment
import org.singapore.ghru.ui.bodymeasurements.measurements.second.MeasurementsSecondFragment
import org.singapore.ghru.ui.bodymeasurements.review.ReviewFragment
import org.singapore.ghru.ui.bodymeasurements.review.completed.CompletedDialogFragment
import org.singapore.ghru.ui.bodymeasurements.scanbarcodebp.ScanBarcodeFragment
import org.singapore.ghru.ui.bodymeasurements.verifyid.VerifyIDFragment
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class BloodPressureBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeBPFragment(): BPFragment

    @ContributesAndroidInjector
    abstract fun contributeMeasurementsFragment(): MeasurementsFragment

    @ContributesAndroidInjector
    abstract fun contributeReviewFragment(): ReviewFragment

    @ContributesAndroidInjector
    abstract fun contributeVerifyIDFragment(): VerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeMeasurementsSecondFragment(): MeasurementsSecondFragment

    @ContributesAndroidInjector
    abstract fun contributeBPMainFragment(): BPMainFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualOneFragment(): BPManualOneFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualTwoFragment(): BPManualTwoFragment

    @ContributesAndroidInjector
    abstract fun contributeBPManualThreeFragment(): BPManualThreeFragment

    @ContributesAndroidInjector
    abstract fun contributeSkipDialogFragment(): SkipDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeInfoFragment(): InfoFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteManualEntryBodyMeasurement(): ManualEntryBodyMeasurement

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteReasonDialogFragment(): org.singapore.ghru.ui.bodymeasurements.bp.reason.ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.singapore.ghru.ui.bodymeasurements.bp.completed.CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckMeasurementDialogFragment(): CheckMeasurementDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

}