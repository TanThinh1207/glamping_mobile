package com.avocado.glamping.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.avocado.glamping.R
import com.avocado.glamping.data.model.req.OrderRequest
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class OrderBottomSheetDialog : BottomSheetDialogFragment() {

    private var onOrderAdded: ((OrderRequest) -> Unit)? = null

    fun setOnOrderAddedListener(listener: (OrderRequest) -> Unit) {
        onOrderAdded = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_add_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookingDetailId = arguments?.getInt("bookingDetailId") ?: return

        val etOrderName = view.findViewById<TextInputEditText>(R.id.et_order_name)
        val etOrderQuantity = view.findViewById<TextInputEditText>(R.id.et_order_quantity)
        val etOrderPrice = view.findViewById<TextInputEditText>(R.id.et_order_price)
        val etOrderNote = view.findViewById<TextInputEditText>(R.id.et_order_note)

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            dismiss()
        }

        view.findViewById<MaterialButton>(R.id.btn_save_order).setOnClickListener {
            val name = etOrderName.text.toString()
            val quantity = etOrderQuantity.text.toString().toIntOrNull() ?: 1
            val price = etOrderPrice.text.toString().toDoubleOrNull() ?: 0.0
            val total = quantity * price
            val note = etOrderNote.text.toString()

            val newOrder = OrderRequest(bookingDetailId, name, quantity, price, total, note)
            onOrderAdded?.invoke(newOrder)  // Call the listener safely
            dismiss()  // Close the dialog after saving
        }
    }

    companion object {
        fun newInstance(bookingDetailId: Int): OrderBottomSheetDialog {
            val fragment = OrderBottomSheetDialog()
            val args = Bundle()
            args.putInt("bookingDetailId", bookingDetailId)
            fragment.arguments = args
            return fragment
        }
    }
}
