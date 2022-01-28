package org.singapore.ghru.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.birbit.android.jobqueue.JobManager
import org.singapore.ghru.*
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.HomeFragmentBinding
import org.singapore.ghru.di.Injectable
//import org.singapore.ghru.network.ConnectivityReceiver
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.setTitleColor
import org.singapore.ghru.vo.HLQSelfActivity
import timber.log.Timber
import javax.inject.Inject


class HomeFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    var binding by autoCleared<HomeFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var homeViewModel: HomeViewModel

    private var adapter by autoCleared<HomeAdapter>()

    @Inject
    lateinit var  jobManager: JobManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<HomeFragmentBinding>(
            inflater,
            R.layout.home_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.detailToolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        return dataBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_setting -> {
                val intent = Intent(activity, SettingActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        jobManager.stop()
        binding.homeViewModel = homeViewModel;

        val adapter = HomeAdapter(dataBindingComponent, appExecutors) { homeItem ->

            Timber.d(homeItem.toString())

            if (homeItem.id == 1) {
                val intent = Intent(activity, RegisterPatientActivity::class.java)
                startActivity(intent)
            }
            if (homeItem.id == 2) { // height and weight
                val intent = Intent(activity, HeightWeightActivity::class.java)
                startActivity(intent)
            }
            if (homeItem.id == 3) {
                val intent = Intent(activity, BloodPressureActivity::class.java)
                startActivity(intent)
            }
            if (homeItem.id == 4) { // hip and waist
                val intent = Intent(activity, HipWaistActivity::class.java)
                startActivity(intent)

            }
//            if (homeItem.id == 4) { // hip and waist
//                val intent = Intent(activity, BodyMeasurementsActivity::class.java)
//                startActivity(intent)
//
//            }
            if (homeItem.id == 5) {
                val intent = Intent(activity, SampleCollectionActivity::class.java)
                startActivity(intent)
            }

            if (homeItem.id == 6) {
                val intent = Intent(activity, ECGActivity::class.java)
                startActivity(intent)
            }
            if (homeItem.id == 7) {

                val intent = Intent(activity, SpirometryActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 8) {
                val intent = Intent(activity, FundoscopyActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 9) {
                val intent = Intent(activity, DXAActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 10) {
                val intent = Intent(activity, ActivityTrackerActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 11) {

                val intent = Intent(activity, WebViewActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 12) {

                val intent = Intent(activity, UltrasoundActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 13) { // grip view

                val intent = Intent(activity, GripStrengthActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 14) {

                //Toast.makeText(activity, "Coming soon...", Toast.LENGTH_LONG).show()

                val intent = Intent(activity, VisualAcuityActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 15) {

                //Toast.makeText(activity, "Coming soon...", Toast.LENGTH_LONG).show()

                val intent = Intent(activity, CognitionActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 16) {

                //Toast.makeText(activity, "Coming soon...", Toast.LENGTH_LONG).show()

                val intent = Intent(activity, FoodQuestionnaireActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 17) {

//                Toast.makeText(activity, "Coming soon...", Toast.LENGTH_LONG).show()

                val intent = Intent(activity, TreadmillActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 18) {

                val intent = Intent(activity, HLQSelfActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 19) {

                val intent = Intent(activity, VicorderActivity::class.java)
                startActivity(intent)

            }
            if (homeItem.id == 20) {

                val intent = Intent(activity, SkinActivity::class.java)
                startActivity(intent)
            }
            if (homeItem.id == 21) {

                val intent = Intent(activity, OctaActivity::class.java)
                startActivity(intent)
            }

        }
        this.adapter = adapter
        binding.nghruList.adapter = adapter
        binding.nghruList.setLayoutManager(GridLayoutManager(activity, 3))
        homeViewModel.setId("en")

        homeViewModel.homeItem.observe(this, Observer { listResource ->


            if (listResource?.data != null) {
                adapter.submitList(listResource.data)
            } else {
                adapter.submitList(emptyList())
            }
        })
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
        //checkConnection(menu!!)
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()


    // Method to manually check connection status
//    private fun checkConnection(menu: Menu) {
//        val isConnected = ConnectivityReceiver.isConnected(context)
//        if (isConnected) {
//            menu.findItem(R.id.menu_text).setTitleColor(Color.WHITE)
//            menu.findItem(R.id.menu_text).setTitle("Online (Local)")
//            menu.findItem(R.id.menu_online).setIcon(R.drawable.ic_icon_local_lan)
//        } else {
//            menu.findItem(R.id.menu_text).setTitleColor(Color.RED)
//            menu.findItem(R.id.menu_text).setTitle("Offline")
//            menu.findItem(R.id.menu_online).setIcon(R.drawable.ic_icon_wifi_disconnected)
//        }
//        activity!!.invalidateOptionsMenu();
//    }
}
