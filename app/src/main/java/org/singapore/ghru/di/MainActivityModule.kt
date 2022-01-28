package org.singapore.ghru.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import org.singapore.ghru.*
import org.singapore.ghru.HeightWeightActivity
import org.singapore.ghru.vo.HLQSelfActivity

@Suppress("unused")
@Module
abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuildersModule::class])
    abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector(modules = [MainFragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [SettingFragmentBuildersModule::class])
    abstract fun contributeSettingActivity(): SettingActivity

    @ContributesAndroidInjector(modules = [EnumerationFragmentBuildersModule::class])
    abstract fun contributeEnumerationActivity(): EnumerationActivity

    @ContributesAndroidInjector(modules = [BodyMeasurementsBuildersModule::class])
    abstract fun contributeBodyMeasurements(): BodyMeasurementsActivity

    @ContributesAndroidInjector(modules = [RegisterPatientBuildersModule::class])
    abstract fun contributeRegisterPatientActivity(): RegisterPatientActivity

    @ContributesAndroidInjector(modules = [ECGBuildersModule::class])
    abstract fun contributeECGActivity(): ECGActivity

    @ContributesAndroidInjector(modules = [FundoscopyBuildersModule::class])
    abstract fun contributeFundoscopyActivity(): FundoscopyActivity

    @ContributesAndroidInjector(modules = [SampleCollectionBuildersModule::class])
    abstract fun contributeSampleCollectionActivity(): SampleCollectionActivity

    @ContributesAndroidInjector(modules = [SampleProcessingBuildersModule::class])
    abstract fun contributeSampleProcessingActivity(): SampleProcessingActivity

    @ContributesAndroidInjector(modules = [SampleStorageBuildersModule::class])
    abstract fun contributeSampleStorageActivity(): SampleStorageActivity

    @ContributesAndroidInjector(modules = [SpirometryBuilderModule::class])
    abstract fun contributeSpirometryActivity(): SpirometryActivity

    @ContributesAndroidInjector(modules = [WebBuildersModule::class])
    abstract fun contributeWebViewActivity(): WebViewActivity

    @ContributesAndroidInjector(modules = [ReportBuildersModule::class])
    abstract fun contributeReportViewActivity(): ReportViewActivity

    @ContributesAndroidInjector(modules = [ActivityTrackerBuildersModule::class])
    abstract fun contributeActivityTrackerActivity(): ActivityTrackerActivity

    @ContributesAndroidInjector(modules = [BloodPressureBuildersModule::class])
    abstract fun contributeBloodPressureActivity(): BloodPressureActivity

    @ContributesAndroidInjector(modules = [ScreeningHomeModule::class])
    abstract fun contributeScreeningHomeActivity(): ScreeningHomeActivity

    @ContributesAndroidInjector(modules = [SampleHomeModule::class])
    abstract fun contributeSampleHomeActivity(): SampleHomeActivity

    @ContributesAndroidInjector(modules = [IntakeBuildersModule::class])
    abstract fun contributeIntakeActivity(): IntakeActivity

    @ContributesAndroidInjector(modules = [HeightWeightBuilderModule::class])
    abstract fun contributeHeightWeightActivity(): HeightWeightActivity

    @ContributesAndroidInjector(modules = [HipWaistBuilderModule::class])
    abstract fun contributeHipWaistActivity(): HipWaistActivity

    @ContributesAndroidInjector(modules = [GripStrengthBuilderModule::class])
    abstract fun contributeGripStrengthActivity(): GripStrengthActivity

    @ContributesAndroidInjector(modules = [DXABuilderModule::class])
    abstract fun contributeDXAActivity(): DXAActivity

    @ContributesAndroidInjector(modules = [UltrasoundBuilderModule::class])
    abstract fun contributeUltrasoundActivity(): UltrasoundActivity

    @ContributesAndroidInjector(modules = [FoodQuestionnaireBuilderModule::class])
    abstract fun contributeFoodQuestionnaireActivity(): FoodQuestionnaireActivity

    @ContributesAndroidInjector(modules = [VisualAcuityBuilderModule::class])
    abstract fun contributeVisualAcuityActivity(): VisualAcuityActivity

    @ContributesAndroidInjector(modules = [TreadmillBuilderModule::class])
    abstract fun contributeTreadmillActivity(): TreadmillActivity

    @ContributesAndroidInjector(modules = [CognitionBuilderModule::class])
    abstract fun contributeCognitionActivity(): CognitionActivity

    @ContributesAndroidInjector(modules = [HLQSelfBuilderModule::class])
    abstract fun contributeHLQSelfActivity(): HLQSelfActivity

    @ContributesAndroidInjector(modules = [DashboardAllStationsBuildersModule::class])
    abstract fun contributeDashboardAllStationsActivity(): DashboardAllStationsActivity

    @ContributesAndroidInjector(modules = [DashboardAllParticipantsBuildersModule::class])
    abstract fun contributeDashboardAllParticipantsActivity(): DashboardAllParticipantsActivity

    @ContributesAndroidInjector(modules = [CheckoutBuildersModule::class])
    abstract fun contributecheckoutActivity(): CheckoutActivity

    @ContributesAndroidInjector(modules = [VicorderBuildersModule::class])
    abstract fun contributeVicorderActivity(): VicorderActivity

    @ContributesAndroidInjector(modules = [SkinBuildersModule::class])
    abstract fun contributeSkinActivity(): SkinActivity

    @ContributesAndroidInjector(modules = [OctaBuildersModule::class])
    abstract fun contributeOctaActivity(): OctaActivity

    @ContributesAndroidInjector(modules = [HLQSelfTwoBuilderModule::class])
    abstract fun contributeHLQSelfTwoActivity(): HLQSelfTwoActivity

    @ContributesAndroidInjector(modules = [WebTwoBuildersModule::class])
    abstract fun contributeWebViewTwoActivity(): WebViewTwoActivity

}
