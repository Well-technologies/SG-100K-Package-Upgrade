package org.singapore.ghru.ui.logout

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.auth0.android.Auth0
import com.auth0.android.Auth0Exception
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.provider.WebAuthProvider
import org.singapore.ghru.LoginActivity
import org.singapore.ghru.R
import org.singapore.ghru.binding.FragmentDataBindingComponent
import org.singapore.ghru.databinding.LogoutDialogFragmentBinding
import org.singapore.ghru.db.AccessTokenDao
import org.singapore.ghru.di.Injectable
import org.singapore.ghru.util.TokenManager
import org.singapore.ghru.util.autoCleared
import org.singapore.ghru.util.singleClick
import org.singapore.ghru.vo.Status
import javax.inject.Inject
import com.auth0.android.result.Credentials
import com.auth0.android.callback.Callback
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.VoidCallback
import org.singapore.ghru.MainActivity


class LogoutDialogFragment : DialogFragment(), Injectable {

    val TAG = LogoutDialogFragment::class.java.getSimpleName()

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    var binding by autoCleared<LogoutDialogFragmentBinding>()
    var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    @Inject
    lateinit var logoutdialogViewModel: LogoutDialogViewModel

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var accessTokenDao: AccessTokenDao

    private lateinit var account: Auth0
    private var cachedCredentials: Credentials? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val dataBinding = DataBindingUtil.inflate<LogoutDialogFragmentBinding>(
            inflater,
            R.layout.logout_dialog_fragment,
            container,
            false
        )
        binding = dataBinding
        return dataBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        binding.buttonAddMember.singleClick {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            dismiss()
            activity?.finish()
        }

        logoutdialogViewModel.accessToken?.observe(this, Observer { accessToken ->


            if (accessToken?.status == Status.SUCCESS) {
                //println(user)
                val accessToken1 = accessToken.data!!
                accessToken1.status = false

//                async {
//                    accessTokenDao.logout(accessToken1)
//
//                }

                logoutdialogViewModel.setLogOut(accessToken1)
            } else if (accessToken?.status == Status.ERROR) {
                //Crashlytics.logException(Exception(accessToken.message?.message))

            }
        })



        logoutdialogViewModel.accessTokenLogout?.observe(this, Observer { accessToken ->


            if (accessToken?.status == Status.SUCCESS)
            {
                logout()
                //logout()
//                val intent = Intent(activity, LoginActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivity(intent)
//                dismiss()
//                activity?.finish()
            } else if (accessToken?.status == Status.ERROR) {
                //Crashlytics.logException(Exception(accessToken.message?.message))
            }
        })

        binding.buttonAcceptAndContinue.singleClick {

            logoutdialogViewModel.setEmail(tokenManager.getEmail()!!)
//            var detete = async {
//                accessTokenDao.logout()
//            }

            tokenManager.deleteToken()
            //logout()


        }

        account = Auth0(
            getString(R.string.com_auth0_client_id),
            getString(R.string.com_auth0_domain)
        )
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                return navController().navigateUp()

//                val intent = Intent(activity, LoginActivity::class.java)
//                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // the content
        val root = RelativeLayout(activity)
        root.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // creating the fullscreen dialog
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(root)
        if (dialog.window != null) {
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    /**
     * Created to be able to override in tests
     */
    fun navController() = findNavController()

    fun show(fragmentManager: FragmentManager) {
        super.show(fragmentManager, TAG)
    }

    private fun logout() {

        try
        {
            WebAuthProvider
                .logout(account)
                .withScheme(getString(R.string.login_scheme))
//                .withReturnToUrl("demo://${getString(R.string.com_auth0_domain)}/v2/logout?federated")
                .withReturnToUrl("https://loginfs.ntu.edu.sg/adfs/ls/?wa=wsignout1.0")
                .start(activity!!, object : Callback<Auth0Exception>,
                    AuthCallback, VoidCallback {

                    override fun onFailure(exception: AuthenticationException) {
                        Log.d("LOGOUT_FRAGMENT", "ERROR_ON_FAILURE: " + exception.toString())
                    }

                    override fun onSuccess(credentials: Credentials) {
                        cachedCredentials = credentials
                    }

                    override fun onFailure(dialog: Dialog) {
                        Log.d("LOGOUT_FRAGMENT", "ERROR_ON_FAILURE: " + dialog.show())
                    }

                    override fun onSuccess(payload: Void?) {
                        Log.d("LOGOUT_FRAGMENT", "SUCCESS: " + payload.toString())

//                        val intent = Intent(activity, LoginActivity::class.java)
//                        startActivity(intent)
                    }

                    override fun onFailure(error: Auth0Exception) {
                        Log.d("LOGOUT_FRAGMENT", "ERROR_ON_FAILURE: " + error.toString())
                    }
                })

            activity?.finishAffinity()
//            val intent = Intent(activity, LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(intent)
        }
        catch(e: Exception)
        {
            Log.d("LOGIN_FRAGMENT", "EXCEPTION:" + e.toString())
        }
    }
}
