package com.ncs.marioapp.UI.AdminScreen

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.ncs.marioapp.Domain.HelperClasses.Utils
import com.ncs.marioapp.Domain.Models.Admin.FormItem
import com.ncs.marioapp.Domain.Models.Admin.FormType
import com.ncs.marioapp.Domain.Models.Admin.QuestionItem
import com.ncs.marioapp.Domain.Models.Admin.RoundQuestionnaire
import com.ncs.marioapp.Domain.Models.ServerResult
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.gone
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.setOnClickThrottleBounceListener
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.toast
import com.ncs.marioapp.Domain.Utility.ExtensionsUtil.visible
import com.ncs.marioapp.R
import com.ncs.marioapp.UI.AdminScreen.AdminMainActivity
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.DragSwipeCallback
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.FormValidationUseCase
import com.ncs.marioapp.UI.AdminScreen.RecordCreation.QuestionnaireAdapter
import com.ncs.marioapp.databinding.CreateQuestionDialogBinding
import com.ncs.marioapp.databinding.FragmentCreateBonusLinkBinding
import com.ncs.marioapp.databinding.FragmentQuestionnaireCreationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateBonusLinkFragment : Fragment() {

    companion object {
        fun newInstance() = CreateBonusLinkFragment()
    }

    private val binding: FragmentCreateBonusLinkBinding by lazy {
        FragmentCreateBonusLinkBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.btnCreateBonusLink.setOnClickThrottleBounceListener {
            val coupon = binding.couponEt.text.toString().trim()

            if(coupon.isNullOrEmpty()){
                toast("Enter Some Coupon Code First")
            }
            else{
                binding.progressBar.visible()
                val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://ncsmario.page.link/coupon/${coupon}"))
                    .setDomainUriPrefix("https://ncsmario.page.link")
                    .setAndroidParameters(
                        DynamicLink.AndroidParameters.Builder("com.ncs.marioapp")
                            .setMinimumVersion(1)
                            .build()
                    )
                    .buildDynamicLink()
                FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLongLink(dynamicLink.uri)
                    .buildShortDynamicLink()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val shortLink = task.result?.shortLink
                            binding.progressBar.gone()
                            binding.link.visible()
                            binding.link.text = shortLink.toString()
                        } else {
                            binding.progressBar.gone()
                            toast("Please try again")
                        }
                    }
            }
        }

        binding.link.setOnClickThrottleBounceListener {
            val link = binding.link.text.toString()

            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Link", link)

            clipboard.setPrimaryClip(clip)

            toast("Copied the link to clipboard")
        }

    }


}