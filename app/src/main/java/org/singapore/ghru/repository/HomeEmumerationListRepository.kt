package org.singapore.ghru.repository

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import org.singapore.ghru.R
import org.singapore.ghru.util.LocaleManager
import org.singapore.ghru.vo.HomeEmumerationListItem
import org.singapore.ghru.vo.Message
import org.singapore.ghru.vo.Resource
import org.singapore.ghru.vo.Status
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class HomeEmumerationListRepository @Inject constructor(
    private val context: Context,
    private val localeManager: LocaleManager
) {

    fun getHomeEmumerationListItems(): LiveData<Resource<List<HomeEmumerationListItem>>> {


//        val mHomeEmumerationListItem = HomeEmumerationListItem(
//            1,
//            getStringByLocalBefore17(context, R.string.enumeration, localeManager.getLanguage()),
//            R.drawable.ic_icon_enumeration
//        )

        val mHomeEmumerationListItem1 = HomeEmumerationListItem(
            2,
            getStringByLocalBefore17(context, R.string.screening, localeManager.getLanguage()),
            R.drawable.ic_icon_screening
        )

//        val mHomeEmumerationListItem6 = HomeEmumerationListItem(
//            6,
//            getStringByLocalBefore17(context, R.string.home_sample_management, localeManager.getLanguage()),
//            R.drawable.ic_icon_pathology
//        )

        val mHomeEmumerationListItem2 = HomeEmumerationListItem(
            3,
            getStringByLocalBefore17(context, R.string.home_reports, localeManager.getLanguage()),
            R.drawable.ic_icon_medical_report
        )

        val mHomeEmumerationListItem3 = HomeEmumerationListItem(
            4,
            getStringByLocalBefore17(context, R.string.home_by_station, localeManager.getLanguage()),
            R.drawable.station_48_1
        )

        val mHomeEmumerationListItem4 = HomeEmumerationListItem(
            5,
            getStringByLocalBefore17(context, R.string.home_by_participant, localeManager.getLanguage()),
            R.drawable.participant_48
        )

        val mHomeEmumerationListItem5 = HomeEmumerationListItem(
            6,
            getStringByLocalBefore17(context, R.string.home_checkout, localeManager.getLanguage()),
            R.drawable.checkout_36
        )

        val test = ArrayList<HomeEmumerationListItem>()

        //test.add(mHomeEmumerationListItem)
        test.add(mHomeEmumerationListItem1)
//        test.add(mHomeEmumerationListItem6)
        test.add(mHomeEmumerationListItem2)
        test.add(mHomeEmumerationListItem3)
        test.add(mHomeEmumerationListItem4)
        test.add(mHomeEmumerationListItem5)


        val homeItems = MutableLiveData<Resource<List<HomeEmumerationListItem>>>()
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
}
