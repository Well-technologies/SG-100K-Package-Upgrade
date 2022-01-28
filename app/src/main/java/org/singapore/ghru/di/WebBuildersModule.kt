package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.questionnaire.completed.CompletedDialogFragment
import org.singapore.ghru.ui.questionnaire.cancel.CancelDialogFragment
import org.singapore.ghru.ui.questionnaire.confirmation.QuestionnaireConfirmationFragment
import org.singapore.ghru.ui.questionnaire.languagelist.QuestionnaireListFragment
import org.singapore.ghru.ui.questionnaire.notcompleted.NotCompletedDialogFragment
import org.singapore.ghru.ui.questionnaire.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.questionnaire.scanbarcode.manualentry.ManualEntryBarcodeFragment
import org.singapore.ghru.ui.questionnaire.web.WebFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment


@Suppress("unused")
@Module
abstract class WebBuildersModule {


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
