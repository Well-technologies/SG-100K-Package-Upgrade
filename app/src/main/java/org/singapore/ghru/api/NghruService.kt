package org.singapore.ghru.api

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.singapore.ghru.jobs.SyncHouseholdMemberJob
import org.singapore.ghru.vo.*
import org.singapore.ghru.vo.request.*
import org.singapore.ghru.vo.request.Member
import retrofit2.Call
import retrofit2.http.*


/**
 * REST API access points
 */
interface NghruService {

    @Headers(
        "Accept: application/json",
        "Content-type:application/json"
    )
    @FormUrlEncoded
    @POST("api/login")
    fun getAccessToken(
        @Field(value = "email", encoded = false) email: String, @Field(
            value = "password",
            encoded = false
        ) password: String
    ): LiveData<ApiResponse<ResourceData<LoginData>>>

    @FormUrlEncoded
    @POST("api/login")
    fun getAccessToken(@FieldMap test: Map<String, String>): LiveData<ApiResponse<AccessToken>>

    @POST("api/refresh")
    fun getRefresh(@Body refreshToken: RefreshToken): LiveData<ApiResponse<AccessToken>>


    @FormUrlEncoded
    @POST("api/login")
    fun getAccessTokenOld(@FieldMap test: Map<String, String>): LiveData<ApiResponse<ResourceData<LoginData>>>

    @POST("api/household")
    fun addHousehold(@Body household: Household): LiveData<ApiResponse<ResourceData<HouseholdData>>>

    @POST("api/v2/household")
    fun addHouseholdRequest(@Body household: HouseholdRequestMeta): LiveData<ApiResponse<ResponceData>>

    @POST("api/v2/household")
    fun addHouseholdRequestSync(@Body household: HouseholdRequestMeta): Call<ResourceData<HouseholdData>>

    @POST("api/household/{householdId}/members")
    fun addMember(@Body member: Member, @Path("householdId") householdId: String): LiveData<ApiResponse<ResourceData<MemberData>>>

    @POST("api/household/{householdId}/members")
    fun addMemberSync(@Body member: List<SyncHouseholdMemberJob.MemberDTO>, @Path("householdId") householdId: String): Call<ResourceData<List<SyncHouseholdMemberJob.MemberDTO>>>

    @POST("api/household/{householdId}/members")
    fun addMembers(@Body member: List<SyncHouseholdMemberJob.MemberDTO>, @Path("householdId") householdId: String): LiveData<ApiResponse<ResourceData<List<SyncHouseholdMemberJob.MemberDTO>>>>

    @GET("api/household/{enumeration_id}/members")
    fun getMember(@Path("enumeration_id") householdId: String, @Query("registered") registered: String): LiveData<ApiResponse<ResourceData<List<Member>>>>

    @GET("api/v2/household/{enumeration_id}")
    fun getHouseHold(@Path("enumeration_id") enumerationId: String): LiveData<ApiResponse<ResourceData<HouseholdBodyData>>>

    @GET("api/v2/household")
    fun getHouseHolds(): LiveData<ApiResponse<ResourceData<List<HouseholdRequestMetaResponce>>>>


    @GET("api/v2/household/{enumeration_id}")
    fun getHouseHoldX(@Path("enumeration_id") enumerationId: String): LiveData<ApiResponse<ResourceData<HouseholdRequestMeta>>>

    @GET("api/profile/screening/{screening_id}")
    fun getParticipant(@Path("screening_id") screeningId: String): LiveData<ApiResponse<ResourceData<Participant>>>

    @GET("api/profile/screening/{screening_id}")
    fun getParticipantRequest(@Path("screening_id") screeningId: String): LiveData<ApiResponse<ResourceData<ParticipantRequest>>>

    @GET("api/profile/screening/{screening_id}")
    fun getParticipantRequest(@Path("screening_id") screeningId: String, @Query("station") station: String): LiveData<ApiResponse<ResourceData<ParticipantRequest>>>


    @GET("api/user")
    fun getUser(): LiveData<ApiResponse<ResourceData<User>>>


    @POST("api/participants")
    fun addParticipantRequest(@Body participantRequest: ParticipantRequest): LiveData<ApiResponse<ResourceData<Participant>>>


    @POST("api/participants")
    fun addParticipantRequestSync(@Body participantRequest: ParticipantRequest): Call<ResourceData<ParticipantRequest>>


    @GET("api/assets/screening/{screening_id}")
    fun getAssets(@Path("screening_id") screeningId: String, @Query("purpose") purpose: String): LiveData<ApiResponse<ResourceData<List<Asset>>>>


    @Multipart
    @POST("api/assets")
    fun upload(
        @Part image: MultipartBody.Part, @Part("screening_id") screeningId: RequestBody, @Part("subject_id") subjectId: RequestBody, @Part(
            "subject_type"
        ) subjectType: RequestBody, @Part("purpose") purpose: RequestBody
    ): LiveData<ApiResponse<Message>>

    @Multipart
    @POST("api/assets")
    fun uploadBackground(
        @Part image: MultipartBody.Part, @Part("screening_id") screeningId: RequestBody, @Part("subject_id") subjectId: RequestBody, @Part(
            "subject_type"
        ) subjectType: RequestBody, @Part("purpose") purpose: RequestBody
    ): Call<Message>


    @POST("api/screening/{screening_id}/body-measurement")
    fun addBodyMeasurementRequest(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: BodyMeasurementRequest): LiveData<ApiResponse<ResourceData<BodyMeasurementRequest>>>

//    @POST("api/screening/{screening_id}/body-measurement")
//    fun addBodyMeasurementMeta(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: BodyMeasurementMeta): LiveData<ApiResponse<ResourceData<Message>>>

    @GET("api/screening/{screening_id}/height-weight")
    fun getBodyMeasurementMeta(@Path("screening_id") screeningId: String): LiveData<ApiResponse<ResourceData<StationData<BodyMeasurementMeta>>>>
//    fun getBodyMeasurementMeta(@Path("screening_id") screeningId: String): LiveData<ApiResponse<BodyMeasurementMetaResonce>>


    @POST("api/screening/{screening_id}/body-measurement")
    fun addBodyMeasurementRequestSync(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: BodyMeasurementRequest): Call<ResourceData<Message>>

    @POST("api/screening/{screening_id}/body-measurement")
    fun addBodyMeasurementMetaSync(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: BodyMeasurementMeta): Call<ResourceData<BodyMeasurementRequest>>

//    @POST("api/screening/{screening_id}/body-measurement")
//    fun addBodyMeasurementMetaSync(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: HeightWeightMeasurementMeta): Call<ResourceData<BodyMeasurementRequest>>

    @POST("api/screening/{screening_id}/ecg")
    fun addECGSync(@Path("screening_id") screeningId: String, @Body mECGStatus: ECGStatus): LiveData<ApiResponse<ResourceData<ECG>>>

    @POST("api/screening/{screening_id}/ecg")
    fun addECG(@Path("screening_id") screeningId: String, @Body mECGStatus: ECGStatus): Call<ResourceData<ECG>>

    @POST("api/screening/{screening_id}/fundoscopy")
    fun addFundoscopyGSync(@Path("screening_id") screeningId: String, @Body fundoscopyRequest: FundoscopyRequest?): LiveData<ApiResponse<ResourceData<ECG>>>

    @POST("api/screening/{screening_id}/fundoscopy")
    fun addFundoscopy(@Path("screening_id") screeningId: String, @Body comment: FundoscopyRequest?): Call<ResourceData<ECG>>


//    @POST("api/screening/{screening_id}/sample/{sample_id}")
//    fun addSampleSync(@Path("screening_id") screeningId: String, @Path("sample_id") sampleId: String,@Body comment: SampleCreateRequest?): LiveData<ApiResponse<ResourceData<SampleData>>>

    @POST("api/screening/{screening_id}/sample")
    fun addSampleSync(@Path("screening_id") screeningId: String, @Body comment: SampleCreateRequest?): LiveData<ApiResponse<ResourceData<SampleData>>>


    @POST("api/screening/{screening_id}/sample/{sample_id}")
    fun addSample(@Path("screening_id") screeningId: String, @Path("sample_id") sampleId: String,@Body comment: SampleCreateRequest?): Call<ResourceData<SampleData>>


    @PUT("api/sample/{sample_id}")
    fun addSampleProcessSync(@Path("sample_id") sampleId: String, @Body sampleProcess: SampleProcess): LiveData<ApiResponse<Message>>

    @PUT("api/sample/{sample_id}")
    fun addSampleProcess(@Path("sample_id") sampleId: String, @Body sampleProcess: SampleProcess): Call<ResourceData<Message>>


    @PUT("api/sample/{sample_id}")
    fun addSampleStorageSync(@Path("sample_id") sampleId: String, @Body sampleStorageRequest: SampleStorageRequest): LiveData<ApiResponse<ResourceData<SampleData>>>

    @PUT("api/sample/{sample_id}")
    fun addSampleStoyage(@Path("sample_id") sampleId: String, @Body sampleStorageRequest: SampleStorageRequest): Call<ResourceData<SampleData>>


    @POST("api/screening/{screening_id}/spirometry")
    fun addSpirometrySync(@Path("screening_id") screeningId: String, @Body spirometryData: SpirometryRequest): LiveData<ApiResponse<ResourceData<CommonResponce>>>


    @POST("api/screening/{screening_id}/spirometry")
    fun addSpirometry(@Path("screening_id") screeningId: String, @Body spirometryData: SpirometryRequest): Call<ResourceData<CommonResponce>>


    @POST("/api/v2/participants")
    fun addParticipantMetaSync(@Body participantMeta: ParticipantMeta): LiveData<ApiResponse<ResourceData<CommonResponce>>>

    @GET("/api/v2/participants/screening/{screening_id}")
    fun getParticipantRequestMeta(@Path("screening_id") screeningId: String): LiveData<ApiResponse<ResourceData<ParticipantMeta>>>

    @GET("/api/v2/participants")
    fun getParticipantRequestMetas(): LiveData<ApiResponse<ResourceData<List<ParticipantMeta>>>>

    @POST("/api/v2/participants")
    fun addParticipantMeta(@Body participantMeta: ParticipantMeta): Call<ResourceData<CommonResponce>>

    @POST("/api/questionnaire/result")
    fun addSurveySync(@Body survey: QuestionMeta): LiveData<ApiResponse<ResourceData<CommonResponce>>>

    @POST("/api/screening/{screening_id}/report")
    fun addSurveyCompleteSync(@Path("screening_id") screeningId: String, @Body reportRequestMeta: ReportRequestMeta): LiveData<ApiResponse<ResourceData<CommonResponce>>>


    @POST("/api/questionnaire/result")
    fun addSurvey(@Body survey: QuestionMeta): Call<ResourceData<CommonResponce>>

    @GET("/api/sample")
    fun getSamplePending(@Query("status") status: String): LiveData<ApiResponse<ResourceData<List<SampleRequest>>>>


    @GET("/api/sample")
    fun getSamples(): LiveData<ApiResponse<ResourceData<List<SampleRequest>>>>


    @GET("/api/sample/{sample_id}")
    fun getSampleBySampleId(@Path("sample_id") sampleId: String): LiveData<ApiResponse<ResourceData<SampleRequest>>>

    @GET("/api/storage/{storage_d}")
    fun getSampleByStorageId(@Path("storage_d") storageId: String): LiveData<ApiResponse<ResourceData<SampleRequest>>>


    @POST("/api/storage/{storage_id}")
    fun addStorageSync(@Path("storage_id") storageId: String, @Body storageDto: StorageDto): LiveData<ApiResponse<Message>>


    @POST("/api/storage/{storage_id}")
    fun addStorage(@Path("storage_id") storageId: String, @Body storageDto: StorageDto): Call<ResourceData<Message>>

    @PUT("api/screening/{screening_id}/station/cancel")
    fun addCancelRequest(@Path("screening_id") screeningId: String, @Body cancelRequest: CancelRequest): LiveData<ApiResponse<ResourceData<MessageCancel>>>

    @PUT("api/v2/screening/{screening_id}/station/cancel")
    fun addNewCancelRequest(@Path("screening_id") screeningId: String, @Body cancelRequest: CancelRequest): LiveData<ApiResponse<ResourceData<MessageCancel>>>

    @PUT("api/sample/{sample_id}/station/cancel")
    fun addCancelSampleRequest(@Path("sample_id") screeningId: String, @Body cancelRequest: CancelRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @PUT("api/storage/{storage_id}/station/cancel")
    fun addCancelStorageRequest(@Path("storage_id") screeningId: String, @Body cancelRequest: CancelRequest): LiveData<ApiResponse<ResourceData<Message>>>


    @PUT("api/screening/{screening_id}/station/cancel")
    fun addCancelRequestSync(@Path("screening_id") screeningId: String, @Body cancelRequest: CancelRequest): Call<ResourceData<Message>>

    @PUT("api/screening/{screening_id}/station/cancel")
    fun addCancelAxivityRequestSync(@Path("screening_id") screeningId: String, @Body cancelRequest: CancelRequest): Call<ResourceData<MessageCancel>>

    @PUT("api/sample/{sample_id}/station/cancel")
    fun addCancelSampleRequestSync(@Path("sample_id") screeningId: String, @Body cancelRequest: CancelRequest): Call<ResourceData<Message>>

    @PUT("api/storage/{storage_id}/station/cancel")
    fun addCancelStorageRequestSync(@Path("storage_id") screeningId: String, @Body cancelRequest: CancelRequest): Call<ResourceData<Message>>

    @GET("/api/devices?status=active")
    fun getStationDevices(): LiveData<ApiResponse<ResourceData<List<StationDeviceData>>>>

//    @POST("api/assets")
//    fun upload( @Part image: MultipartBody.Part, @Field(value = "screening_id", encoded = false) screeningId: String, @Field(value = "subject_id", encoded = false) subjectId: String, @Field(value = "subject_type", encoded = false) subjectType: String, @Field(value = "purpose", encoded = false) purpose: RequestBody): LiveData<ApiResponse<String>>
//
//    @Multipart
//    @POST("api/assets")
//    fun upload( @FieldMap test: Map<String, String>, @Part image: MultipartBody.Part): LiveData<ApiResponse<String>>
//
//    @Multipart
//    @POST("api/assets")
//    fun uploadTest( @PartMap params: Map<String, RequestBody>): LiveData<ApiResponse<String>>

    @POST("api/screening/{screening_id}/blood-pressure")
    fun addBloodPresureRequest(@Path("screening_id") screeningId: String, @Body bloodPressureMetaRequest: BloodPressureMetaRequest): LiveData<ApiResponse<ResourceData<BloodPressureMetaRequest>>>

    @POST("api/screening/{screening_id}/blood-pressure")
    fun addBloodPresureRequestSync(@Path("screening_id") screeningId: String, @Body bloodPressureMetaRequest: BloodPressureMetaRequest): Call<ResourceData<BloodPressureMetaRequest>>


    @POST("api/screening/{screening_id}/axivity")
    fun addAxivity(@Path("screening_id") screeningId: String, @Body axivity: Axivity): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/axivity")
    fun addAxivitySync(@Path("screening_id") screeningId: String, @Body axivity: Axivity): Call<ResourceData<Message>>

    @GET("api/questionnaire/{language}")
    fun getQuestionnaire(@Path("language") language: String): LiveData<ApiResponse<ResourceData<Questionnaire>>>

    @GET("api/questionnaire")
    fun getQuestionnaire(): LiveData<ApiResponse<ResourceData<List<Questionnaire>>>>

    @POST("/api/screening/{screening_id}/intake")
    fun postIntake(@Body body : IntakeRequestNew , @Path("screening_id") screening_id: String): LiveData<ApiResponse<ResourceData<IntakeResponse>>>

    @PUT("/api/screening/{screening_id}/intake")
    fun updateIntake(@Body body : IntakeRequestNew , @Path("screening_id") screening_id: String): LiveData<ApiResponse<ResourceData<IntakeResponse>>>

//    @GET("/api/devices?measurement=hemoglobin")
//    fun getHemoglobinStationDevices(): LiveData<ApiResponse<ResourceData<List<StationDeviceData>>>>

    @POST("api/screening/{screening_id}/grip-strength")
    fun addGripStrengthSync(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: GripStrengthRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/hip-waist")
    fun addHipWaistSync(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: HipWaistRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/dxa")
    fun addDXA(@Path("screening_id") screeningId: String, @Body comment: DXARequest?): Call<ResourceData<ECG>>

    @POST("api/screening/{screening_id}/dxa")
    fun addDXAGSync(@Path("screening_id") screeningId: String, @Body dxaRequest: DXARequest?): LiveData<ApiResponse<ResourceData<ECG>>>

    @POST("api/screening/{screening_id}/ultrasound")
    fun addUltrasound(@Path("screening_id") screeningId: String, @Body comment: UltrasoundRequest?): Call<ResourceData<ECG>>

    @POST("api/screening/{screening_id}/ultrasound")
    fun addUltrasoundGSync(@Path("screening_id") screeningId: String, @Body dxaRequest: UltrasoundRequest?): LiveData<ApiResponse<ResourceData<ECG>>>

    @POST("api/screening/{screening_id}/visual-acuity")
    fun addVisualAcuitySync(@Path("screening_id") screeningId: String, @Body visualAcuityRequest: VisualAcuityRequest): LiveData<ApiResponse<ResourceData<Message>>>


    @POST("api/screening/{screening_id}/treadmill")
    fun addTreadmill(@Path("screening_id") screeningId: String, @Body comment: TreadmillRequest?): Call<ResourceData<ECG>>

    @POST("api/screening/{screening_id}/treadmill")
    fun addTreadmillGSync(@Path("screening_id") screeningId: String, @Body treadmillRequest: TreadmillRequest?): LiveData<ApiResponse<ResourceData<ECG>>>

    @POST("api/screening/{screening_id}/food-frequency")
    fun addFFQSync(@Path("screening_id") screeningId: String, @Body ffqRequest: FFQRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @GET("api/screening/food-frequency/{screening_id}")
    fun getParticipantCredin(@Path("screening_id") screeningId: String): LiveData<ApiResponse<ResourceData<ParticipantCre>>>

    @PUT("/api/questionnaire/result/{screening_id}")
    fun updateHLQ(@Body body : HLQResponse , @Path("screening_id") screening_id: String): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/cognition")
    fun addCognitionSync(@Path("screening_id") screeningId: String, @Body cognitionRequest: CognitionRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @GET("api/questionnaire_self")
    fun getQuestionnaireSelf(): LiveData<ApiResponse<ResourceData<List<QuestionnaireSelf>>>>

    @GET("api/questionnaire_self/{language}")
    fun getQuestionnaireSelf(@Path("language") language: String): LiveData<ApiResponse<ResourceData<QuestionnaireSelf>>>

    @POST("/api/questionnaire_self/result")
    fun addSurveySelfSync(@Body survey: QuestionMeta): LiveData<ApiResponse<ResourceData<CommonResponce>>>

    @PUT("/api/questionnaire_self/result/{screening_id}")
    fun updateHLQSelf(@Body body : HLQResponse , @Path("screening_id") screening_id: String): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/height-weight")
    fun addBodyMeasurementMeta(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: BodyMeasurementMeta): LiveData<ApiResponse<ResourceData<Message>>>

    @GET("api/readings/{screening_id}")
    fun getHL7Readings(@Path("screening_id") screeningId: String, @Query("station") station: String): LiveData<ApiResponse<ResourceData<HL7Readings>>>

    @PUT("/api/screening/{screening_id}/food-frequency")
    fun updateFFQ(@Body body : FFQRequestNew , @Path("screening_id") screening_id: String): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/food-frequency")
    fun addFFQSyncNew(@Path("screening_id") screeningId: String, @Body ffqRequest: FFQRequestNew1): LiveData<ApiResponse<ResourceData<Message>>>

    @PUT("/api/screening/{screening_id}/cognition")
    fun updateCog(@Body body : CognitionRequestNew , @Path("screening_id") screening_id: String): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/cognition")
    fun addCogSyncNew(@Path("screening_id") screeningId: String, @Body ffqRequest: CognitionRequestNew1): LiveData<ApiResponse<ResourceData<Message>>>

    @GET("/api/v2/participants/screening/{screening_id}")
    fun getSingleParticipant(@Path("screening_id") screening_id: String): LiveData<ApiResponse<ResourceData<ParticipantStationsData<List<ParticipantStation>>>>>

    @GET("/api/screening/status/prticipant")
    fun allParticipants(@Query("page") page: Int, @Query("keyword") keyWord: String): LiveData<ApiResponse<ResourceDataWithMeta<List<ParticipantStationItemNewNew>>>>

    @GET("/api/screening/status/station")
    fun getStationParticipants(@Query("page") page: Int, @Query("station") station: String, @Query("status") status: String, @Query("date") date: String, @Query("sort") sort: String): LiveData<ApiResponse<ResourceDataWithMeta<StationParticipant>>>

    @POST("api/screening/{screening_id}/checkout")
    fun addCheckout(@Path("screening_id") screeningId: String, @Body checkoutRequest: CheckoutRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @GET("/api/screening/status/prticipant/without")
    fun getNotStartedStationParticipants(@Query("page") page: Int, @Query("station") station: String, @Query("status") status: String, @Query("date") date: String, @Query("sort") sort: String): LiveData<ApiResponse<ResourceDataWithMeta<StationParticipant>>>

    @GET("api/screening/{screening_id}/ecg")
    fun getEcgData(@Path("screening_id") screeningId: String): LiveData<ApiResponse<ResourceData<ECGData>>>

    @GET("/api/screening/food-frequency/{screening_id}/{lang}")
    fun getFFQLanguage(@Path("screening_id") screeningId: String, @Path("lang") lang: String): LiveData<ApiResponse<ResourceData<ParticipantCre>>>

    // to het height and weight to DXA station
    @GET("api/screening/{screening_id}/height-weight")
    fun getBodyMeasurementMetaNew(@Path("screening_id") screeningId: String): LiveData<ApiResponse<ResourceData<StationData<BodyMeasurementMetaNew>>>>

    // to new cancel request with meta to capture to start and end time
    @PUT("api/v2/screening/{screening_id}/station/cancel")
    fun addNewCancelRequestWithMeta(@Path("screening_id") screeningId: String, @Body cancelRequest: CancelRequestWithMeta): LiveData<ApiResponse<ResourceData<MessageCancel>>>

    @POST("api/screening/{screening_id}/vicorder")
    fun addVicorder(@Path("screening_id") screeningId: String, @Body vicorderRequest: VicorderRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/skin")
    fun addSkin(@Path("screening_id") screeningId: String, @Body skinRequest: SkinRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @POST("api/screening/{screening_id}/octa")
    fun addOcta(@Path("screening_id") screeningId: String, @Body octaRequest: OctaRequest): LiveData<ApiResponse<ResourceData<Message>>>

    @FormUrlEncoded
    @POST("api/sso/login")
    fun getAccessTokenNew(@FieldMap test: Map<String, String>): LiveData<ApiResponse<AccessToken>>

    @POST("api/screening/{screening_id}/height-weight")
    fun syncHeightAndWeight(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: BodyMeasurementMetaWithoutReadings): LiveData<ApiResponse<ResourceData<BodyMeasurementMetaWithoutReadings>>>

    @POST("api/screening/{screening_id}/height-weight")
    fun syncHeightAndWeightWithManuAuto(@Path("screening_id") screeningId: String, @Body bodyMeasurementRequest: BodyMeasurementMetaManualAuto): LiveData<ApiResponse<ResourceData<BodyMeasurementMetaManualAuto>>>

}
