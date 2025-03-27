package com.avocado.glamping.fragment.hosting.campsite

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.avocado.glamping.R
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

class ImageDialogFragment : DialogFragment() {

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_image_slider, container, false)
        val imageSlider : ImageSlider = view.findViewById(R.id.image_slider)

        val images = arguments?.getStringArrayList("images") ?: arrayListOf()
        Log.e("ImageDialogFragment", images.toString())

        val imageSliderList = images.map { image -> SlideModel(image, "title", ScaleTypes.FIT) }

        imageSlider.setImageList(imageSliderList)

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, 700)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    companion object {
        fun newInstance(images: ArrayList<String>): ImageDialogFragment {
            val fragment = ImageDialogFragment()
            val args = Bundle()
            args.putStringArrayList("images", images)
            fragment.arguments = args
            return fragment
        }
    }
}