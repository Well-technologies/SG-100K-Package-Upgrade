package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.cognition.completed.StartedDialogFragment
import org.singapore.ghru.ui.foodquestionnaire.ScanBarcodeFragment
import org.singapore.ghru.ui.foodquestionnaire.confirmartion.FFQConfirmationFragment
import org.singapore.ghru.ui.foodquestionnaire.contraindication.FoodFrequencyQuestionnaireFragment
import org.singapore.ghru.ui.foodquestionnaire.contraindication.FoodFrequencySkipFragment
import org.singapore.ghru.ui.foodquestionnaire.guide.FoodFrequencyGuideFragment
import org.singapore.ghru.ui.foodquestionnaire.language.FoodFrequencyLanguageFragment
import org.singapore.ghru.ui.foodquestionnaire.manualentry.ManualEntryFoodFrequencyFragment
import org.singapore.ghru.ui.foodquestionnaire.reason.ReasonDialogFragment
import org.singapore.ghru.ui.foodquestionnaire.stageonereason.ReasonDialogFragmentNew
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.statuscheck.StatusCheckDialogFragment
import org.singapore.ghru.ui.statuscheckffq.FFQStatusCheckDialogFragment
import org.singapore.ghru.ui.statuscheckffqnew.FFQStatusCheckNewDialogFragment

@Suppress("unused")
@Module
abstract class FoodQuestionnaireBuilderModule {

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryBarcodeFragment() : ManualEntryFoodFrequencyFragment

    @ContributesAndroidInjector
    abstract fun StationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun ErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun FoodFrequencyContraFragment(): FoodFrequencyQuestionnaireFragment

    @ContributesAndroidInjector
    abstract fun FoodFrequencyContraSkipFragment(): FoodFrequencySkipFragment

    @ContributesAndroidInjector
    abstract fun ReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun FFQGuideFragment(): FoodFrequencyGuideFragment

    @ContributesAndroidInjector
    abstract fun FFQConfirmationFragment(): FFQConfirmationFragment

    @ContributesAndroidInjector
    abstract fun StatusCheckDialogFragment(): StatusCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun StartedDialogFragment(): StartedDialogFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun ReasonDialogFragmentNew(): ReasonDialogFragmentNew

    @ContributesAndroidInjector
    abstract fun FFQStatusDialogFragment(): FFQStatusCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun FFQLanguageFragment(): FoodFrequencyLanguageFragment

    @ContributesAndroidInjector
    abstract fun FFQStatusCheckNewDialogFragment(): FFQStatusCheckNewDialogFragment

}