package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.CampSiteUpdateRequest
import com.avocado.glamping.data.model.resp.CampsiteResponse
import com.avocado.glamping.repository.CampSiteRepository
import com.avocado.glamping.viewmodel.CampSiteViewModel
import com.avocado.glamping.viewmodel.UpdateCampSiteState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampSiteNameBottomSheetDialog() : BottomSheetDialogFragment() {
    private val viewModel : CampSiteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_camp_site_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val campSite = arguments?.getParcelable<CampsiteResponse>("campSite")

        val edCampSiteName =  view.findViewById<TextInputEditText>(R.id.et_camp_site_name)
        edCampSiteName.setText(campSite?.name)
        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            dismiss()
        }

        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            campSite?.id?.let { handleUpdateCampSite(it, edCampSiteName.text.toString()) }
        }


        observeUpdateCampSite(view)
    }

    private fun handleUpdateCampSite(id : Int, name : String){
        viewModel.updateCampsite(id, CampSiteUpdateRequest(name))
    }

    private fun observeUpdateCampSite(view : View){
        viewModel.updateCampSiteState.observe(viewLifecycleOwner){ state ->
            when(state){
                is UpdateCampSiteState.Loading ->{
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                }
                is UpdateCampSiteState.Success ->{
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    parentFragmentManager.setFragmentResult(
                        "updateCampSite",
                        Bundle().apply { putParcelable("updatedCampSite", state.response) }
                    )

                    dismiss()
                }
                is UpdateCampSiteState.Error ->{
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    companion object {
        fun newInstance(campSite : CampsiteResponse): CampSiteNameBottomSheetDialog {
            val fragment = CampSiteNameBottomSheetDialog()
            val args = Bundle()
            args.putParcelable("campSite", campSite)
            fragment.arguments = args
            return fragment
        }
    }
}