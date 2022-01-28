package org.singapore.ghru.ui.devices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.singapore.ghru.AppExecutors
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.DevicesFragmentBinding
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.util.LocaleManager
import org.singapore.ghru.util.autoCleared
import javax.inject.Inject

class DevicesFragment : Fragment(), Injectable {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    @Inject
    lateinit var appExecutors: AppExecutors

    @Inject
    lateinit var localeManager: LocaleManager


    var binding by autoCleared<DevicesFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var stationViewModel: DevicesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<DevicesFragmentBinding>(
            inflater,
            R.layout.devices_fragment,
            container,
            false
        )
        binding = dataBinding
        setHasOptionsMenu(true)
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()
}
