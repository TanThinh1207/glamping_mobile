package com.avocado.glamping.fragment.hosting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.avocado.glamping.R
import com.avocado.glamping.adapter.CalendarAdapter
import com.avocado.glamping.data.model.resp.CampsiteResponse

class CalendarFragment : Fragment(R.layout.fragment_calendar) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarRecyclerView : RecyclerView = view.findViewById(R.id.calendarRecyclerView)
        calendarRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        val campsites = mutableListOf(
            CampsiteResponse("Tropical Eglamping", "Sẵn sáng phục vụ"),
            CampsiteResponse("Sunshine Glamping", "Tạm ngưng phục vụ")
        )
        calendarRecyclerView.adapter = CalendarAdapter(campsites) { campsite ->
            Toast.makeText(requireContext(),campsite.name, Toast.LENGTH_SHORT).show()
        }
    }
}