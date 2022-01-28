package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.ui.codeheck.CodeCheckDialogFragment
import org.singapore.ghru.ui.samplemanagement.fastingbloodglucose.FastingBloodGlucoseFragment
import org.singapore.ghru.ui.samplemanagement.hb1ac.Hb1AcFragment
import org.singapore.ghru.ui.samplemanagement.hdl.HDLFragment
import org.singapore.ghru.ui.samplemanagement.hemoglobin.HemoglobinFragment
import org.singapore.ghru.ui.samplemanagement.hogtt.HOGTTFragment
import org.singapore.ghru.ui.samplemanagement.home.SampleMangementHomeFragment
import org.singapore.ghru.ui.samplemanagement.lipidprofile.LipidProfileFragment
import org.singapore.ghru.ui.samplemanagement.pendingsamplelist.PendingSampleListFragment
import org.singapore.ghru.ui.samplemanagement.storage.StorageFragment
import org.singapore.ghru.ui.samplemanagement.storage.manualentry.ManualEntryBarcodeFragment
import org.singapore.ghru.ui.samplemanagement.storage.manualentry.ManualEntryFragment
import org.singapore.ghru.ui.samplemanagement.storage.reason.ReasonDialogFragment
import org.singapore.ghru.ui.samplemanagement.storage.scanqrcode.ScanBarcodeFragment
import org.singapore.ghru.ui.samplemanagement.totalcholesterol.TotalCholesterolFragment
import org.singapore.ghru.ui.samplemanagement.triglycerides.TriglyceridesFragment
import org.singapore.ghru.ui.samplemanagement.tubescanbarcode.TubeScanBarcodeFragment
import org.singapore.ghru.ui.samplemanagement.tubescanbarcode.errordialog.ErrorDialogFragment
import org.singapore.ghru.ui.samplemanagement.tubescanbarcode.manualentry.TubeScanManualBarcodeFragment

@Suppress("unused")
@Module
abstract class SampleProcessingBuildersModule {


    @ContributesAndroidInjector
    abstract fun contributePendingSampleListFragment(): PendingSampleListFragment

    @ContributesAndroidInjector
    abstract fun contributeTubeScanBarcodeFragment(): TubeScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeSampleMangementHomeFragment(): SampleMangementHomeFragment

    @ContributesAndroidInjector
    abstract fun contributeLipidProfileFragment(): LipidProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeFastingBloodGlucoseFragment(): FastingBloodGlucoseFragment

    @ContributesAndroidInjector
    abstract fun contributeHb1AcFragment(): Hb1AcFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragment(): ErrorDialogFragment


    @ContributesAndroidInjector
    abstract fun contributeErrorDialogFragmentXX(): org.singapore.ghru.ui.registerpatient.scanqrcode.errordialog.ErrorDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeTubeScanManualBarcodeFragment(): TubeScanManualBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeScanFragment(): org.singapore.ghru.ui.samplemanagement.storage.scanbarcode.ScanBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeStorageFragment(): StorageFragment

    @ContributesAndroidInjector
    abstract fun contributeScanQRCodeFragment(): ScanBarcodeFragment


    @ContributesAndroidInjector
    abstract fun contributeReasonDialogFragment(): ReasonDialogFragment


    @ContributesAndroidInjector
    abstract fun contributeManualEntryFragment(): ManualEntryFragment

    @ContributesAndroidInjector
    abstract fun contributeManualEntryBarcodeFragment(): ManualEntryBarcodeFragment

    @ContributesAndroidInjector
    abstract fun contributeHOGTTFragment(): HOGTTFragment

    @ContributesAndroidInjector
    abstract fun contributeCodeCheckDialogFragment(): CodeCheckDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeTotalCholesterolFragment(): TotalCholesterolFragment

    @ContributesAndroidInjector
    abstract fun contributeHDLFragment(): HDLFragment

    @ContributesAndroidInjector
    abstract fun contributeHemoglobinFragment(): HemoglobinFragment

    @ContributesAndroidInjector
    abstract fun contributeTriglyceridesFragment(): TriglyceridesFragment

    @ContributesAndroidInjector
    abstract fun contrubuteCompletedDialogFragment(): org.singapore.ghru.ui.samplemanagement.storage.completed.CompletedDialogFragment


}

