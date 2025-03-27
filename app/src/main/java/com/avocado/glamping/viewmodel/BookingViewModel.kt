package com.avocado.glamping.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avocado.glamping.data.model.req.OrderRequest
import com.avocado.glamping.data.model.resp.BookingResponse
import com.avocado.glamping.data.model.resp.PagingResponse
import com.avocado.glamping.repository.BookingRepository
import com.avocado.glamping.repository.PaymentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repository: BookingRepository,
    private val paymentRepository : PaymentRepository
) : ViewModel(){
    private val _getBookingsState = MutableLiveData<GetBookingsState?>()
    val getBookingsState : LiveData<GetBookingsState?> get() = _getBookingsState
    private val _updateBookingState = MutableLiveData<UpdateBookingState?>()
    val updateBookingState : LiveData<UpdateBookingState?> get() = _updateBookingState
    private val _selectedStatus = MutableLiveData("Deposit")
    val selectedStatus: LiveData<String> get() = _selectedStatus
    private val _refundState = MutableLiveData<RefundState>()
    val refundState : LiveData<RefundState> get() = _refundState

    private var isLoading = false
    private var currentJob: Job? = null

    fun setSelectedStatus(status: String) {
        _selectedStatus.value = status
    }

     fun getBookings(
         params: Map<String, String?>,
         page: Int,
         size: Int,
         fields: String,
         sortBy: String,
         direction: String
    ) {

         if (isLoading) {
             currentJob?.cancel() // Cancel the previous call if still running
         }

         isLoading = true
         currentJob = viewModelScope.launch {
            _getBookingsState.value = GetBookingsState.Loading
            try {
                val response = repository.getBookings(params,page,size,fields,sortBy,direction)
                response.onSuccess { resp ->
                    _getBookingsState.value = GetBookingsState.Success(resp.data)

                }.onFailure { resp ->
                    _getBookingsState.value = resp.message?.let { GetBookingsState.Error(it) }
                    resp.message?.let { Log.e("Response data", it) }
                }
            } catch (e : Exception){
                _getBookingsState.value = GetBookingsState.Error(e.message.toString())
            }finally {
                isLoading = false
            }
        }
    }

    fun updateBooking(
        bookingId : Int,
        status : String,
        reason : String,
        bookingDetailOrders : List<OrderRequest>
    ) {
        viewModelScope.launch {
            _updateBookingState.value = UpdateBookingState.Loading
            try {
                val response = repository.updateBooking(bookingId, status, reason, bookingDetailOrders)
                response.onSuccess { resp ->
                    _updateBookingState.value = UpdateBookingState.Success(resp.data)
                }.onFailure { resp ->
                    _updateBookingState.value = resp.message?.let { UpdateBookingState.Error(it) }
                }
            } catch (e : Exception){
                _updateBookingState.value = UpdateBookingState.Error(e.message.toString())
            }
        }
    }
    fun refund(
        bookingId : Int,
        message : String
    ) {
        viewModelScope.launch {
            _refundState.value = RefundState.Loading
            try {
                val response = paymentRepository.refund(bookingId, message)
                response.onSuccess { resp ->
                    _refundState.value = RefundState.Success(resp.data)
                }.onFailure { resp ->
                    _refundState.value = RefundState.Error(resp.message.toString())
                }
            }catch (e : Exception){
                _refundState.value = RefundState.Error(e.message.toString())
            }
        }
    }
}

sealed class GetBookingsState{
    data object Loading: GetBookingsState()
    data class Success(val response : PagingResponse<List<BookingResponse>>) : GetBookingsState()
    data class Error(val mess : String) : GetBookingsState()
}

sealed class UpdateBookingState{
    data object Loading : UpdateBookingState()
    data class Success(val response: BookingResponse) : UpdateBookingState()
    data class Error(val mess : String) : UpdateBookingState()
}

sealed class RefundState{
    data object Loading : RefundState()
    data class Success(val response : String) : RefundState()
    data class Error(val mess : String) : RefundState()
}