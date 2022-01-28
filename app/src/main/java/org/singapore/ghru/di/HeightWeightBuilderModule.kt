package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.heightweight.HeightWeightHomeFragment
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.contraindication.HeightWeightQuestionnaireFragment
import org.singapore.ghru.ui.heightweight.contraindication.HeightWeightSkipFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.heightweight.height.HeightFragment
import org.singapore.ghru.ui.heightweight.height.reason.ReasonDialogFragment
import org.singapore.ghru.ui.heightweight.manualentry.ManualEntryHeightWeightFragment
import org.singapore.ghru.ui.heightweight.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.heightweight.weight.WeightFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class HeightWeightBuilderModule {

    @ContributesAndroidInjector
    abstract fun HeightWeightHomeFragment(): HeightWeightHomeFragment

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryFragment(): ManualEntryHeightWeightFragment

    @ContributesAndroidInjector
    abstract fun HeightFragment(): HeightFragment

    @ContributesAndroidInjector
    abstract fun HeightWeightQuestionaryFragment(): HeightWeightQuestionnaireFragment

    @ContributesAndroidInjector
    abstract fun HeightWeightQuestionarySkipFragment(): HeightWeightSkipFragment

    @ContributesAndroidInjector
    abstract fun ReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun StationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun ErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun WeightFragment(): WeightFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment
}