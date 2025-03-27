package com.avocado.glamping.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.req.CampSiteUpdateRequest
import com.avocado.glamping.data.model.resp.CampSiteNameResponse
import com.avocado.glamping.data.model.resp.CampsiteResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.repository.CampSiteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class CampSiteViewModel @Inject constructor(
    private val repository : CampSiteRepository
) : ViewModel(){
    private val _getCampSitesState = MutableLiveData<GetCampSitesState>()
    val getCampSitesState : LiveData<GetCampSitesState> get() = _getCampSitesState

    private val _updateCampSiteState = MutableLiveData<UpdateCampSiteState>()
    val updateCampSiteState : LiveData<UpdateCampSiteState> get() = _updateCampSiteState

    private val _getCampSitesNameState = MutableLiveData<GetCampSitesNameState>()
    val getCampSitesNameState : LiveData<GetCampSitesNameState> get() = _getCampSitesNameState

    private val _updateUtilities = MutableLiveData<UpdateUtilities>()
    val updateUtilities : LiveData<UpdateUtilities> get() = _updateUtilities

    fun getCampsites(
        params : Map<String, String>,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String
    ) {
        viewModelScope.launch {
            _getCampSitesState.value = GetCampSitesState.Loading
            try {
                val resp = repository.getCampSites(params, page, size, fields, sortBy, direction)
                resp.onSuccess { resp ->
                    _getCampSitesState.value = GetCampSitesState.Success(resp.data)
                }.onFailure { resp ->
                    _getCampSitesState.value = resp.message?.let { GetCampSitesState.Error(it) }
                }
            } catch (e : Exception){
                _getCampSitesState.value = GetCampSitesState.Error(e.message.toString())
            }
        }
    }

    fun getCampsitesName(
        params : Map<String, String>,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String
    ) {
        viewModelScope.launch {
            _getCampSitesNameState.value = GetCampSitesNameState.Loading
            try {
                val resp = repository.getCampSitesName(params, page, size, fields, sortBy, direction)
                resp.onSuccess { resp ->
                    _getCampSitesNameState.value = GetCampSitesNameState.Success(resp.data)
                }.onFailure { resp ->
                    _getCampSitesNameState.value = resp.message?.let { GetCampSitesNameState.Error(it) }
                }
            } catch (e : Exception){
                _getCampSitesNameState.value = GetCampSitesNameState.Error(e.message.toString())
            }
        }
    }

    fun updateCampsite(id : Int,request : CampSiteUpdateRequest){
        viewModelScope.launch {
            _updateCampSiteState.value = UpdateCampSiteState.Loading
            try {
                val response = repository.updateCampSite(id,request)
                response.onSuccess { resp ->
                    Log.e("UpdateCampSite", "Success")
                    _updateCampSiteState.value = UpdateCampSiteState.Success(resp.data)
                }.onFailure { resp ->
                    Log.e("UpdateCampSite", "Error")
                    _updateCampSiteState.value = UpdateCampSiteState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _updateCampSiteState.value = UpdateCampSiteState.Error(e.message.toString())
            }
        }
    }

    fun updateUtilities(id : Int, utilitiesIds : List<Int>){
        viewModelScope.launch {
            _updateUtilities.value = UpdateUtilities.Loading
            try {
                val response = repository.updateUtilities(id, utilitiesIds)
                response.onSuccess { resp ->
                    _updateUtilities.value = UpdateUtilities.Success(resp.data)
                }.onFailure { resp ->
                    _updateUtilities.value = UpdateUtilities.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _updateUtilities.value = UpdateUtilities.Error(e.message.toString())
            }
        }
    }

}

sealed class GetCampSitesState{
    data object Loading : GetCampSitesState()
    data class Success(val response : PagingResponse<List<CampsiteResponse>>) : GetCampSitesState()
    data class Error(val mess : String) : GetCampSitesState()
}

sealed class UpdateCampSiteState{
    data object Loading : UpdateCampSiteState()
    data class Success(val response : CampsiteResponse) : UpdateCampSiteState()
    data class Error(val mess : String) : UpdateCampSiteState()
}

sealed class GetCampSitesNameState{
    data object Loading : GetCampSitesNameState()
    data class Success(val response : PagingResponse<List<CampSiteNameResponse>>) : GetCampSitesNameState()
    data class Error(val mess : String) : GetCampSitesNameState()
}

sealed class UpdateUtilities{
    data object Loading : UpdateUtilities()
    data class Success(val response : CampsiteResponse) : UpdateUtilities()
    data class Error(val mess : String) : UpdateUtilities()
}