package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.vicorder.ScanBarcodeFragment
import org.singapore.ghru.ui.vicorder.manualentry.ManualEntryVicorderFragment
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.dxa.noheight.NoHeightDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.vicorder.reason.ReasonDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
import org.singapore.ghru.ui.vicorder.contra.VicorderQuestionsFragment
import org.singapore.ghru.ui.vicorder.stationquestion.StationQuestionFragment


@Suppress("unused")
@Module
abstract class VicorderBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFundoscopyFragment(): ManualEntryVicorderFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteContraVicorderFragment(): VicorderQuestionsFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationQuestionsFragment(): StationQuestionFragment

    @ContributesAndroidInjector
    abstract fun NoHeightFragment(): NoHeightDialogFragment

}

