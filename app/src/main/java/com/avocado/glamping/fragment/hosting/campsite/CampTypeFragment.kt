package com.avocado.glamping.fragment.hosting.campsite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.adapter.FacilityAdapter
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.data.model.resp.FacilityResponse
import com.avocado.glamping.di.PriceFormat
import com.avocado.glamping.fragment.dialog.CampTypeCapacityBottomSheet
import com.avocado.glamping.fragment.dialog.CampTypeFacilityBottomSheet

import com.avocado.glamping.fragment.dialog.CampTypePriceBottomSheetDialog
import com.avocado.glamping.viewmodel.FacilityViewModel
import com.avocado.glamping.viewmodel.GetFacilityState
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampTypeFragment : Fragment(R.layout.fragment_edit_camp_type) {

    private val args : CampTypeFragmentArgs by navArgs()
    private val facilityViewModel : FacilityViewModel by viewModels()
    private lateinit var campType : CampTypeResponse

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_camp_type, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        campType = args.campType

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            findNavController().popBackStack()
        }
        view.findViewById<ShapeableImageView>(R.id.img_camp_type).load(campType.image){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }
        view.findViewById<TextView>(R.id.tv_daily_price).text = PriceFormat.formatPrice(
            campType.price, BuildConfig.LANGUAGE, BuildConfig.COUNTRY
        )
        view.findViewById<TextView>(R.id.tv_weekend_price).text = PriceFormat.formatPrice(
            campType.weekendRate, BuildConfig.LANGUAGE, BuildConfig.COUNTRY
        )
        view.findViewById<TextView>(R.id.tv_capacity).text = requireContext().getString(R.string.capacity_format, campType.capacity)
        view.findViewById<LinearLayout>(R.id.layout_daily_price).setOnClickListener{
            showEditPriceDialog(campType, "Daily Price")
        }
        view.findViewById<LinearLayout>(R.id.layout_weekend_price).setOnClickListener{
            showEditPriceDialog(campType, "Weekend Price")
        }
        view.findViewById<LinearLayout>(R.id.layout_capacity).setOnClickListener{
            showCapacityDialog(campType.capacity)
        }
        view.findViewById<LinearLayout>(R.id.layout_facility).setOnClickListener{
            fetFacilities()
        }

        handleUpdate(view)

        facilityViewModel.getFacilities.observe(viewLifecycleOwner){state ->
            when(state){
                is GetFacilityState.Loading ->{

                }
                is GetFacilityState.Success ->{
                    showFacilityDialog(state.response.content, campType.facilities, campType)
                }
                is GetFacilityState.Error ->{
                    Toast.makeText(requireContext(), "here", Toast.LENGTH_SHORT).show()
                }
            }
        }
        view.findViewById<SwitchMaterial>(R.id.switch_status).isChecked = campType.status

        val recyclerView : RecyclerView = view.findViewById(R.id.facilityRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = FacilityAdapter(campType.facilities)

    }

    private fun handleUpdate(view : View){
        parentFragmentManager.setFragmentResultListener("updateCampType", viewLifecycleOwner) {_, bundle ->
            val updatedCampType = bundle.getParcelable<CampTypeResponse>("updatedCampType")
            updatedCampType?.let { newCampType ->
                campType = newCampType
                view.findViewById<TextView>(R.id.tv_daily_price).text = PriceFormat.formatPrice(
                    campType.price, BuildConfig.LANGUAGE, BuildConfig.COUNTRY
                )
                view.findViewById<TextView>(R.id.tv_weekend_price).text = PriceFormat.formatPrice(
                    campType.weekendRate, BuildConfig.LANGUAGE, BuildConfig.COUNTRY
                )
                view.findViewById<TextView>(R.id.tv_capacity).text = requireContext().getString(R.string.capacity_format, campType.capacity)
                val recyclerView : RecyclerView = view.findViewById(R.id.facilityRecyclerView)
                recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                recyclerView.adapter = FacilityAdapter(campType.facilities)
            }
        }
    }

    private fun showEditPriceDialog(campType : CampTypeResponse, type : String){
        val bottomSheet = CampTypePriceBottomSheetDialog.newInstance(campType, type)
        bottomSheet.show(parentFragmentManager, "CampTypePrice")
    }

    private fun showCapacityDialog(capacity : Int){
        val bottomSheet = CampTypeCapacityBottomSheet.newInstance(campType,capacity)
        bottomSheet.show(parentFragmentManager, "CampTypeCapacity")
    }

    private fun showFacilityDialog(facilities : List<FacilityResponse>, selectedFacilities : List<FacilityResponse>, campType: CampTypeResponse){
        val bottomSheet = CampTypeFacilityBottomSheet.newInstance(campType,facilities, selectedFacilities){}
        bottomSheet.show(parentFragmentManager, "CampTypeFacility")
    }

    private fun fetFacilities(){
        facilityViewModel.getFacilities(
            mapOf(
                "status" to "true"
            ),
            page = 0,
            size = 10,
            "",
            "id",
            "ASC"
        )
    }
}