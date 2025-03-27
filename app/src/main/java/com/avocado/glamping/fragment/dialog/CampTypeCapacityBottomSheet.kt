package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.CampTypeUpdateRequest
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.viewmodel.CampTypeViewModel
import com.avocado.glamping.viewmodel.UpdateCampTypeState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampTypeCapacityBottomSheet : BottomSheetDialogFragment() {

    private val campTypeViewModel : CampTypeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_camp_type_capacity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val campType = arguments?.getParcelable<CampTypeResponse>("campType")

        var capacity = arguments?.getInt("amount")
        val plus = view.findViewById<ImageView>(R.id.img_plus)
        val minus = view.findViewById<ImageView>(R.id.img_minus)
        val tvCapacity = view.findViewById<TextView>(R.id.tv_capacity)
        tvCapacity.text = capacity.toString()

        plus.setOnClickListener {
            capacity = capacity!! + 1
            tvCapacity.text = capacity.toString()
        }

        minus.setOnClickListener {
            if (capacity!! > 1) {
                capacity = capacity!! - 1
                tvCapacity.text = capacity.toString()
            }
        }

        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            if (campType != null) {
                updateCampType(tvCapacity.text.toString().toInt(), campType.id)
            }
            updateCampTypeObserver(view)
        }
    }

    private fun updateCampType(capacity : Int, id : Int){
        campTypeViewModel.updateCampType(
            CampTypeUpdateRequest(
                null,
                capacity,
                null,
                null,
                null,
                null
            ),
            id
        )
    }

    private fun updateCampTypeObserver(view : View){
        campTypeViewModel.updateCampTypeState.observe(viewLifecycleOwner){ state ->
            when(state){
                is UpdateCampTypeState.Loading -> {
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.INVISIBLE
                }
                is UpdateCampTypeState.Success -> {
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    parentFragmentManager.setFragmentResult(
                        "updateCampType",
                        Bundle().apply { putParcelable("updatedCampType", state.response) }
                    )
                    dismiss()
                }
                is UpdateCampTypeState.Error -> {
                    view.findViewById<MaterialButton>(R.id.btn_save).visibility = View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                    Toast.makeText(requireContext(), state.mess, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(campType : CampTypeResponse, amount : Int): CampTypeCapacityBottomSheet {
            val fragment = CampTypeCapacityBottomSheet()
            val args = Bundle()
            args.putInt("amount", amount)
            args.putParcelable("campType", campType)
            fragment.arguments = args
            return fragment
        }
    }
}