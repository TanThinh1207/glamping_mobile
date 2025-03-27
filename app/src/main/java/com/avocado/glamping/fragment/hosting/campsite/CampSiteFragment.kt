package com.avocado.glamping.fragment.hosting.campsite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.adapter.CampsiteAdapter
import com.avocado.glamping.viewmodel.CampSiteViewModel
import com.avocado.glamping.viewmodel.GetCampSitesState
import dagger.hilt.android.AndroidEntryPoint
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

@AndroidEntryPoint
class CampSiteFragment : Fragment(R.layout.fragment_camp_site) {

    private val viewModel : CampSiteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camp_site, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val progressBar : ProgressBar = view.findViewById(R.id.progressBar)
        val campsiteRecyclerView : RecyclerView = view.findViewById(R.id.campsiteRecyclerView)
        campsiteRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        fetchCampSites()

        viewModel.getCampSitesState.observe(viewLifecycleOwner){ state ->
            when(state) {
                is GetCampSitesState.Loading -> {
                    campsiteRecyclerView.visibility = View.INVISIBLE
                    progressBar.visibility = View.VISIBLE
                }
                is GetCampSitesState.Success -> {
                    progressBar.visibility = View.GONE
                    campsiteRecyclerView.visibility = View.VISIBLE
                    val adapter = campsiteRecyclerView.adapter as? CampsiteAdapter
                    if (adapter == null) {
                        // If adapter is null, set a new one
                        campsiteRecyclerView.adapter =
                            CampsiteAdapter(state.response.content) { campsite ->
                                val action =
                                    CampSiteFragmentDirections.actionCampSiteFragmentToCampsiteDetailFragment(
                                        campsite
                                    )
                                findNavController().navigate(action)
                            }
                    }
                }
                is GetCampSitesState.Error -> {
                    campsiteRecyclerView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun createPoint(latitude : Double, longitude : Double) : Point{
        val geometryFactory = GeometryFactory(PrecisionModel(), 4326)
        return geometryFactory.createPoint(Coordinate(longitude, latitude))
    }

    private fun fetchCampSites(){
        viewModel.getCampsites(
            mapOf(
                "userId" to UserPreferences.getUser(requireContext())?.user?.id.toString()
            ),
            0,
            10,
            "",
            "id",
            "ASC"
        )
    }
}