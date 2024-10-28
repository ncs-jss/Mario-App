package com.ncs.mario.UI.MainScreen.Internship

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.support.v4.os.IResultReceiver._Parcel
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import com.ncs.mario.Domain.Utility.ExtensionsUtil.gone
import com.ncs.mario.R
import com.ncs.mario.UI.MainScreen.MainActivity
import com.ncs.mario.databinding.FragmentInternshipBinding

class InternshipFragment : Fragment() {

    companion object {
        fun newInstance() = InternshipFragment()
    }
    private var _binding: FragmentInternshipBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InternshipViewModel by viewModels()
    private val activityBinding: MainActivity by lazy {
        (requireActivity() as MainActivity)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInternshipBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        activityBinding.binding.actionbar.score.visibility=View.VISIBLE
        activityBinding.binding.actionbar.btnHam.setImageResource(R.drawable.ham)
        activityBinding.binding.actionbar.titleTv.text="Internships"
        activityBinding.binding.actionbar.btnHam.setOnClickListener {
            if (activityBinding.binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                activityBinding.binding.drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                activityBinding.binding.drawerLayout.openDrawer(GravityCompat.START)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews(){

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}