package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.octa.reading.reason.ReasonDialogFragment
import org.singapore.ghru.ui.octa.ScanBarcodeFragment
import org.singapore.ghru.ui.octa.contra.OctaQuestionsFragment
import org.singapore.ghru.ui.octa.manualentry.ManualEntryOctaFragment
import org.singapore.ghru.ui.octa.reading.OctaReadingFragment
import org.singapore.ghru.ui.octa.reading.completed.CompletedDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.ultrasound.missingvalue.UltraMissingDialogFragment


@Suppress("unused")
@Module
abstract class OctaBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeFundoscopyReadingFragment(): OctaReadingFragment

    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFundoscopyFragment(): ManualEntryOctaFragment

    @ContributesAndroidInjector
    abstract fun contrubuteStationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeContraFragment(): OctaQuestionsFragment

    @ContributesAndroidInjector
    abstract fun missingDialogFragment(): UltraMissingDialogFragment
}

