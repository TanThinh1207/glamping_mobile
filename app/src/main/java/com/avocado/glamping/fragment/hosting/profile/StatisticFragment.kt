package com.avocado.glamping.fragment.hosting.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.avocado.glamping.BuildConfig
import com.avocado.glamping.R
import com.avocado.glamping.UserPreferences
import com.avocado.glamping.data.model.resp.CampSiteNameResponse
import com.avocado.glamping.data.model.resp.ProfitResponse
import com.avocado.glamping.data.model.resp.RevenueResponse
import com.avocado.glamping.di.PriceFormat
import com.avocado.glamping.fragment.dialog.CampSiteRadioButtonBottomSheetDialog
import com.avocado.glamping.fragment.dialog.OnCampSiteSelectedListener
import com.avocado.glamping.viewmodel.CampSiteViewModel
import com.avocado.glamping.viewmodel.GetCampSiteRevenueState
import com.avocado.glamping.viewmodel.GetCampSitesNameState
import com.avocado.glamping.viewmodel.RevenueViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.WeakHashMap

@AndroidEntryPoint
class StatisticFragment : Fragment(R.layout.fragment_statistic), OnCampSiteSelectedListener {

    private val campSiteViewModel : CampSiteViewModel by viewModels()
    private val revenueViewModel : RevenueViewModel by viewModels()
    private lateinit var barChart : BarChart

    private lateinit var campSites : List<CampSiteNameResponse>
    private lateinit var campSiteRevenues : RevenueResponse

    private lateinit var startDate : TextInputEditText
    private lateinit var endDate : TextInputEditText

    private lateinit var currentCampSite : CampSiteNameResponse
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_statistic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        barChart = view.findViewById(R.id.chart_revenue)

        view.findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            findNavController().popBackStack()
        }

        setUpChart()

        val (start, end) = getCurrentMonthStartAndEnd()

        startDate = view.findViewById(R.id.ed_start_date)
        endDate = view.findViewById(R.id.ed_end_date)
        val dropDownLayout : LinearLayout = view.findViewById(R.id.dropdown_layout)
        startDate.setText(start)
        endDate.setText(end)

        fetchCampSitesName()
        observeFetchCampSitesName(view, startDate.text.toString(), endDate.text.toString())

        view.findViewById<MaterialButton>(R.id.btn_search).setOnClickListener {
            fetchCampSiteRevenue(startDate.text.toString(), endDate.text.toString(), currentCampSite.id, "daily")
            observeFetchCampSiteRevenue(view, startDate.text.toString(), endDate.text.toString())
        }

        startDate.setOnClickListener{
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.MaterialCalendarTheme)
                .build()


            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.format(selection)
                startDate.setText(date)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        endDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setTheme(R.style.MaterialCalendarTheme)
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.format(selection)
                endDate.setText(date)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        dropDownLayout.setOnClickListener{
            showCampSiteDialog()
        }

    }

    private fun getCurrentMonthStartAndEnd(): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val firstDay = LocalDate.now().withDayOfMonth(1)
        val lastDay = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())

        return Pair(firstDay.format(formatter), lastDay.format(formatter))
    }


    private fun observeFetchCampSitesName(view : View, startDate : String, endDate : String){
        campSiteViewModel.getCampSitesNameState.observe(viewLifecycleOwner){ state ->
            when(state) {
                is GetCampSitesNameState.Loading -> {
                    view.findViewById<NestedScrollView>(R.id.layout_statistic).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progress_bar_layout).visibility = View.VISIBLE
                    view.findViewById<TextView>(R.id.tv_no_camp_site).visibility = View.GONE
                }
                is GetCampSitesNameState.Success -> {
                    view.findViewById<NestedScrollView>(R.id.layout_statistic).visibility = View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.progress_bar_layout).visibility = View.GONE
                    view.findViewById<TextView>(R.id.tv_no_camp_site).visibility = View.GONE
                    campSites = state.response.content
                    if (campSites.isNotEmpty()){
                        currentCampSite = campSites[0]
                        view.findViewById<TextView>(R.id.title_camp_site_name).text = currentCampSite.name
                        Log.e("Start Date in observer", startDate)
                        fetchCampSiteRevenue(startDate, endDate, currentCampSite.id, "daily")
                        observeFetchCampSiteRevenue(view, startDate, endDate)
                    } else{
                        view.findViewById<LinearLayout>(R.id.layout_display_infor).visibility = View.GONE
                        view.findViewById<TextView>(R.id.tv_no_camp_site).visibility = View.VISIBLE
                    }
                }
                is GetCampSitesNameState.Error -> {
                    view.findViewById<NestedScrollView>(R.id.layout_statistic).visibility = View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.progress_bar_layout).visibility = View.GONE
                }
            }
        }
    }

    private fun fetchCampSitesName(){
        campSiteViewModel.getCampsitesName(
            mapOf(
                "userId" to UserPreferences.getUser(requireContext())?.user?.id.toString()
            ),
            0,
            10,
            "id,name",
            "id",
            "ASC"
        )
    }

    private fun observeFetchCampSiteRevenue(view : View, startDate: String, endDate: String){
        revenueViewModel.getCampSiteRevenueState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is GetCampSiteRevenueState.Loading -> {
                    view.findViewById<LinearLayout>(R.id.layout_revenue).visibility = View.GONE
                    view.findViewById<ProgressBar>(R.id.progress_bar_layout).visibility = View.VISIBLE
                    barChart.visibility = View.GONE
                }
                is GetCampSiteRevenueState.Success -> {
                    view.findViewById<LinearLayout>(R.id.layout_revenue).visibility = View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.progress_bar_layout).visibility = View.GONE
                    barChart.visibility = View.VISIBLE
                    campSiteRevenues = state.response
                    val totalRevenue = campSiteRevenues.profitList.sumOf { it.totalRevenue }
                    val percent : Double = if (campSiteRevenues.recentRevenue > 0){
                        (totalRevenue - campSiteRevenues.recentRevenue) / campSiteRevenues.recentRevenue
                    } else {
                        (totalRevenue - campSiteRevenues.recentRevenue)
                    }
                    view.findViewById<TextView>(R.id.tv_total_revenue).text = PriceFormat.formatPrice(totalRevenue, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                    view.findViewById<TextView>(R.id.tv_stripe_revenue).text = PriceFormat.formatPrice(campSiteRevenues.profitList.sumOf { it.totalProfit }, BuildConfig.LANGUAGE, BuildConfig.COUNTRY)
                    view.findViewById<TextView>(R.id.tv_revenue_percent).text = String.format("%.2f%%", percent * 100)
                    Log.e("Percent", percent.toString())
                    if (percent < 0) view.findViewById<TextView>(R.id.tv_revenue_percent).setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    else view.findViewById<TextView>(R.id.tv_revenue_percent).setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    val revenueData = campSiteRevenues.profitList.associate {
                        val formattedDate = SimpleDateFormat("MMM dd", Locale.getDefault()).format(parseDate(it.date).time)
                        formattedDate to it.totalRevenue.toFloat()
                    }
                    Log.e("Current Date in ViewModel", startDate)
                    updateChart(parseDate(startDate), parseDate(endDate), revenueData)
                }
                is GetCampSiteRevenueState.Error -> {
                    view.findViewById<LinearLayout>(R.id.layout_revenue).visibility = View.VISIBLE
                    view.findViewById<ProgressBar>(R.id.progress_bar_layout).visibility = View.GONE
                }
            }
        }
    }

    private fun fetchCampSiteRevenue(startDate : String, endDate : String, campSiteId : Int, interval : String){
        UserPreferences.getUser(requireContext())?.user?.id?.let {
            revenueViewModel.getCampSiteRevenue(
                it,
                startDate,
                endDate,
                campSiteId,
                interval
            )
        }
    }

    private fun setUpChart(){
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)
        barChart.setScaleEnabled(true)
        barChart.setPinchZoom(true)
        barChart.isDragEnabled = true

        val xAxis : XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)

        val yAxis : YAxis = barChart.axisLeft
        yAxis.granularity = 10000f
        yAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return NumberFormat.getCurrencyInstance(Locale(BuildConfig.LANGUAGE, BuildConfig.COUNTRY)).format(value)
            }
        }
        barChart.axisRight.isEnabled = false
    }

    private fun updateChart(startDate : Calendar, endDate : Calendar, revenueDate : Map<String, Float>){
        val barEntries = mutableListOf<BarEntry>()
        val dateLabels = mutableListOf<String>()

        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
        val currentDate = startDate.clone() as Calendar


        Log.e("Current Date", currentDate.time.toString())

        var index = 0f
        while (!currentDate.after(endDate)){
            val dateStr = sdf.format(currentDate.time)
            val dateDisplay = sdf.format(currentDate.time)

            val revenue = revenueDate[dateStr] ?: 0f
            Log.e("Revenue", revenue.toString())
            barEntries.add(BarEntry(index, revenue))
            dateLabels.add(dateDisplay)

            currentDate.add(Calendar.DAY_OF_MONTH, 1)
            index++
        }

        val barDataSet = BarDataSet(barEntries, "Revenue")
        barDataSet.color = android.graphics.Color.BLACK

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(dateLabels)
        barChart.invalidate()
    }

    private fun parseDate(dateStr: String): Calendar {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStr) ?: Date()
        return Calendar.getInstance().apply { time = date }
    }

    private fun showCampSiteDialog(){
        val bottomSheet = CampSiteRadioButtonBottomSheetDialog.newInstance(campSites, currentCampSite, this)
        bottomSheet.show(parentFragmentManager, "CampSiteRadioButton")
    }

    override fun onCampSiteSelected(campSite: CampSiteNameResponse) {
        if (currentCampSite.id != campSite.id){
            currentCampSite = campSite
            fetchCampSiteRevenue(startDate.text.toString(), endDate.text.toString(), currentCampSite.id, "daily")
        }
    }
}