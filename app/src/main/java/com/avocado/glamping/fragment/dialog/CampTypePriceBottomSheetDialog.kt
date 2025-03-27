package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.CampTypeUpdateRequest
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.di.PriceFormat
import com.avocado.glamping.viewmodel.CampTypeViewModel
import com.avocado.glamping.viewmodel.UpdateCampTypeState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CampTypePriceBottomSheetDialog : BottomSheetDialogFragment() {

    private val campTypeViewModel : CampTypeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_edit_price, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val priceEditText = view.findViewById<TextInputEditText>(R.id.et_price)

        val campType = arguments?.getParcelable<CampTypeResponse>("campType")


        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            dismiss()
        }
        view.findViewById<TextView>(R.id.tv_camp_type_price).text = arguments?.getString("type")
        if (arguments?.getString("type") == "Daily Price") priceEditText.setText(PriceFormat.formatPrice(campType?.price, BuildConfig.LANGUAGE, BuildConfig.COUNTRY) )
        else priceEditText.setText(PriceFormat.formatPrice(campType?.weekendRate, BuildConfig.LANGUAGE, BuildConfig.COUNTRY) )
        priceEditText.addTextChangedListener(object : TextWatcher{
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    priceEditText.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("\\D".toRegex(), "")
                    Log.e("Money", cleanString)
                    if (cleanString.isNotEmpty()) {
                        val parsed = PriceFormat.formatPrice(cleanString.toDouble(), BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                        current = parsed

                        priceEditText.setText(current)
                        priceEditText.setSelection(current.length - 2)
                    }

                    priceEditText.addTextChangedListener(this)
                }
            }

        })

        view.findViewById<MaterialButton>(R.id.btn_save).setOnClickListener {
            val cleanString = priceEditText.text.toString().replace("\\D".toRegex(), "")
            campType?.id?.let { it1 -> updateCampType(cleanString.toDouble(), it1) }
        }
        updateCampTypeObserver(view)
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

    private fun updateCampType(price : Double, id : Int){
        if (arguments?.getString("type") == "Daily Price"){
            campTypeViewModel.updateCampType(
                CampTypeUpdateRequest(
                    null,
                    null,
                    price,
                    null,
                    null,
                    null
                ),
                id
            )
        }else{
            campTypeViewModel.updateCampType(
                CampTypeUpdateRequest(
                    null,
                    null,
                    null,
                    price,
                    null,
                    null
                ),
                id
            )
        }

    }

    companion object {
        fun newInstance(campType : CampTypeResponse, type : String): CampTypePriceBottomSheetDialog {
            val fragment = CampTypePriceBottomSheetDialog()
            val args = Bundle()
            args.putString("type", type)
            args.putParcelable("campType", campType)
            fragment.arguments = args
            return fragment
        }
    }
}