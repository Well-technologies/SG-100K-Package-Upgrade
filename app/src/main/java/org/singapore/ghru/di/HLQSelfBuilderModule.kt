package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.hlqself.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.hlqself.cancel.CancelDialogFragment
import org.singapore.ghru.ui.hlqself.confirmation.QuestionnaireConfirmationFragment
import org.singapore.ghru.ui.hlqself.languagelist.QuestionnaireListFragment
import org.singapore.ghru.ui.hlqself.scanbarcode.manualentry.ManualEntryBarcodeFragment
import org.singapore.ghru.ui.hlqself.WebFragment
import org.singapore.ghru.ui.questionnaire.notcompleted.NotCompletedDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class HLQSelfBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeWebFragment(): WebFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteQuestionnaireListFragment(): QuestionnaireListFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteConfirmationFragment(): QuestionnaireConfirmationFragment

    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun NotCompletedDialogFragment(): NotCompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment
}