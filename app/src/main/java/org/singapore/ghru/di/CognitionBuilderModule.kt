package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.cognition.ScanBarcodeFragment
import org.singapore.ghru.ui.cognition.completed.StartedDialogFragment
import org.singapore.ghru.ui.cognition.confirmation.CognitionConfirmationFragment
import org.singapore.ghru.ui.cognition.contraindication.CognitionQuestionnaireFragment
import org.singapore.ghru.ui.cognition.contraindication.CognitionSkipFragment
import org.singapore.ghru.ui.cognition.guide.CognitionGuideFragment
import org.singapore.ghru.ui.cognition.manualentry.ManualEntryCognitionFragment
import org.singapore.ghru.ui.cognition.reason.ReasonDialogFragment
import org.singapore.ghru.ui.cognition.stageonereason.ReasonDialogFragmentNew
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.statuscheck.StatusCheckDialogFragment


@Suppress("unused")
@Module
abstract class CognitionBuilderModule {

    @ContributesAndroidInjector
    abstract fun StationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun ErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryCognitionFragment(): ManualEntryCognitionFragment

    @ContributesAndroidInjector
    abstract fun SkipDialogFragment(): CognitionSkipFragment

    @ContributesAndroidInjector
    abstract fun CognitionQuetionnaireFragment(): CognitionQuestionnaireFragment

    @ContributesAndroidInjector
    abstract fun CognitionReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun CognitionConfirmationFragment(): CognitionConfirmationFragment

    @ContributesAndroidInjector
    abstract fun StatusCheckDialogFragment(): StatusCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CognitionGuideFragment(): CognitionGuideFragment

    @ContributesAndroidInjector
    abstract fun StartedDialogFragment(): StartedDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CognitionReasonDialogFragmentNew(): ReasonDialogFragmentNew
}