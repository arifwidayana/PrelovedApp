package com.preloved.app.ui.fragment.homepage.account

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.preloved.app.BuildConfig
import com.preloved.app.R
import com.preloved.app.base.arch.BaseFragment
import com.preloved.app.base.model.Resource
import com.preloved.app.data.local.datastore.DatastoreManager
import com.preloved.app.databinding.FragmentAccountBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : BaseFragment<FragmentAccountBinding, AccountViewModel>(
    FragmentAccountBinding::inflate
), AccountContract.View {
    override val viewModel: AccountViewModel by viewModel()
    private val bundle = Bundle()

    companion object {
        const val USER_TOKEN = "user_token"
    }

    override fun showLoading(isVisible: Boolean) {
        super.showLoading(isVisible)
        getViewBinding().pbLoading.isVisible = isVisible
    }

    override fun showContent(isVisible: Boolean) {
        super.showContent(isVisible)
        getViewBinding().nsv.isVisible = isVisible
    }

    override fun initView() {
        viewModel.userSession()
        getViewBinding().apply {
            tvVersionApp.text = "Version ${BuildConfig.VERSION_NAME}"
            tvMyOrder.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_myOrderFragment)
            }
            tvChangePassword.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_editPasswordFragment)
            }
            tvChangeProfile.setOnClickListener {
                findNavController().navigate(
                    R.id.action_accountFragment_to_editProfileFragment,
                    bundle
                )
            }
            tvWishlist.setOnClickListener {
                findNavController().navigate(R.id.action_accountFragment_to_wishlistFragment)
            }
            tvExit.setOnClickListener {
                AlertDialog
                    .Builder(requireContext())
                    .setTitle(getString(R.string.message_exit))
                    .setPositiveButton(getString(R.string.sure)) { dialogPositive, _ ->
                        viewModel.deleteToken()
                        Toast
                            .makeText(
                                requireContext(),
                                "Logout Success",
                                Toast.LENGTH_SHORT
                            )
                            .show()
                        findNavController().navigate(R.id.homeFragment)
                        dialogPositive.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialogNegative, _ ->
                        dialogNegative.dismiss()
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }

    override fun observeData() {
        viewModel.userSessionResult().observe(viewLifecycleOwner) {
            if (it.access_token == DatastoreManager.DEFAULT_ACCESS_TOKEN) {
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.please_login))
                    .setPositiveButton(getString(R.string.login)) { dialogP, _ ->
                        //ToLogin Fragment
                        dialogP.dismiss()
                        val passData =
                            AccountFragmentDirections.actionAccountFragmentToLoginFragment3(
                                status = 1
                            )
                        findNavController().navigate(passData)

                    }
                    .setNegativeButton(getString(R.string.later)) { dialogN, _ ->
                        //ToHomeFragment
                        dialogN.dismiss()
                        findNavController().popBackStack()

                    }
                    .setCancelable(false)
                    .show()
                //viewModel.checkLogin().removeObserver(viewLifecycleOwner)
            } else {
                bundle.putString(USER_TOKEN, it.access_token)
                viewModel.getUserData(it.access_token)
            }
        }
        viewModel.getUserDataResult().observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    showLoading(true)
                    showContent(false)
                }
                is Resource.Success -> {
                    showLoading(false)
                    showContent(true)
                    if (it.data != null) {
                        if (it.data.imageUrl != null) {
                            Glide.with(requireContext())
                                .load(it.data.imageUrl.toString())
                                .placeholder(R.drawable.ic_profile)
                                .circleCrop()
                                .into(getViewBinding().ivProfile)
                        }
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    showContent(true)
                    if (it.message!!.contains("403")) {
                        AlertDialog.Builder(context)
                            .setTitle(getString(R.string.warning))
                            .setMessage(getString(R.string.your_session))
                            .setPositiveButton(getString(R.string.login)) { dialogP, _ ->
                                dialogP.dismiss()
                                findNavController().navigate(R.id.action_accountFragment_to_loginFragment3)
                            }
                            .setNegativeButton(getString(R.string.later)) { negativeButton, _ ->
                                negativeButton.dismiss()
                                findNavController().popBackStack()
                            }
                            .setCancelable(false)
                            .show()
                    }
                }
            }
        }
    }
}