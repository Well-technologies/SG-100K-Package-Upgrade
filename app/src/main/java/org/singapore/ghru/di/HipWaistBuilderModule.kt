package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.hipwaist.contraindication.HipWaistQuestionnaireFragment
import org.singapore.ghru.ui.hipwaist.contraindication.HipWaistSkipFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.hipwaist.reason.ReasonDialogFragment
import org.singapore.ghru.ui.hipwaist.manualentry.ManualEntryHipWaistFragment
import org.singapore.ghru.ui.hipwaist.ScanBarcodeFragment
import org.singapore.ghru.ui.hipwaist.hip.HipFragment
import org.singapore.ghru.ui.hipwaist.home.HipWaistHomeFragment
import org.singapore.ghru.ui.hipwaist.waist.WaistFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class HipWaistBuilderModule {

    @ContributesAndroidInjector
    abstract fun HeightWeightHomeFragment(): HipWaistHomeFragment

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryFragment(): ManualEntryHipWaistFragment
//
    @ContributesAndroidInjector
    abstract fun HeightFragment(): HipFragment

    @ContributesAndroidInjector
    abstract fun WaistFragment(): WaistFragment
//
    @ContributesAndroidInjector
    abstract fun HeightWeightQuestionaryFragment(): HipWaistQuestionnaireFragment
//
    @ContributesAndroidInjector
    abstract fun HeightWeightQuestionarySkipFragment(): HipWaistSkipFragment
//
    @ContributesAndroidInjector
    abstract fun ReasonDialogFragment(): ReasonDialogFragment
//
    @ContributesAndroidInjector
    abstract fun StationCheckDialogFragment(): StationCheckDialogFragment
//
    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment
//
    @ContributesAndroidInjector
    abstract fun ErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment
//
//    @ContributesAndroidInjector
//    abstract fun WeightFragment(): WeightFragment
}