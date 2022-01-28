package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.camera.CameraFragment
import org.singapore.ghru.ui.codeheck.CodeCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.basicdetails.BasicDetailsFragment
import org.singapore.ghru.ui.registerpatient.checklist.CheckListFragment
import org.singapore.ghru.ui.registerpatient.confirmation.ConfirmationFragment
import org.singapore.ghru.ui.registerpatient.confirmation.completed.CompletedDialogFragment
import org.singapore.ghru.ui.registerpatient.explanation.ExplanationFragment
import org.singapore.ghru.ui.registerpatient.explanation.reasondialog.ExplanationDialogFragment
import org.singapore.ghru.ui.registerpatient.identification.IdentificationFragment
import org.singapore.ghru.ui.registerpatient.identification.patientphoto.PatientPhotoFragment
import org.singapore.ghru.ui.registerpatient.review.ReviewFragment
import org.singapore.ghru.ui.registerpatient.scanbarcode.ScanBarcodeFragment
import org.singapore.ghru.ui.registerpatient.scanbarcode.manualentry.ManualEntryBarcodeFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.ScanQRCodeFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.manualentry.ManualEntryQRCodeFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.membersdialog.MembersDialogFragment
import org.singapore.ghru.ui.registerpatient_new.basicdetails.BasicDetailsFragmentNew
import org.singapore.ghru.ui.registerpatient_new.confirmation.ConfirmationFragmentNew
import org.singapore.ghru.ui.registerpatient_new.confirmation.completed.CompletedDialogFragmentNew
import org.singapore.ghru.ui.registerpatient_new.review.ReviewFragmentNew
import org.singapore.ghru.ui.registerpatient_new.scanbarcode.ScanBarcodeFragmentNew
import org.singapore.ghru.ui.registerpatient_new.scanbarcode.manualentry.ManualEntryBarcodeFragmentNew
import org.singapore.ghru.ui.registerpatient_sg.basicdetails.BasicDetailsFragmentSG
import org.singapore.ghru.ui.registerpatient_sg.checklist.CheckListFragmentSG
import org.singapore.ghru.ui.registerpatient_sg.confirmation.ConfirmationFragmentSG
import org.singapore.ghru.ui.registerpatient_sg.confirmation.completed.CompletedDialogFragmentSG
import org.singapore.ghru.ui.registerpatient_sg.explanation.ExplanationFragmentSG
import org.singapore.ghru.ui.registerpatient_sg.review.ReviewFragmentSG
import org.singapore.ghru.ui.registerpatient_sg.scanbarcode.ScanBarcodeFragmentSG
import org.singapore.ghru.ui.registerpatient_sg.scanbarcode.manualentry.ManualEntryBarcodeFragmentSG

@Suppress("unused")
@Module
abstract class RegisterPatientBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeScanQRCodeFragment(): ScanQRCodeFragment

    @ContributesAndroidInjector
    abstract fun contributeMembersDialogFragment(): MembersDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeExplanationFragment(): ExplanationFragment

    @ContributesAndroidInjector
    abstract fun contributeExplanationDialogFragment(): ExplanationDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeIdentificationFragment(): IdentificationFragment

    @ContributesAndroidInjector
    abstract fun contributeReviewFragment(): ReviewFragment

    @ContributesAndroidInjector
    abstract fun contributeBasicDetailsFragment(): BasicDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeConfirmationFragment(): ConfirmationFragment

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCameraFragment(): CameraFragment


    @ContributesAndroidInjector
    abstract fun contributePatientPhotoFragment(): PatientPhotoFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCheckListFragment(): CheckListFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryQRCodeFragment(): ManualEntryQRCodeFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCheckListFragmentSg(): CheckListFragmentSG

    @ContributesAndroidInjector
    abstract fun contributeBasicDetailsFragmentSG(): BasicDetailsFragmentSG

    @ContributesAndroidInjector
    abstract fun contributeReviewFragmentSG(): ReviewFragmentSG

    @ContributesAndroidInjector
    abstract fun contributeExplanationFragmentSG(): ExplanationFragmentSG

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragmentSG(): ScanBarcodeFragmentSG

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragmentSG(): ManualEntryBarcodeFragmentSG

    @ContributesAndroidInjector
    abstract fun contributeConfirmationFragmentSG(): ConfirmationFragmentSG

    @ContributesAndroidInjector
    abstract fun contributeCompleteDialogFragmentSG(): CompletedDialogFragmentSG


    @ContributesAndroidInjector
    abstract fun contributeBasicDetailsFragmentNew(): BasicDetailsFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeCompleteDialogFragmentNew(): CompletedDialogFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeConfirmationFragmentNew(): ConfirmationFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeReviewFragmentNew(): ReviewFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragmentNew(): ManualEntryBarcodeFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeScanBarcodeFragmentNew(): ScanBarcodeFragmentNew

}
