package com.avocado.glamping.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.resp.RevenueResponse
import com.avocado.glamping.repository.RevenueRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RevenueViewModel @Inject constructor(
    private val revenueRepository: RevenueRepository
) : ViewModel(){
    private val _getCampSiteRevenueState = MutableLiveData<GetCampSiteRevenueState>()
    val getCampSiteRevenueState : LiveData<GetCampSiteRevenueState> get() = _getCampSiteRevenueState

    fun getCampSiteRevenue(
        id : Int,
        startDate : String,
        endDate : String,
        campSiteId : Int,
        interval : String
    ) {
        viewModelScope.launch {
            try {
                _getCampSiteRevenueState.value = GetCampSiteRevenueState.Loading
                val response = revenueRepository.getCampSiteRevenue(id,startDate,endDate,campSiteId,interval)
                response.onSuccess { resp ->
                    _getCampSiteRevenueState.value = GetCampSiteRevenueState.Success(resp.data)
                }.onFailure { resp ->
                    _getCampSiteRevenueState.value = GetCampSiteRevenueState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _getCampSiteRevenueState.value= GetCampSiteRevenueState.Error(e.message.toString())
            }
        }
    }
}

sealed class GetCampSiteRevenueState(){
    data object Loading : GetCampSiteRevenueState()
    data class Success (val response : RevenueResponse) : GetCampSiteRevenueState()
    data class Error (val mess : String) : GetCampSiteRevenueState()
}