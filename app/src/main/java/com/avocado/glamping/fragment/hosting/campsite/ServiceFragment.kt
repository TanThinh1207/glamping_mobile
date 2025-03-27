package com.avocado.glamping.fragment.hosting.campsite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.data.model.resp.CampTypeResponse
import com.avocado.glamping.data.model.resp.SelectionResponse
import com.avocado.glamping.di.PriceFormat
import com.avocado.glamping.fragment.dialog.ServiceDescriptionBottomSheet
import com.avocado.glamping.fragment.dialog.ServiceNameBottomSheet
import com.avocado.glamping.fragment.dialog.ServicePriceBottomSheet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.imageview.ShapeableImageView

class ServiceFragment : Fragment(R.layout.fragment_edit_service) {

    private lateinit var image : ShapeableImageView
    private lateinit var serviceName : TextView
    private lateinit var servicePrice : TextView
    private lateinit var serviceDescription : TextView
    private lateinit var service : SelectionResponse

    private lateinit var nameLayout : LinearLayout
    private lateinit var priceLayout : LinearLayout
    private lateinit var descriptionLayout : LinearLayout

    private val args : ServiceFragmentArgs by navArgs()

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation).visibility = View.GONE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_service, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            findNavController().popBackStack()
        }
        image = view.findViewById(R.id.img_service)
        serviceName = view.findViewById(R.id.tv_name)
        servicePrice = view.findViewById(R.id.tv_price)
        serviceDescription = view.findViewById(R.id.tv_description)
        nameLayout = view.findViewById(R.id.layout_name)
        priceLayout = view.findViewById(R.id.layout_price)
        descriptionLayout = view.findViewById(R.id.layout_description)
        service = args.service
        displayInformation()

        handleUpdate()

        nameLayout.setOnClickListener{
            showEditNameDialog()
        }

        priceLayout.setOnClickListener{
            showEditPriceDialog()
        }

        descriptionLayout.setOnClickListener{
            showEditDescriptionDialog()
        }
    }

    private fun displayInformation(){
        image.load(service.image){
            placeholder(R.drawable.image_placeholder)
            error(R.drawable.image_placeholder)
        }
        serviceName.text = service.name
        servicePrice.text = PriceFormat.formatPrice(service.price, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
        serviceDescription.text = service.description
    }

    private fun handleUpdate(){
        parentFragmentManager.setFragmentResultListener("updateService", viewLifecycleOwner) {_,bundle ->
            val updatedService = bundle.getParcelable<SelectionResponse>("updatedService")
            updatedService?.let { newService ->
                service = newService
                serviceName.text = service.name
                servicePrice.text = PriceFormat.formatPrice(service.price, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                serviceDescription.text = service.description
            }
        }
    }

    private fun showEditNameDialog(){
        val bottomSheet = ServiceNameBottomSheet.newInstance(service)
        bottomSheet.show(parentFragmentManager, "ServiceName")
    }

    private fun showEditPriceDialog(){
        val bottomSheet =  ServicePriceBottomSheet.newInstance(service)
        bottomSheet.show(parentFragmentManager, "ServicePrice")
    }

    private fun showEditDescriptionDialog(){
        val bottomSheet = ServiceDescriptionBottomSheet.newInstance(service)
        bottomSheet.show(parentFragmentManager, "ServiceDescription")
    }
}