package com.avocado.glamping.fragment.hosting.campsite

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avocado.glamping.R
import com.avocado.glamping.adapter.CampTypeAdapter
import com.avocado.glamping.adapter.RatingAdapter
import com.avocado.glamping.adapter.ServiceAdapter
import com.avocado.glamping.adapter.UtilityAdapter
import com.avocado.glamping.data.model.req.CampSiteUpdateRequest
import com.avocado.glamping.data.model.resp.CampsiteResponse
import com.avocado.glamping.data.model.resp.FacilityResponse
import com.avocado.glamping.data.model.resp.UtilityResponse
import com.avocado.glamping.fragment.dialog.CampSiteDescriptionBottomSheetDialog
import com.avocado.glamping.fragment.dialog.CampSiteNameBottomSheetDialog
import com.avocado.glamping.fragment.dialog.CampSiteUtilityBottomSheet
import com.avocado.glamping.viewmodel.CampSiteViewModel
import com.avocado.glamping.viewmodel.GetRatingsState
import com.avocado.glamping.viewmodel.GetUtilitiesState
import com.avocado.glamping.viewmodel.RatingViewModel
import com.avocado.glamping.viewmodel.UpdateCampSiteState
import com.avocado.glamping.viewmodel.UtilityViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CampsiteDetailFragment : Fragment(R.layout.fragment_camp_site_detail), OnMapReadyCallback {

    private val args : CampsiteDetailFragmentArgs by navArgs()
    private lateinit var utilityRecyclerView : RecyclerView
    private lateinit var mapView : MapView
    private lateinit var campSite: CampsiteResponse
    private val utilityViewModel : UtilityViewModel by viewModels()
    private val viewModel : CampSiteViewModel by viewModels()
    private val ratingViewModel : RatingViewModel by viewModels()
    private lateinit var ratingRecyclerView : RecyclerView



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camp_site_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        campSite = args.campSite

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE  // Hide Bottom Navigation

        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            view.findViewById<TextView>(R.id.camp_site_name).text = campSite.name
            view.findViewById<TextView>(R.id.title_camp_site_name).text = campSite.name
            view.findViewById<TextView>(R.id.camp_site_description).text = campSite.description
            view.findViewById<ImageView>(R.id.img_camp_site_left).load(campSite.imageList.getOrNull(1)?.path)
            view.findViewById<ImageView>(R.id.img_camp_site_mid).load(campSite.imageList.getOrNull(0)?.path)
            view.findViewById<ImageView>(R.id.img_camp_site_right).load(campSite.imageList.getOrNull(2)?.path)
            view.findViewById<TextView>(R.id.camp_site_address).text = campSite.address

            view.findViewById<FrameLayout>(R.id.img_list).setOnClickListener{
                val images = ArrayList(campSite.imageList.map { imageResponse -> imageResponse.path })
                Log.e("CampSiteDetailImage", images.toString())
                val dialogFragment = ImageDialogFragment.newInstance(images)
                dialogFragment.show(parentFragmentManager, "ImageDialog")
            }

            view.findViewById<LinearLayout>(R.id.layout_camp_site_name).setOnClickListener{
                showCampSiteNameDialog(campSite)
            }

            view.findViewById<LinearLayout>(R.id.layout_camp_site_description).setOnClickListener{
                showCampSiteDescriptionDialog(campSite)
            }




            showAdapter(campSite, view)
            handleUpdate(view)

            view.findViewById<LinearLayout>(R.id.layout_camp_site_utility).setOnClickListener{
                fetchUtilities()
                fetchUtilityObserver()
            }
            if (campSite.status == "Pending"){
                view.findViewById<SwitchMaterial>(R.id.switch_status).visibility = View.INVISIBLE
            } else {
                view.findViewById<SwitchMaterial>(R.id.switch_status).isChecked = campSite.status == "Available"
            }
            view.findViewById<SwitchMaterial>(R.id.switch_status).setOnCheckedChangeListener{ _, isChecked ->
                handleUpdateCampSite(campSite.id, if (isChecked) "Available" else "Not_Available")
            }



            view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
                findNavController().popBackStack()
            }
            progressBar.visibility = View.GONE
        }

        mapView  = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.VISIBLE  // Show Bottom Navigation when returning
    }

    override fun onMapReady(p0: GoogleMap) {

        val campSiteLocation = LatLng(
            args.campSite.latitude,
            args.campSite.longitude
        )
        p0.addMarker(MarkerOptions().position(campSiteLocation).title(args.campSite.name))
        p0.moveCamera(CameraUpdateFactory.newLatLngZoom(campSiteLocation, 15f))
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mapView.isInitialized) { // Check if mapView is initialized
            mapView.onDestroy()
        }
    }


    private fun showCampSiteNameDialog(campSite : CampsiteResponse){
        val bottomSheet = CampSiteNameBottomSheetDialog.newInstance(campSite)
        bottomSheet.show(parentFragmentManager, "CustomBottomSheet")
    }

    private fun showCampSiteDescriptionDialog(campSite : CampsiteResponse){
        val bottomSheet = CampSiteDescriptionBottomSheetDialog.newInstance(campSite)
        bottomSheet.show(parentFragmentManager, "DescriptionBottomSheet")
    }

    private fun showAdapter(campSite : CampsiteResponse, view : View){
        val campTypeRecyclerView : RecyclerView = view.findViewById(R.id.campTypeRecyclerView)
        campTypeRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        campTypeRecyclerView.adapter = CampTypeAdapter(campSite.campSiteCampTypeList) { camptype ->
            val action = CampsiteDetailFragmentDirections.actionCampsiteDetailFragmentToCampTypeFragment(
                camptype
            )
            findNavController().navigate(action)
        }

        val serviceRecyclerView : RecyclerView = view.findViewById(R.id.serviceRecyclerView)
        serviceRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        serviceRecyclerView.adapter = ServiceAdapter(campSite.campSiteSelectionsList) { service ->
            val action = CampsiteDetailFragmentDirections.actionCampsiteDetailFragmentToServiceFragment(
                service
            )
            findNavController().navigate(action)
        }

        utilityRecyclerView = view.findViewById(R.id.utilityRecyclerView)
        utilityRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        utilityRecyclerView.adapter = UtilityAdapter(campSite.campSiteUtilityList){}

        fetchRatings(campSite.id)
        observeFetchRatings(view)

    }

    private fun handleUpdate(view: View) {
        parentFragmentManager.setFragmentResultListener("updateCampSite", viewLifecycleOwner) { _, bundle ->
            val updatedCampSite = bundle.getParcelable<CampsiteResponse>("updatedCampSite")
            updatedCampSite?.let { newCampSite ->
                Log.e("ParentFragment", "Updated CampSite: $newCampSite")
                campSite = updatedCampSite
                // Update UI with new camp details
                view.findViewById<TextView>(R.id.camp_site_name).text = newCampSite.name
                view.findViewById<TextView>(R.id.title_camp_site_name).text = newCampSite.name
                view.findViewById<TextView>(R.id.camp_site_description).text = newCampSite.description
                view.findViewById<ImageView>(R.id.img_camp_site_left).load(newCampSite.imageList.getOrNull(1)?.path)
                view.findViewById<ImageView>(R.id.img_camp_site_mid).load(newCampSite.imageList.getOrNull(0)?.path)
                view.findViewById<ImageView>(R.id.img_camp_site_right).load(newCampSite.imageList.getOrNull(2)?.path)

                // Update map location if changed
                val campSiteLocation = LatLng(newCampSite.latitude, newCampSite.longitude)
                mapView.getMapAsync { googleMap ->
                    googleMap.clear() // Remove old marker
                    googleMap.addMarker(MarkerOptions().position(campSiteLocation).title(newCampSite.name))
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(campSiteLocation, 15f))
                }

                // Update adapters with new data
                showAdapter(newCampSite, view)
            }
        }
    }

    private fun showUtilityDialog(utilities : List<UtilityResponse>, selectedUtilities: List<UtilityResponse>){
        Log.e("Dialog Counting", "1")
        val bottomSheet = CampSiteUtilityBottomSheet.newInstance(campSite,utilities, selectedUtilities){}
        bottomSheet.show(parentFragmentManager, "CampTypeFacility")
    }

    private fun fetchUtilities(){
        utilityViewModel.getUtilities(
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

    private fun fetchUtilityObserver(){
        utilityViewModel.getUtilitiesState.removeObservers(viewLifecycleOwner)
        utilityViewModel.getUtilitiesState.observe(viewLifecycleOwner){state ->
            when(state){
                is  GetUtilitiesState.Loading ->{

                }
                is GetUtilitiesState.Success ->{
                    showUtilityDialog(state.response.content, campSite.campSiteUtilityList)
                }
                is GetUtilitiesState.Error ->{
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleUpdateCampSite(id : Int, status : String){
        viewModel.updateCampsite(id, CampSiteUpdateRequest(
            null,
            null,
            null,
            status,
            null,
            null,
            null,
            null,
            null
        )
        )
    }

    private fun fetchRatings(campSiteId : Int){
        ratingViewModel.getRatings(
            campSiteId,
            0,
            10,
            "",
            "",
            "desc"
        )
    }

    private fun observeFetchRatings(view : View){
        ratingViewModel.getRatingsState.observe(viewLifecycleOwner) {state ->
            when(state) {
                is GetRatingsState.Loading -> {
                    view.findViewById<ProgressBar>(R.id.ratingProgressBar).visibility = View.VISIBLE
                    view.findViewById<RecyclerView>(R.id.ratingRecyclerView).visibility = View.GONE
                }
                is GetRatingsState.Success -> {
                    view.findViewById<ProgressBar>(R.id.ratingProgressBar).visibility = View.GONE
                    view.findViewById<RecyclerView>(R.id.ratingRecyclerView).visibility = View.VISIBLE
                    ratingRecyclerView = view.findViewById(R.id.ratingRecyclerView)
                    ratingRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

                    ratingRecyclerView.adapter = RatingAdapter(state.response.content.ratingResponseList)
                }
                is GetRatingsState.Error -> {
                    view.findViewById<ProgressBar>(R.id.ratingProgressBar).visibility = View.GONE
                    view.findViewById<RecyclerView>(R.id.ratingRecyclerView).visibility = View.GONE
                }
            }
        }
    }
}