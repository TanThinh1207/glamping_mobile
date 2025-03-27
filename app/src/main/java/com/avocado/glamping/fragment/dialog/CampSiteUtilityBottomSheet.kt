package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.adapter.ButtonFacilityAdapter
import com.avocado.glamping.adapter.ButtonUtilityAdapter
import com.avocado.glamping.adapter.UtilityAdapter
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.data.model.resp.CampsiteResponse
import com.avocado.glamping.data.model.resp.FacilityResponse
import com.avocado.glamping.data.model.resp.UtilityResponse
import com.avocado.glamping.viewmodel.CampSiteViewModel
import com.avocado.glamping.viewmodel.CampTypeViewModel
import com.avocado.glamping.viewmodel.UpdateCampTypeFacilitiesState
import com.avocado.glamping.viewmodel.UpdateUtilities
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CampSiteUtilityBottomSheet(
    private val onUtilitiesSelected : (List<UtilityResponse>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var adapter: ButtonUtilityAdapter
    private var selectedList = mutableListOf<UtilityResponse>()

    private val campSiteViewModel : CampSiteViewModel by viewModels()




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_utility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView : RecyclerView = view.findViewById(R.id.utilityRecyclerView)
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW // Items flow in rows
            flexWrap = FlexWrap.WRAP // Wrap items into multiple lines
            justifyContent = JustifyContent.FLEX_START // Align items to the start
        }

        val campSite = arguments?.getParcelable<CampsiteResponse>("campSite")

        val utilities : List<UtilityResponse> = arguments?.getParcelableArrayList("utilities") ?: emptyList()
        selectedList = arguments?.getParcelableArrayList<UtilityResponse>("selectedUtilities")?.toMutableList() ?: mutableListOf()


        recyclerView.layoutManager = flexboxLayoutManager
        adapter = ButtonUtilityAdapter(utilities, selectedList) {utility ->
            toggleUtilitySelection(utility)
        }
        recyclerView.adapter = adapter

        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            if (campSite != null) updateUtilities(campSite.id, selectedList.map { it.id })
            observeUpdateUtilities(view)
        }


    }

    private fun toggleUtilitySelection(utility : UtilityResponse) {

        if (selectedList.contains(utility)) {
            selectedList.remove(utility)
        } else {
            selectedList.add(utility)
        }
        adapter.updateSelection(selectedList)
        onUtilitiesSelected(selectedList)
    }

    private fun updateUtilities(campSiteId : Int, utilities : List<Int>){
        campSiteViewModel.updateUtilities(campSiteId, utilities)
    }

    private fun observeUpdateUtilities(view : View){
        campSiteViewModel.updateUtilities.observe(viewLifecycleOwner) { state ->
            when(state) {
                is UpdateUtilities.Loading -> {
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                }
                is UpdateUtilities.Success -> {
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    parentFragmentManager.setFragmentResult(
                        "updateCampSite",
                        Bundle().apply { putParcelable("updatedCampSite", state.response) }
                    )
                    dismiss()
                }
                is  UpdateUtilities.Error -> {
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    companion object {
        fun newInstance(
            campSite : CampsiteResponse,
            utilities: List<UtilityResponse>,
            selectedUtilities: List<UtilityResponse>,
            onUtilitiesSelected: (List<UtilityResponse>) -> Unit
        ): CampSiteUtilityBottomSheet {
            val fragment = CampSiteUtilityBottomSheet(onUtilitiesSelected)
            val args = Bundle().apply {
                putParcelableArrayList("selectedUtilities", ArrayList(selectedUtilities))
                putParcelableArrayList("utilities", ArrayList(utilities))
                putParcelable("campSite", campSite)
            }
            fragment.arguments = args
            return fragment
        }
    }
}

