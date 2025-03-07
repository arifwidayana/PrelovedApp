package com.preloved.app.ui.fragment.homepage.buyer.info

import android.app.ActionBar
import android.app.AlertDialog
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.preloved.app.R
import com.preloved.app.base.arch.BaseFragment
import com.preloved.app.base.model.Resource
import com.preloved.app.data.local.datastore.DatastoreManager
import com.preloved.app.data.network.model.response.RequestApproveOrder
import com.preloved.app.data.network.model.response.SellerOrderResponse
import com.preloved.app.databinding.FragmentBuyerInfoBinding
import com.preloved.app.ui.currency
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.properties.Delegates

class BuyerInfoFragment : BaseFragment<FragmentBuyerInfoBinding, BuyerInfoViewModel>(
    FragmentBuyerInfoBinding::inflate
), BuyerInfoContract.View {
    override val viewModel: BuyerInfoViewModel by viewModel()
    private var token = ""
    private lateinit var status: String
    private lateinit var namaPenawar: String
    private lateinit var kotaPenawar: String
    private lateinit var imagePenawar: String
    private lateinit var productName: String
    private lateinit var productPrice: String
    private lateinit var productBid: String
    private lateinit var imageProduct: String
    private var idProduct by Delegates.notNull<Int>()
    private var idOrder by Delegates.notNull<Int>()

    private val args by navArgs<BuyerInfoFragmentArgs>()

    override fun initView() {
        viewModel.userSession()
        setOnClickListeners()
    }

    override fun setOnClickListeners() {
        getViewBinding().apply {
            btnBack.setOnClickListener {
                findNavController().popBackStack()
            }
            btnTerima.setOnClickListener {
                val idOrder = args.orderId
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.accept_offer))
                    .setPositiveButton(getString(R.string.accept)) { positive, _ ->
                        status = "accepted"
                        val body = RequestApproveOrder(
                            status
                        )
                        viewModel.statusOrder(token, idOrder, body)
                        positive.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { negative, _ ->
                        negative.dismiss()
                    }
                    .show()
            }
            btnTolak.setOnClickListener {
                val idOrder = args.orderId
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.decline_the_offer))
                    .setPositiveButton(getString(R.string.sure)) { positive, _ ->
                        status = "declined"
                        val body = RequestApproveOrder(
                            status
                        )
                        viewModel.statusOrder(token, idOrder, body)
                        positive.dismiss()
                    }
                    .setNegativeButton(getString(R.string.cancel)) { negative, _ ->
                        negative.dismiss()
                    }
                    .show()
            }
            btnHubungi.setOnClickListener {
                val bottomFragment = BottomSheetBuyerInfoFragment(
                    namaPenawar,
                    kotaPenawar,
                    imagePenawar,
                    productName,
                    productPrice,
                    productBid,
                    imageProduct
                )
                bottomFragment.show(parentFragmentManager, "Tag")
            }
            btnStatus.setOnClickListener {
                Log.d("HAYO3", idProduct.toString())
                val bottomFragmentStatus = BottomSheetBuyerInfoStatusFragment(
                    idOrder = idOrder
                )
                bottomFragmentStatus.show(parentFragmentManager, "Tag")
            }
        }
    }

    override fun showLoading(isVisible: Boolean) {
        super.showLoading(isVisible)
        getViewBinding().pbLoading.isVisible = isVisible
    }

    override fun showContent(isVisible: Boolean) {
        super.showContent(isVisible)
        getViewBinding().groupContent.isVisible = isVisible
    }

    override fun setDataToView(data: SellerOrderResponse) {
        getViewBinding().apply {
            tvNamaPenawar.text = data.user.fullName
            tvKotaPenawar.text = data.user.city
            Glide.with(requireContext())
            Glide.with(requireContext())
                .load(data.user.imageUrl)
                .placeholder(R.drawable.image_profile)
                .into(getViewBinding().ivAvatarPenawar)
            tvNamaProduk.text = data.product.name
            Glide.with(requireContext())
                .load(data.product.imageUrl)
                .placeholder(R.drawable.image_profile)
                .into(getViewBinding().ivProductImage)
            tvHargaAwalProduk.text = currency(data.product.basePrice)
            tvHargaDitawarProduk.text = currency(data.price)
            namaPenawar = data.user.fullName
            imagePenawar = data.user.imageUrl
            productName = data.productName
            productPrice = data.basePrice
            productBid = data.price.toString()
            kotaPenawar = data.user.city
            imageProduct = data.product.imageUrl
            idProduct = data.productId
            idOrder = data.id

            if (data.status == "accepted") {
                btnGroup.visibility = View.GONE
                btnGroupAccepted.visibility = View.VISIBLE
                val bottomFragment = BottomSheetBuyerInfoFragment(
                    namaPenawar,
                    kotaPenawar,
                    imagePenawar,
                    productName,
                    productPrice,
                    productBid,
                    imageProduct
                )
                bottomFragment.show(parentFragmentManager, "Tag")

            }
        }
    }

    override fun observeData() {
        super.observeData()
        viewModel.userSessionResult().observe(viewLifecycleOwner) {
            if (it.access_token == DatastoreManager.DEFAULT_ACCESS_TOKEN) {
                AlertDialog.Builder(context)
                    .setTitle(getString(R.string.warning))
                    .setMessage(getString(R.string.please_login))
                    .setPositiveButton(R.string.login) { dialogP, _ ->
                        //ToLogin Fragment
                        dialogP.dismiss()
                        findNavController().navigate(R.id.action_accountFragment_to_loginFragment3)

                    }
                    .setNegativeButton(getString(R.string.later)) { dialogN, _ ->
                        //ToHomeFragment
                        dialogN.dismiss()
                        findNavController().navigate(R.id.homeFragment)

                    }
                    .setCancelable(false)
                    .show()
            } else {
                token = it.access_token
                val idOrderBuyer = args.orderId
                viewModel.getSellerOrderById(token, idOrderBuyer)
            }
        }
        viewModel.getSellerOrderByIdResult().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(true)
                    showContent(false)
                }
                is Resource.Success -> {
                    showLoading(false)
                    showContent(true)
                    response.data?.let { setDataToView(it) }
                }
                is Resource.Error -> {
                    showLoading(true)
                    showContent(false)
                }
            }
        }
        viewModel.statusOrderResult().observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    if (status == "accepted") {
                        showToastAccept()
                        getViewBinding().apply {
                            btnGroup.visibility = View.GONE
                            btnGroupAccepted.visibility = View.VISIBLE
                            val bottomFragment = BottomSheetBuyerInfoFragment(
                                namaPenawar,
                                kotaPenawar,
                                imagePenawar,
                                productName,
                                productPrice,
                                productBid,
                                imageProduct
                            )
                            bottomFragment.show(parentFragmentManager, "Tag")
                        }
                    } else {
                        showToastDecline()
                        findNavController().navigate(R.id.action_buyerInfoFragment_to_saleFragment)
                    }
                }
                is Resource.Error -> {
                    showLoading(true)
                }
            }
        }
    }

    private fun showToastAccept() {
        val snackBarView =
            Snackbar.make(
                getViewBinding().root,
                getString(R.string.congratulation_sell),
                Snackbar.LENGTH_LONG
            )
        val layoutParams = ActionBar.LayoutParams(snackBarView.view.layoutParams)
        snackBarView.setAction(" ") {
            snackBarView.dismiss()
        }
        val textView =
            snackBarView.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0)
        textView.compoundDrawablePadding = 16
        layoutParams.gravity = Gravity.TOP
        layoutParams.setMargins(32, 150, 32, 0)
        snackBarView.view.setPadding(24, 16, 0, 16)
        snackBarView.view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary
            )
        )
        snackBarView.view.layoutParams = layoutParams
        snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackBarView.show()
    }

    private fun showToastDecline() {
        val snackBarView =
            Snackbar.make(
                getViewBinding().root,
                getString(R.string.offer_decline),
                Snackbar.LENGTH_LONG
            )
        val layoutParams = ActionBar.LayoutParams(snackBarView.view.layoutParams)
        snackBarView.setAction(" ") {
            snackBarView.dismiss()
        }
        val textView =
            snackBarView.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_close, 0)
        textView.compoundDrawablePadding = 16
        layoutParams.gravity = Gravity.TOP
        layoutParams.setMargins(32, 150, 32, 0)
        snackBarView.view.setPadding(24, 16, 0, 16)
        snackBarView.view.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary
            )
        )
        snackBarView.view.layoutParams = layoutParams
        snackBarView.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackBarView.show()
    }
}
