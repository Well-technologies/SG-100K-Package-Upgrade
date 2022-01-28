package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.dxa.noheight.NoHeightDialogFragment
import org.singapore.ghru.ui.ecg.guide.ElectrodeFragment
import org.singapore.ghru.ui.ecg.guide.PreperationFragment
import org.singapore.ghru.ui.ecg.trace.TraceFragment
import org.singapore.ghru.ui.ecg.trace.complete.CompleteDialogFragment
import org.singapore.ghru.ui.ecg.trace.reason.ReasonDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.spirometry.cancel.CancelDialogFragment
import org.singapore.ghru.ui.spirometry.checklist.CheckListFragment
import org.singapore.ghru.ui.spirometry.guide.GuideMainFragment
import org.singapore.ghru.ui.spirometry.manualentry.ManualEntrySpirometryFragment
import org.singapore.ghru.ui.spirometry.record.RecordTestFragment
import org.singapore.ghru.ui.spirometry.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.spirometry.tests.TestFragment
import org.singapore.ghru.ui.spirometry.tests.checkmeasurement.CheckMeasurementDialogFragment
import org.singapore.ghru.ui.spirometry.verifyid.SpirometryVerifyIDFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class SpirometryBuilderModule {

    @ContributesAndroidInjector
    abstract fun SpirometryVerifyIDFragment(): SpirometryVerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeSpirometryGuideMainFragment(): GuideMainFragment

    @ContributesAndroidInjector
    abstract fun contributeTestFragment(): TestFragment

    @ContributesAndroidInjector
    abstract fun contributeCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeRecordTestFragment(): RecordTestFragment

    @ContributesAndroidInjector
    abstract fun contributeElectrodeFragment(): ElectrodeFragment

    @ContributesAndroidInjector
    abstract fun contributePreperationFragment(): PreperationFragment

    @ContributesAndroidInjector
    abstract fun contributeTraceFragment(): TraceFragment

    @ContributesAndroidInjector
    abstract fun contributeCompleteDialogFragment(): CompleteDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragmentt(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryECGFragment(): ManualEntrySpirometryFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.singapore.ghru.ui.spirometry.tests.completed.CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCheckListFragment(): CheckListFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCancelDialogFragment(): org.singapore.ghru.ui.spirometry.cancelchecklist.CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckMeasurementDialogFragment(): CheckMeasurementDialogFragment

    @ContributesAndroidInjector
    abstract fun NoHeightFragment(): NoHeightDialogFragment

}