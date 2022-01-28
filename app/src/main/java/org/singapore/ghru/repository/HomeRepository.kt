package org.singapore.ghru.repository

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.singapore.ghru.R
import org.singapore.ghru.util.LocaleManager
import org.singapore.ghru.vo.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeRepository @Inject constructor(
    private val context: Context,
    private val localeManager: LocaleManager
) {

    fun getHomeItems(): LiveData<Resource<List<HomeItem>>> {


        val mHomeItem = HomeItem(
            1,
            getStringByLocalBefore17(context, R.string.screening_register_participant, localeManager.getLanguage()),
            R.drawable.ic_icon_register_patient
        )

        val mHomeItem1 = HomeItem( // height and weight
            2,
            getStringByLocalBefore17(context, R.string.screening_height_weight, localeManager.getLanguage()),
            R.drawable.weighing_scale
        )

        val mHomeItem2 = HomeItem(
            3,
            getStringByLocalBefore17(context, R.string.screening_blood_pressure, localeManager.getLanguage()),
            R.drawable.ic_icon_bp
        )

        val mHomeItem3 = HomeItem( // hip and waist
            4,
            getStringByLocalBefore17(context, R.string.screening_hip_waist, localeManager.getLanguage()),
            R.drawable.ic_icon_body_measurements
        )

        val mHomeItem4 = HomeItem(
            5,
            getStringByLocalBefore17(context, R.string.screening_biological_samples, localeManager.getLanguage()),
            R.drawable.ic_icon_bio_samples
        )

        val mHomeItem5 = HomeItem(
            6,
            getStringByLocalBefore17(context, R.string.ecg, localeManager.getLanguage()),
            R.drawable.ic_icon_ecg
        )

        val mHomeItem6 = HomeItem(
            7,
            getStringByLocalBefore17(context, R.string.spirometry, localeManager.getLanguage()),
            R.drawable.ic_icon_spirometry
        )

        val mHomeItem7 = HomeItem(
            8,
            getStringByLocalBefore17(context, R.string.fundoscopy, localeManager.getLanguage()),
            R.drawable.ic_icon_fundoscopy
        )

        val mHomeItem12 = HomeItem(
            9,
            getStringByLocalBefore17(context, R.string.screening_dxa, localeManager.getLanguage()),
            R.drawable.ic_icon_dxa
        )

        val mHomeItem8 = HomeItem(
            10,
            getStringByLocalBefore17(context, R.string.activity_tracker, localeManager.getLanguage()),
            R.drawable.ic_icon_activity_tracker
        )

        val mHomeItem9 = HomeItem(
            11,
            getStringByLocalBefore17(context, R.string.screening_hlq, localeManager.getLanguage()),
            R.drawable.ic_icon_healthy_lifestyle
        )

        val mHomeItem13 = HomeItem(
            12,
            getStringByLocalBefore17(context, R.string.screening_ultrasound, localeManager.getLanguage()),
            R.drawable.ultrasound
        )

        val mHomeItem10 = HomeItem( // grip view
            13,
            getStringByLocalBefore17(context, R.string.screening_grip, localeManager.getLanguage()),
            R.drawable.hand_grip
        )

        val mHomeItem14 = HomeItem(
            14,
            getStringByLocalBefore17(context, R.string.screening_acuity, localeManager.getLanguage()),
            R.drawable.ophthalmology
        )

        val mHomeItem15 = HomeItem(
            15,
            getStringByLocalBefore17(context, R.string.screening_cognition, localeManager.getLanguage()),
            R.drawable.brain
        )

        val mHomeItem16 = HomeItem(
            16,
            getStringByLocalBefore17(context, R.string.screening_food_questionnaire, localeManager.getLanguage()),
            R.drawable.food
        )

        val mHomeItem17 = HomeItem(
            17,
            getStringByLocalBefore17(context, R.string.screening_treadmill, localeManager.getLanguage()),
            R.drawable.treadmill
        )

        val mHomeItem18 = HomeItem(
            18,
            getStringByLocalBefore17(context, R.string.screening_hlq_self, localeManager.getLanguage()),
            R.drawable.self1_36
        )

        val mHomeItem19 = HomeItem(
            19,
            getStringByLocalBefore17(context, R.string.screening_vicorder, localeManager.getLanguage()),
            R.drawable.vicorder
        )

        val mHomeItem20 = HomeItem(
            20,
            getStringByLocalBefore17(context, R.string.screening_skin, localeManager.getLanguage()),
            R.drawable.skin
        )

        val mHomeItem21 = HomeItem(
            21,
            getStringByLocalBefore17(context, R.string.screening_octa, localeManager.getLanguage()),
            R.drawable.octa
        )

        val test = ArrayList<HomeItem>()

        test.add(mHomeItem)
        test.add(mHomeItem1)
        test.add(mHomeItem2)


        test.add(mHomeItem5)
        test.add(mHomeItem6)

        test.add(mHomeItem3)
        test.add(mHomeItem7)

        test.add(mHomeItem12)

        test.add(mHomeItem8)


        test.add(mHomeItem13)

        test.add(mHomeItem10)

        test.add(mHomeItem14)
        test.add(mHomeItem15)
        test.add(mHomeItem16)
        test.add(mHomeItem17)
        test.add(mHomeItem18)
        test.add(mHomeItem9)

        test.add(mHomeItem4)
        test.add(mHomeItem19)
        test.add(mHomeItem20)
        test.add(mHomeItem21)

        val homeItems = MutableLiveData<Resource<List<HomeItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems
    }


    fun getSampleItems(): LiveData<Resource<List<HomeItem>>> {


        val mHomeItem = HomeItem(
            1,
            getStringByLocalBefore17(context, R.string.sample_management_processing, localeManager.getLanguage()),
            R.drawable.ic_icon_pathology
        )

        val mHomeItem1 = HomeItem(
            2,
            getStringByLocalBefore17(context, R.string.sample_management_storage, localeManager.getLanguage()),
            R.drawable.ic_icon_cryo
        )


        val test = ArrayList<HomeItem>()

        test.add(mHomeItem)
        test.add(mHomeItem1)

        val homeItems = MutableLiveData<Resource<List<HomeItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems

    }


    private fun getStringByLocalBefore17(context: Context, resId: Int, language: String): String {
        val currentResources = context.resources
        val assets = currentResources.assets
        val metrics = currentResources.displayMetrics
        val config = Configuration(currentResources.configuration)
        val locale = Locale(language)
        Locale.setDefault(locale)
        config.locale = locale
        val defaultLocaleResources = Resources(assets, metrics, config)
        val string = defaultLocaleResources.getString(resId)
        // Restore device-specific locale
        Resources(assets, metrics, currentResources.configuration)
        return string
    }

    fun getDashboardItems(): LiveData<Resource<List<HomeItem>>> {

        val mHomeItem1 = HomeItem(
            2,
            getStringByLocalBefore17(context, R.string.screening_height_weight, localeManager.getLanguage()),
            R.drawable.weighing_scale
        )

        val mHomeItem2 = HomeItem(
            3,
            getStringByLocalBefore17(context, R.string.screening_blood_pressure, localeManager.getLanguage()),
            R.drawable.ic_icon_bp
        )

        val mHomeItem3 = HomeItem(
            4,
            getStringByLocalBefore17(context, R.string.screening_hip_waist, localeManager.getLanguage()),
            R.drawable.ic_icon_body_measurements
        )

        val mHomeItem4 = HomeItem(
            5,
            getStringByLocalBefore17(context, R.string.screening_biological_samples, localeManager.getLanguage()),
            R.drawable.ic_icon_bio_samples
        )

        val mHomeItem5 = HomeItem(
            6,
            getStringByLocalBefore17(context, R.string.ecg, localeManager.getLanguage()),
            R.drawable.ic_icon_ecg
        )

        val mHomeItem6 = HomeItem(
            7,
            getStringByLocalBefore17(context, R.string.spirometry, localeManager.getLanguage()),
            R.drawable.ic_icon_spirometry
        )

        val mHomeItem7 = HomeItem(
            8,
            getStringByLocalBefore17(context, R.string.fundoscopy, localeManager.getLanguage()),
            R.drawable.ic_icon_fundoscopy
        )

        val mHomeItem8 = HomeItem(
            9,
            getStringByLocalBefore17(context, R.string.screening_dxa, localeManager.getLanguage()),
            R.drawable.ic_icon_dxa
        )

        val mHomeItem9 = HomeItem(
            10,
            getStringByLocalBefore17(context, R.string.activity_tracker, localeManager.getLanguage()),
            R.drawable.ic_icon_activity_tracker
        )

        val mHomeItem10 = HomeItem(
            11,
            getStringByLocalBefore17(context, R.string.screening_hlq, localeManager.getLanguage()),
            R.drawable.ic_icon_healthy_lifestyle
        )

        val mHomeItem11 = HomeItem(
            12,
            getStringByLocalBefore17(context, R.string.screening_ultrasound, localeManager.getLanguage()),
            R.drawable.ultrasound
        )

        val mHomeItem12 = HomeItem( // grip view
            13,
            getStringByLocalBefore17(context, R.string.screening_grip, localeManager.getLanguage()),
            R.drawable.hand_grip
        )

        val mHomeItem13 = HomeItem(
            14,
            getStringByLocalBefore17(context, R.string.screening_acuity, localeManager.getLanguage()),
            R.drawable.ophthalmology
        )

        val mHomeItem14 = HomeItem(
            15,
            getStringByLocalBefore17(context, R.string.screening_cognition, localeManager.getLanguage()),
            R.drawable.brain
        )

        val mHomeItem15 = HomeItem(
            16,
            getStringByLocalBefore17(context, R.string.screening_food_questionnaire, localeManager.getLanguage()),
            R.drawable.food
        )

        val mHomeItem16 = HomeItem(
            17,
            getStringByLocalBefore17(context, R.string.screening_treadmill, localeManager.getLanguage()),
            R.drawable.treadmill
        )

        val mHomeItem17 = HomeItem(
            18,
            getStringByLocalBefore17(context, R.string.screening_hlq_self, localeManager.getLanguage()),
            R.drawable.self1_36
        )
        val mHomeItem18 = HomeItem(
            19,
            getStringByLocalBefore17(context, R.string.screening_checkout, localeManager.getLanguage()),
            R.drawable.checkout_36
        )
        val mHomeItem19 = HomeItem(
            20,
            getStringByLocalBefore17(context, R.string.screening_vicorder, localeManager.getLanguage()),
            R.drawable.vicorder
        )
        val mHomeItem20 = HomeItem(
            21,
            getStringByLocalBefore17(context, R.string.screening_skin, localeManager.getLanguage()),
            R.drawable.skin
        )
        val mHomeItem21 = HomeItem(
            22,
            getStringByLocalBefore17(context, R.string.screening_octa, localeManager.getLanguage()),
            R.drawable.octa
        )

        val test = ArrayList<HomeItem>()
        test.add(mHomeItem1)
        test.add(mHomeItem2)


        test.add(mHomeItem5)
        test.add(mHomeItem6)

        test.add(mHomeItem3)
        test.add(mHomeItem7)
        test.add(mHomeItem11)
        test.add(mHomeItem12)

        test.add(mHomeItem8)


        test.add(mHomeItem13)

        test.add(mHomeItem10)

        test.add(mHomeItem14)
        test.add(mHomeItem15)
        test.add(mHomeItem16)
        test.add(mHomeItem17)
        test.add(mHomeItem18)
        test.add(mHomeItem9)

        test.add(mHomeItem4)
        test.add(mHomeItem19)
        test.add(mHomeItem20)
        test.add(mHomeItem21)


        val homeItems = MutableLiveData<Resource<List<HomeItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems
    }

    fun getSelectedStationData(): LiveData<Resource<List<StationItem>>> {

        val mHomeItem1 = StationItem( // height and weight
            2,
            "PAA-0001-1",
            "18/01/2021",
            R.drawable.status_cancel
        )

        val mHomeItem2 = StationItem(
            3,
            "PAA-0001-2",
            "21/01/2021",
            R.drawable.status_complete
        )

        val mHomeItem3 = StationItem( // hip and waist
            4,
            "PAA-0001-3",
            "25/01/2021",
            R.drawable.status_progress
        )



        val test = ArrayList<StationItem>()
        test.add(mHomeItem1)
        test.add(mHomeItem2)

        test.add(mHomeItem3)


        val homeItems = MutableLiveData<Resource<List<StationItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems
    }

    fun getAllParticipantData(): LiveData<Resource<List<ParticipantStationsItem>>> {

        val mHomeItem1 = ParticipantStationsItem(
            1,
            "PAA-0001-1",
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel
        )

        val mHomeItem2 = ParticipantStationsItem(
            2,
            "PAA-0001-2",
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel

        )

        val mHomeItem3 = ParticipantStationsItem(
            3,
            "PAA-0001-3",
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel,
            R.drawable.status_progress,
            R.drawable.status_complete,
            R.drawable.status_cancel

        )



        val test = ArrayList<ParticipantStationsItem>()
        test.add(mHomeItem1)
        test.add(mHomeItem2)

        test.add(mHomeItem3)


        val homeItems = MutableLiveData<Resource<List<ParticipantStationsItem>>>()
        val resource = Resource(Status.SUCCESS, test, Message(null, null))
        homeItems.setValue(resource)

        return homeItems
    }
}
