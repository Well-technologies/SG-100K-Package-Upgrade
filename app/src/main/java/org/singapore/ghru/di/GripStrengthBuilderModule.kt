package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.gripstrangth.GripStrengthHomeFragment
import org.singapore.ghru.ui.gripstrangth.contraindication.GripStrengthQuestionnaireFragment
import org.singapore.ghru.ui.gripstrangth.contraindication.GripStrengthSkipFragment
import org.singapore.ghru.ui.gripstrangth.grip.GripFragment
import org.singapore.ghru.ui.gripstrangth.grip.missingvlaue.ReadingOneMissingDialogFragment
import org.singapore.ghru.ui.gripstrangth.manualentry.ManualEntryGripStrengthFragment
import org.singapore.ghru.ui.gripstrangth.reading3.Reading3Fragment
import org.singapore.ghru.ui.gripstrangth.reading3.missingvalue.ReadingThreeMissingDialogFragment
import org.singapore.ghru.ui.gripstrangth.strength.StrengthFragment
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.gripstrangth.reason.ReasonDialogFragment
import org.singapore.ghru.ui.gripstrangth.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.gripstrangth.strength.missingvalue.ReadingTwoMissingDialogFragment
import org.singapore.ghru.ui.gripstrangth.valuecheckdialog.ValueCheckDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment

@Suppress("unused")
@Module
abstract class GripStrengthBuilderModule {

    @ContributesAndroidInjector
    abstract fun GripStrengthHomeFragment(): GripStrengthHomeFragment

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryFragment(): ManualEntryGripStrengthFragment

    @ContributesAndroidInjector
    abstract fun GripFragment(): GripFragment

    @ContributesAndroidInjector
    abstract fun StrengthFragment(): StrengthFragment

    @ContributesAndroidInjector
    abstract fun GripStrengthQuestionaryFragment(): GripStrengthQuestionnaireFragment

    @ContributesAndroidInjector
    abstract fun GripStrengthQuestionarySkipFragment(): GripStrengthSkipFragment

    @ContributesAndroidInjector
    abstract fun ReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun StationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun ErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun Reading3Fragment(): Reading3Fragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun ValueCheckDialogFragment(): ValueCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun ReadingOneDialogFragment(): ReadingOneMissingDialogFragment

    @ContributesAndroidInjector
    abstract fun ReadingTwoMissingDialogFragment(): ReadingTwoMissingDialogFragment

    @ContributesAndroidInjector
    abstract fun ReadingThreeMissingDialogFragment(): ReadingThreeMissingDialogFragment


}