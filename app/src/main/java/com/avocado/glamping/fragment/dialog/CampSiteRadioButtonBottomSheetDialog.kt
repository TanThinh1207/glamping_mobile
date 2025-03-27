package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.adapter.CampSiteRadioButtonAdapter
import com.avocado.glamping.data.model.resp.CampSiteNameResponse
import com.avocado.glamping.data.model.resp.UtilityResponse
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class CampSiteRadioButtonBottomSheetDialog(
    private val listener : OnCampSiteSelectedListener
) : BottomSheetDialogFragment() {

    private lateinit var adapter : CampSiteRadioButtonAdapter
    private lateinit var currentCampSite : CampSiteNameResponse

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_camp_site_radio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView : RecyclerView = view.findViewById(R.id.campsiteRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val campSites : List<CampSiteNameResponse> = arguments?.getParcelableArrayList("campSites") ?: emptyList()
        val selectedCampSite = arguments?.getParcelable<CampSiteNameResponse>("selectedCampSite")

        val saveBtn : MaterialButton = view.findViewById(R.id.btn_save)




        Log.e("Current CampSite", selectedCampSite.toString())
        Log.e("List CampSite", campSites.toString())

        if (selectedCampSite != null) {
            currentCampSite = selectedCampSite
            adapter = CampSiteRadioButtonAdapter(campSites, currentCampSite)
            recyclerView.adapter = adapter

            saveBtn.setOnClickListener {
                currentCampSite = adapter.getSelectedCampSite()!!
                listener.onCampSiteSelected(currentCampSite)
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance(
            campSites : List<CampSiteNameResponse>,
            selectedCampSite : CampSiteNameResponse,
            listener: OnCampSiteSelectedListener
        ): CampSiteRadioButtonBottomSheetDialog {
            val fragment = CampSiteRadioButtonBottomSheetDialog(listener)
            val args = Bundle()
            args.putParcelable("selectedCampSite", selectedCampSite)
            args.putParcelableArrayList("campSites", ArrayList(campSites))
            fragment.arguments = args
            return fragment
        }
    }

}

interface OnCampSiteSelectedListener {
    fun onCampSiteSelected(campSite: CampSiteNameResponse)
}