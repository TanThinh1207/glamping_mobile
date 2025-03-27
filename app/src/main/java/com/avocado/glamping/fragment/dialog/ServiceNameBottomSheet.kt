package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.avocado.glamping.R
import com.avocado.glamping.data.model.dto.CampSite
import com.avocado.glamping.data.model.req.ServiceUpdateRequest
import com.avocado.glamping.data.model.resp.CampsiteResponse
import com.avocado.glamping.data.model.resp.SelectionResponse
import com.avocado.glamping.viewmodel.SelectionViewModel
import com.avocado.glamping.viewmodel.UpdateSelectionState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ServiceNameBottomSheet : BottomSheetDialogFragment(){

    private lateinit var service : SelectionResponse
    private lateinit var edServiceName : TextInputEditText
    private lateinit var saveBtn : MaterialButton
    private lateinit var progressBar: ProgressBar

    private val serviceViewModel : SelectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_service_name, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            dismiss()
        }

        service = arguments?.getParcelable("service")!!
        edServiceName = view.findViewById(R.id.et_service_name)
        edServiceName.setText(service.name)
        saveBtn = view.findViewById(R.id.btn_save)
        progressBar = view.findViewById(R.id.progressBar)

        saveBtn.setOnClickListener {
            updateService()
            observeUpdateService()
        }

    }

    private fun updateService(){
        serviceViewModel.updateSelection(
            ServiceUpdateRequest(
                service.id,
                edServiceName.text.toString(),
                null,
                null
            )
        )
    }

    private fun observeUpdateService(){
        serviceViewModel.updateSelectionState.observe(viewLifecycleOwner) {state ->
            when(state) {
                is UpdateSelectionState.Loading -> {
                    saveBtn.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                }
                is UpdateSelectionState.Success -> {
                    saveBtn.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    parentFragmentManager.setFragmentResult(
                        "updateService",
                        Bundle().apply { putParcelable("updatedService", state.response) }
                    )
                    dismiss()
                }
                is UpdateSelectionState.Error -> {
                    saveBtn.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(service : SelectionResponse): ServiceNameBottomSheet{
            val fragment = ServiceNameBottomSheet()
            val args = Bundle()
            args.putParcelable("service", service)
            fragment.arguments = args
            return fragment
        }
    }
}