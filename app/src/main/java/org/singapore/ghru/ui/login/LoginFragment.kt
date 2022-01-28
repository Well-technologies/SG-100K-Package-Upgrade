package org.singapore.ghru.ui.login

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import br.com.ilhasoft.support.validation.Validator
import com.auth0.android.Auth0
import com.auth0.android.Auth0Exception
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.BaseCallback
import com.auth0.android.provider.WebAuthProvider
import com.crashlytics.android.Crashlytics
import org.singapore.ghru.BuildConfig
import org.singapore.ghru.MainActivity
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.LoginFragmentBinding
import org.singapore.ghru.db.AccessTokenDao
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.ui.common.RetryCallback
import org.singapore.ghru.util.TokenManager
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.hideKeyboard
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Status
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.*
import javax.inject.Inject
import com.auth0.android.result.Credentials
import com.auth0.android.callback.Callback
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.VoidCallback
import com.auth0.android.result.UserProfile
import kotlin.math.log

class LoginFragment : Fragment(), Injectable, EasyPermissions.PermissionCallbacks {
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    private val RC_SMS_PERM = 122

    private val LOCATION_AND_CAMERA: Array<String> =
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA)
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory


    var binding by autoCleared<LoginFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)

    @Inject
    lateinit var loginViewModel: LoginViewModel

    private val registry = LifecycleRegistry(this)

    @Inject
    lateinit var tokenManager: TokenManager

    private lateinit var validator: Validator

    @Inject
    lateinit var accessTokenDao: AccessTokenDao

    var prefs : SharedPreferences? = null

    var dateFormat : String = "yyyy-MM-dd hh:mm"

    private lateinit var account: Auth0
    private var cachedCredentials: Credentials? = null
    private var cachedUserProfile: UserProfile? = null
    private var emailFromNtu : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        account = Auth0(
//            getString(R.string.com_auth0_client_id),
//            getString(R.string.com_auth0_domain)
//        )
//
//        try
//        {
//            login()
//        }
//        catch (e: Exception)
//        {
//            Log.d("LOGIN_FRAGMENT","AUTH0_LOGIN_FAILED" + e.toString())
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<LoginFragmentBinding>(
            inflater,
            R.layout.login_fragment,
            container,
            false
        )

        binding = dataBinding
        validator = Validator(binding)
        dataBinding.retryCallback = object : RetryCallback {
            override fun retry() {
                binding.userResource = null
                loginViewModel.onError()
            }
        }
        binding.textViewVesion.text = getApplicationVersionName()

        return dataBinding.root
    }

    private fun getApplicationVersionName(): String {

        try {
            val packageInfo = activity?.getPackageManager()?.getPackageInfo(activity?.getPackageName(), 0)
            return packageInfo?.versionName!!
        } catch (ignored: Exception) {
        }

        return ""
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
//        binding.linearLayout.visibility = View.INVISIBLE
//        binding.linearLayout2.visibility = View.INVISIBLE
        //binding.buttonLogin.visibility = View.INVISIBLE

        loginViewModel.accessTokenOffline?.observe(this, Observer { accessToken ->


            if (accessToken?.status == Status.SUCCESS) {
                binding.progressBar.visibility = View.GONE
                if (accessToken.data != null) {
                    if (accessToken.data.status) {
                        if (!isLoginClick) {
                            loadMainActivity()
                        }

                    } else {
//                        binding.linearLayout.visibility = View.VISIBLE
//                        binding.linearLayout2.visibility = View.VISIBLE
                        binding.buttonLogin.visibility = View.VISIBLE
                    }

                }
            }
            else if (accessToken?.status == Status.ERROR){
//                binding.linearLayout.visibility = View.VISIBLE
//                binding.linearLayout2.visibility = View.VISIBLE
                binding.buttonLogin.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        })
        if (tokenManager.getEmail() != null) {
            loginViewModel.setEmail(tokenManager.getEmail()!!)
        } else {
//            binding.linearLayout.visibility = View.VISIBLE
//            binding.linearLayout2.visibility = View.VISIBLE
            //binding.buttonLogin.visibility = View.VISIBLE
        }



        if (BuildConfig.DEBUG) {

//            binding.textInputEditTextEmail.setText("sgqa@yopmail.com")
//            binding.textInputEditTextPassword.setText("Qwerty123#")

//            binding.textInputEditTextEmail.setText("stagingbnqa@nghru.org")
//            binding.textInputEditTextPassword.setText("Asdfgh123#")

        }

        loginViewModel.accessTokenNew?.observe(this, Observer { accessToken ->

            //binding.progressBar.visibility = View.GONE
            binding.userResource = accessToken
            if (accessToken?.status == Status.SUCCESS ) {
                //println(user)
                if(accessToken.data!=null) {
                    val token = accessToken.data!!
                    //tokenManager.saveToken(token)
//                    tokenManager.saveEmail(binding.textInputEditTextEmail.text.toString())
                    tokenManager.saveEmail(emailFromNtu!!)
                    binding.textViewError.visibility = View.INVISIBLE
                    token.status = true
                    //accessTokenDao.login(token)

                        loginViewModel.setStationDevice("GET")

                }else
                {
                    Timber.d(getString(R.string.user_not_found))

                    binding.textViewError.visibility = View.VISIBLE
                    binding.textViewError.setText(getString(R.string.user_not_found))
//                    binding.linearLayout.visibility = View.VISIBLE
//                    binding.linearLayout2.visibility = View.VISIBLE
//                    binding.buttonLogin.visibility = View.VISIBLE
                    binding.userResource = null
                    loginViewModel.onError()
                }

            } else if (accessToken?.status == Status.ERROR) {
                Crashlytics.logException(Exception(accessToken.message?.message))
                Timber.d(accessToken.message?.message)

                binding.textViewError.visibility = View.VISIBLE
                binding.textViewError.setText(accessToken.message?.message)
//                binding.linearLayout.visibility = View.VISIBLE
//                binding.linearLayout2.visibility = View.VISIBLE
                binding.buttonLogin.visibility = View.VISIBLE
                binding.userResource = null
                loginViewModel.onError()
            }
        })

        loginViewModel.stationDevices?.observe(this, Observer {
            binding.progressBar.visibility = View.GONE

            if (it?.status == Status.SUCCESS)
            {
                loginViewModel.setStationDeviceList(it.data?.data!!)
            }
            else if(it?.status == Status.ERROR)
            {
                loadMainActivity()
            }

        })

        loginViewModel.stationDeviceList?.observe(this, Observer {
            binding.progressBar.visibility = View.GONE
            if (it?.status == Status.SUCCESS || it?.status == Status.ERROR)
            {
               loadMainActivity()
            }
        })
        binding.buttonLogin.singleClick {

//            if (validator.validate())
//            {
//                binding.progressBar.visibility = View.VISIBLE
//                val mPattern: Pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#\$%\\^&\\*])(?=.{8,})")
//
//                val matche: Matcher = mPattern.matcher(binding.textInputEditTextPassword.text.toString())
//                binding.root.hideKeyboard()
//
//                if(!matche.find())
//                {
//                    binding.textInputLayoutPassword.error = getString(R.string.passowrd_reg_error)
//                    //weightEditText.setText(); // Don't know what to place
//                }
//                else
//                {
//                    activity?.runOnUiThread(
//                        object : Runnable {
//                            override fun run() {
//                                binding.textViewError.text = ""
//                                binding.linearLayout.visibility = View.INVISIBLE
//                                binding.linearLayout2.visibility = View.INVISIBLE
//                                binding.buttonLogin.visibility = View.INVISIBLE
//                                isLoginClick = true
//
//                                    loginViewModel.setLogin(
//                                        binding.textInputEditTextEmail.text.toString(),
//                                        binding.textInputEditTextPassword.text.toString(),
//                                        isNetworkAvailable()
//                                    )
//
//                            }
//                        }
//                    )
//                }
//            }

            login()
        }

        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED && (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED)
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.CAMERA
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) && ActivityCompat.shouldShowRequestPermissionRationale(
                    activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    activity!!,
                    LOCATION_AND_CAMERA,
                    RC_SMS_PERM
                )
                ActivityCompat.requestPermissions(
                    activity!!,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )


                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        // loginViewModel.setDevices("devices")

        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )

        binding.buttonLogout.singleClick {

            logout()
        }

        binding.buttonAuthLogin.singleClick {

            //login()
        }
    }

    var isLoginClick: Boolean = false

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun getLocalTimeString(): String {
        val s = SimpleDateFormat(dateFormat, Locale.US)
        return s.format(Date())
    }
    fun  loadMainActivity()
    {
        prefs?.edit()?.putBoolean("isTimeOut", false)?.apply()
        prefs?.edit()?.putString("loginDateTime", getLocalTimeString())?.apply()
        prefs?.edit()?.putString("dateTime", getLocalTimeString())?.apply()

        val intent = Intent(activity, MainActivity::class.java)
        startActivity(intent)
        activity!!.finish()
    }

    private fun login() {

        binding.progressBar.visibility = View.VISIBLE
        binding.buttonLogin.visibility = View.GONE

        try
        {
            WebAuthProvider
                .login(account)
                .withScheme(getString(R.string.login_scheme))
                .withScope(getString(R.string.com_login_scope))
                .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")
                //.withResponseType(getString(R.string.response_type))
//            .withAudience(getString(R.string.login_audience, getString(R.string.com_auth0_domain)))
                //.withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")
                //.withAudience(getString(R.string.login_audience_new1))
                .start(activity!!, object : Callback<Auth0Exception>,
                    AuthCallback {

                    override fun onFailure(exception: AuthenticationException) {
                        //showSnackBar(getString(R.string.login_failure_message, exception.getCode()))
                        Log.d("LOGIN_FRAGMENT", "ERROR_ON_FAILURE: " + exception.toString())
                    }

                    override fun onSuccess(credentials: Credentials) {
                        cachedCredentials = credentials
                        Log.d("LOGIN_FRAGMENT", "SUCCESS: " + cachedCredentials)
                        checkUserProfile()
                    }

                    override fun onFailure(dialog: Dialog) {
                        Log.d("LOGIN_FRAGMENT", "ERROR_ON_FAILURE: " + dialog.show())
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onFailure(error: Auth0Exception) {
                        Log.d("LOGIN_FRAGMENT", "ERROR_ON_FAILURE: " + error.toString())
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
        }
        catch(e: Exception)
        {
            Log.d("LOGIN_FRAGMENT", "EXCEPTION:" + e.toString())
        }

    }

    private fun logout() {
        try
        {
            WebAuthProvider
                .logout(account)
                .withScheme(getString(R.string.login_scheme))
                .start(activity!!, object : Callback<Auth0Exception>,
                    AuthCallback, VoidCallback {

                    override fun onFailure(exception: AuthenticationException) {
                        //showSnackBar(getString(R.string.login_failure_message, exception.getCode()))
                        Log.d("LOGOUT_FRAGMENT", "ERROR_ON_FAILURE: " + exception.toString())
                    }

                    override fun onSuccess(credentials: Credentials) {
                        cachedCredentials = credentials
                        //Toast.makeText(activity!!, "Login success with token: " + credentials.accessToken, Toast.LENGTH_LONG).show()
                        Log.d("LOGOUT_FRAGMENT", "SUCCESS: " + credentials.accessToken)
                    }

                    override fun onFailure(dialog: Dialog) {
                        Log.d("LOGOUT_FRAGMENT", "ERROR_ON_FAILURE: " + dialog.show())
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onSuccess(payload: Void?) {
                        Log.d("LOGOUT_FRAGMENT", "SUCCESS: " + payload.toString())
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onFailure(error: Auth0Exception) {
                        Log.d("LOGOUT_FRAGMENT", "ERROR_ON_FAILURE: " + error.toString())
                        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }
                })
        }
        catch(e: Exception)
        {
            Log.d("LOGIN_FRAGMENT", "EXCEPTION:" + e.toString())
        }
    }

    private fun checkUserProfile() {
        // Guard against showing the profile when no user is logged in
        if (cachedCredentials == null) {
            return
        }

        val client = AuthenticationAPIClient(account)
        client
            .userInfo(cachedCredentials!!.accessToken!!)
            .start(object : BaseCallback<UserProfile, AuthenticationException> {

                override fun onFailure(exception: AuthenticationException) {

                    Log.d("LOGOUT_FRAGMENT", "ERROR_ON_FAILURE_PROFILE: " + exception.toString())
                }

                override fun onSuccess(payload: UserProfile?)
                {
                    activity?.runOnUiThread(
                        object : Runnable {
                            override fun run() {
                                binding.textViewError.text = ""
//                                binding.linearLayout.visibility = View.INVISIBLE
//                                binding.linearLayout2.visibility = View.INVISIBLE
                                //binding.buttonLogin.visibility = View.INVISIBLE
                                isLoginClick = true
                                //binding.progressBar.visibility = View.VISIBLE

                                loginViewModel.setLoginNew(
                                    payload!!.email,
                                    isNetworkAvailable()
                                )

                                emailFromNtu = payload!!.email

                            }
                        }
                    )
                    Log.d("LOGOUT_FRAGMENT", "SUCCESS_PROFILE: " + payload!!.email)
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
    }
}
