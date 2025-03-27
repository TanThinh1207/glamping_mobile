package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.adapter.ButtonFacilityAdapter
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.data.model.resp.FacilityResponse
import com.avocado.glamping.viewmodel.CampTypeViewModel
import com.avocado.glamping.viewmodel.UpdateCampTypeFacilitiesState
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CampTypeFacilityBottomSheet(
    private val onFacilitiesSelected: (List<FacilityResponse>) -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var adapter: ButtonFacilityAdapter
    private var selectedList = mutableListOf<FacilityResponse>()

    private val campTypeViewModel : CampTypeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_facility, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.facilityRecyclerView)

        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            flexDirection = FlexDirection.ROW // Items flow in rows
            flexWrap = FlexWrap.WRAP // Wrap items into multiple lines
            justifyContent = JustifyContent.FLEX_START // Align items to the start
        }

        val campType = arguments?.getParcelable<CampTypeResponse>("campType")

        // Retrieve arguments safely
        val facilityList: List<FacilityResponse> = arguments?.getParcelableArrayList("facilities") ?: emptyList()
        selectedList = arguments?.getParcelableArrayList<FacilityResponse>("selectedFacilities")?.toMutableList() ?: mutableListOf()

        // Set up RecyclerView
        recyclerView.layoutManager = flexboxLayoutManager
        adapter = ButtonFacilityAdapter(facilityList, selectedList) { facility ->
            toggleFacilitySelection(facility)
        }
        recyclerView.adapter = adapter

        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            val ids = selectedList.map { it.id }
            if (campType != null){
                updateFacilities(campType.id, ids)
                observeUpdateFacilities(view)
            }
        }
    }

    private fun toggleFacilitySelection(facility: FacilityResponse) {
        if (selectedList.contains(facility)) {
            selectedList.remove(facility)
        } else {
            selectedList.add(facility)
        }
        adapter.updateSelection(selectedList)
        onFacilitiesSelected(selectedList)
    }

    private fun observeUpdateFacilities(view : View){
        campTypeViewModel.updateCampTypeFacilitiesState.observe(viewLifecycleOwner){ state ->
            when(state){
                is UpdateCampTypeFacilitiesState.Loading ->{
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                }
                is UpdateCampTypeFacilitiesState.Success ->{
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    parentFragmentManager.setFragmentResult(
                        "updateCampType",
                        Bundle().apply { putParcelable("updatedCampType", state.response) }
                    )
                    dismiss()
                }
                is UpdateCampTypeFacilitiesState.Error -> {
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateFacilities(campTypeId : Int, ids : List<Int>){
        campTypeViewModel.updateCampTypeFacilities(campTypeId, ids)
    }

    companion object {
        fun newInstance(
            campType : CampTypeResponse,
            facilities: List<FacilityResponse>,
            selectedFacilities: List<FacilityResponse>,
            onFacilitiesSelected: (List<FacilityResponse>) -> Unit
        ): CampTypeFacilityBottomSheet {
            val fragment = CampTypeFacilityBottomSheet(onFacilitiesSelected)
            val args = Bundle().apply {
                putParcelableArrayList("selectedFacilities", ArrayList(selectedFacilities))
                putParcelableArrayList("facilities", ArrayList(facilities))
                putParcelable("campType", campType)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
