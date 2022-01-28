package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.skin.ScanBarcodeFragment
import org.singapore.ghru.ui.skin.manualentry.ManualEntrySkinFragment
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.skin.reason.ReasonDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.fundoscopy.reading.completed.CompletedDialogFragment
import org.singapore.ghru.ui.skin.contra.SkinQuestionsFragment
import org.singapore.ghru.ui.skin.partialreadings.PartialReadingsDialogFragment
import org.singapore.ghru.ui.skin.reading.SkinReadingFragment
import org.singapore.ghru.ui.ultrasound.missingvalue.UltraMissingDialogFragment


@Suppress("unused")
@Module
abstract class SkinBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFundoscopyFragment(): ManualEntrySkinFragment

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
    abstract fun contrubuteContraVicorderFragment(): SkinQuestionsFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationQuestionsFragment(): SkinReadingFragment

    @ContributesAndroidInjector
    abstract fun contrubutePartialReadingFragment(): PartialReadingsDialogFragment

    @ContributesAndroidInjector
    abstract fun missingDialogFragment(): UltraMissingDialogFragment


}

