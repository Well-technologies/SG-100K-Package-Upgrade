package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.codeheck.CodeCheckDialogFragment
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.samplecollection.bagscanbarcode.BagScanBarcodeFragment
import org.singapore.ghru.ui.samplecollection.bagscanned.BagScannedFragment
import org.singapore.ghru.ui.samplecollection.bagscanned.partialreadings.PartialReadingsDialogFragment
import org.singapore.ghru.ui.samplecollection.bagscanned.reason.ReasonDialogFragment
import org.singapore.ghru.ui.samplecollection.fast.FastFragment
import org.singapore.ghru.ui.samplecollection.fast.reshedule.ResheduleDialogFragment
import org.singapore.ghru.ui.samplecollection.fasted.FastedFragment
import org.singapore.ghru.ui.samplecollection.manualentry.ManualEntrySampleBagBarcodeFragment
import org.singapore.ghru.ui.samplecollection.manualentry.ManualEntrySampleCollectionFragment
import org.singapore.ghru.ui.samplecollection.questions.SampleQuestionsFragment
import org.singapore.ghru.ui.samplecollection.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.samplecollection.verifyid.VerifyIDFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class SampleCollectionBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeVerifyIDFragment(): VerifyIDFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeFastFragment(): FastFragment

    @ContributesAndroidInjector
    abstract fun contributeResheduleDialogFragment(): ResheduleDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeFastedFragment(): FastedFragment

    @ContributesAndroidInjector
    abstract fun contributeBagScanBarcodeFragment(): BagScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeBagScannedFragment(): BagScannedFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntrySampleCollectionFragment(): ManualEntrySampleCollectionFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntrySampleBagBarcodeFragment(): ManualEntrySampleBagBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.singapore.ghru.ui.samplecollection.bagscanned.completed.CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun PartialReadingsDialogFragment(): PartialReadingsDialogFragment

    @ContributesAndroidInjector
    abstract fun SampleSkipFragment(): ECGSkipFragment

    @ContributesAndroidInjector
    abstract fun contributeSampleQuestionFragment(): SampleQuestionsFragment

    @ContributesAndroidInjector
    abstract fun contributeQuestionReasonDialogFragment(): org.singapore.ghru.ui.samplecollection.questions.reason.ReasonDialogFragment
}

