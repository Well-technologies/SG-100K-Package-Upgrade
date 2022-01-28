package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.dxa.DXAHomeFragment
import org.singapore.ghru.ui.dxa.completed.CompletedDialogFragment
import org.singapore.ghru.ui.dxa.contra.DXAQuestionsFragment
import org.singapore.ghru.ui.dxa.manualentry.ManualEntryDXAFragment
import org.singapore.ghru.ui.dxa.missingvalues.DXAMissingDialogFragment
import org.singapore.ghru.ui.dxa.noheight.NoHeightDialogFragment
import org.singapore.ghru.ui.dxa.reason.ReasonDialogFragment
import org.singapore.ghru.ui.dxa.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.ecg.questions.ECGSkipFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment

@Suppress("unused")
@Module
abstract class DXABuilderModule {

    @ContributesAndroidInjector
    abstract fun DXAHomeFragment(): DXAHomeFragment

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryFragment(): ManualEntryDXAFragment

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
    abstract fun DXAQuestionaryFragment(): DXAQuestionsFragment

    @ContributesAndroidInjector
    abstract fun DXASkipFragment(): ECGSkipFragment

    @ContributesAndroidInjector
    abstract fun NoHeightFragment(): NoHeightDialogFragment

    @ContributesAndroidInjector
    abstract fun DxaMissingDialogFragment(): DXAMissingDialogFragment


}