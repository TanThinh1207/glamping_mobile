package com.avocado.glamping.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.resp.AverageRatingResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.data.model.resp.RatingResponse
import com.avocado.glamping.repository.RatingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val repository : RatingRepository
) : ViewModel(){

    private val _getRatingsState = MutableLiveData<GetRatingsState>()
    val getRatingsState : LiveData<GetRatingsState> get() = _getRatingsState

    fun getRatings(
        campSiteId : Int,
        page : Int,
        size : Int,
        fields : String,
        sortBy : String,
        direction : String,
    ){
        viewModelScope.launch {
            _getRatingsState.value = GetRatingsState.Loading
            try {
                val response = repository.getRatings(campSiteId,page,size,fields,sortBy,direction)
                response.onSuccess { resp ->
                    _getRatingsState.value = GetRatingsState.Success(resp.data)
                }.onFailure { resp ->
                    _getRatingsState.value = GetRatingsState.Error(resp.message.toString())
                    Log.e("Rating Error", resp.message.toString())
                }
            }catch (e : Exception){
                Log.e("Rating Exception", e.toString())
                _getRatingsState.value = GetRatingsState.Error(e.message.toString())
            }
        }
    }
}

sealed class GetRatingsState {
    data object Loading : GetRatingsState()
    data class Success (val response : PagingResponse<AverageRatingResponse>) : GetRatingsState()
    data class Error (val mess : String) : GetRatingsState()
}