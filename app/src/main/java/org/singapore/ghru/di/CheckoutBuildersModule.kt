package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.checkout.ScanBarcodeFragment
import org.singapore.ghru.ui.checkout.alreadycheckout.AlreadyCheckoutDialogFragment
import org.singapore.ghru.ui.checkout.alreadycheckout.cancel.CancelDialogFragment
import org.singapore.ghru.ui.checkout.alreadycheckout.completed.CompletedDialogFragment
import org.singapore.ghru.ui.checkout.bankdetails.BankDetailsFragment
import org.singapore.ghru.ui.checkout.completion.CheckoutCompletionFragment
import org.singapore.ghru.ui.checkout.completion.paymentcompletion.PaymentCompletionFragment
import org.singapore.ghru.ui.checkout.manualentry.ManualEntryCheckoutFragment
import org.singapore.ghru.ui.checkout.notcomplete.NotCompleteDialogFragment
import org.singapore.ghru.ui.checkout.notstarted.NotStartedDialogFragment
import org.singapore.ghru.ui.checkout.paymentinformation.PaymentInformationFragment
import org.singapore.ghru.ui.checkout.paymentinformation.giroconfirmation.GiroConfirmationFragment
import org.singapore.ghru.ui.checkout.paymentinformation.paymentreview.PaymentReviewFragment
import org.singapore.ghru.ui.checkout.selectedparticipant.SelectedParticipantFragment
import org.singapore.ghru.ui.checkout.voucherdetails.VoucherDetailsFragment
import org.singapore.ghru.ui.checkout.voucherdetails.manualentry.ManualEntryVoucherFragment
import org.singapore.ghru.ui.checkout.voucherdetails.scanbarcode.VoucherScanBarcodeFragment
import org.singapore.ghru.ui.checkout.voucherdetails.scanbarcode1.VoucherScanBarcodeOneFragment
import org.singapore.ghru.ui.checkout.voucherdetails.scanbarcode2.VoucherScanBarcodeTwoFragment
import org.singapore.ghru.ui.checkout.voucherdetails.uniqeerror.UniqueVoucherDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment

@Suppress("unused")
@Module
abstract class CheckoutBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeScanbarcodeFragment(): ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryCheckoutFragment

    @ContributesAndroidInjector
    abstract fun contributeSelectedParticipantFragment(): SelectedParticipantFragment

    @ContributesAndroidInjector
    abstract fun contributeAlreadyFragment(): AlreadyCheckoutDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeNotcompleteFragment(): NotCompleteDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCheckoutCompletionFragment(): CheckoutCompletionFragment

    @ContributesAndroidInjector
    abstract fun contributeBankDetailsFragment(): BankDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeVoucherDetailsFragment(): VoucherDetailsFragment

    @ContributesAndroidInjector
    abstract fun contributeVoucherScanFragment(): VoucherScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeManualVoucherFragment(): ManualEntryVoucherFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentCompleteFragment(): PaymentCompletionFragment

    @ContributesAndroidInjector
    abstract fun contributeCancelDialogFragment(): CancelDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeCompletedDialogFragment(): CompletedDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeVoucherScanOneFragment(): VoucherScanBarcodeOneFragment

    @ContributesAndroidInjector
    abstract fun contributeVoucherScanTwoFragment(): VoucherScanBarcodeTwoFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentInformationFragment(): PaymentInformationFragment

    @ContributesAndroidInjector
    abstract fun contributePaymentReviewFragment(): PaymentReviewFragment

    @ContributesAndroidInjector
    abstract fun contributeGiroConfirmationFragment(): GiroConfirmationFragment

    @ContributesAndroidInjector
    abstract fun contributeUniqeDialogFragment() : UniqueVoucherDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeNotStartedFragment(): NotStartedDialogFragment
}
