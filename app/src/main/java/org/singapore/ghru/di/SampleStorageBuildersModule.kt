package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.codeheck.CodeCheckDialogFragment
import org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.samplemanagement.storage.StorageFragment
import org.singapore.ghru.ui.samplemanagement.storage.manualentry.ManualEntryBarcodeFragment
import org.singapore.ghru.ui.samplemanagement.storage.manualentry.ManualEntryFragment
import org.singapore.ghru.ui.samplemanagement.storage.reason.ReasonDialogFragment
import org.singapore.ghru.ui.samplemanagement.storage.samplelist.PendingSampleListFragment
import org.singapore.ghru.ui.samplemanagement.storage.scanqrcode.ScanBarcodeFragment
import org.singapore.ghru.ui.samplemanagement.storage.transfer.TransferFragment

@Suppress("unused")
@Module
abstract class SampleStorageBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributeScanFragment(): org.singapore.ghru.ui.samplemanagement.storage.scanbarcode.ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeStorageFragment(): StorageFragment

    @ContributesAndroidInjector
    abstract fun contributeScanQRCodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryFragment


    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributePendingSampleListFragment(): PendingSampleListFragment

    @ContributesAndroidInjector
    abstract fun contributeTransferFragment(): TransferFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment


    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragmentXX(): org.singapore.ghru.ui.samplemanagement.storage.reasonc.ReasonDialogFragment


    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.singapore.ghru.ui.samplemanagement.storage.completed.CompletedDialogFragment

}

