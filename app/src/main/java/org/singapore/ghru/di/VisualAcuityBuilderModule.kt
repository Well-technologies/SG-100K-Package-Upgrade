package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkoutcheck.CheckoutCheckDialogFragment
import org.singapore.ghru.ui.visualacuity.ScanBarcodeFragment
import org.singapore.ghru.ui.heightweight.completed.CompletedDialogFragment
import org.singapore.ghru.ui.heightweight.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.stationcheck.StationCheckDialogFragment
import org.singapore.ghru.ui.visualacuity.contraindication.VisualAcuityQuestionnaireFragment
import org.singapore.ghru.ui.visualacuity.contraindication.VisualAcuitySkipFragment
import org.singapore.ghru.ui.visualacuity.home.VisualAcuityHomeFragment
import org.singapore.ghru.ui.visualacuity.lefteye.LeftEyeFragment
import org.singapore.ghru.ui.visualacuity.manualentry.ManualEntryVisualAcuityFragment
import org.singapore.ghru.ui.visualacuity.reason.ReasonDialogFragment
import org.singapore.ghru.ui.visualacuity.righteye.RightEyeFragment
import org.singapore.ghru.ui.visualacuity.visualaid.VisualAcuityAidFragment

@Suppress("unused")
@Module
abstract class VisualAcuityBuilderModule {

    @ContributesAndroidInjector
    abstract fun ScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun StationCheckDialogFragment(): StationCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun CompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun ErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun VisualAcuityQuestionaryFragment(): VisualAcuityQuestionnaireFragment

    @ContributesAndroidInjector
    abstract fun ManualEntryFragment(): ManualEntryVisualAcuityFragment

    @ContributesAndroidInjector
    abstract fun VisualAcuityQuestionarySkipFragment(): VisualAcuitySkipFragment

    @ContributesAndroidInjector
    abstract fun ReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun VisualAcuityAidFragment(): VisualAcuityAidFragment

    @ContributesAndroidInjector
    abstract fun VisualAcuityHomeFragment(): VisualAcuityHomeFragment

    @ContributesAndroidInjector
    abstract fun VisualAcuityLeftEyeFragment(): LeftEyeFragment

    @ContributesAndroidInjector
    abstract fun VisualAcuityRightEyeFragment(): RightEyeFragment

    @ContributesAndroidInjector
    abstract fun CheckoutDialogFragment(): CheckoutCheckDialogFragment
}