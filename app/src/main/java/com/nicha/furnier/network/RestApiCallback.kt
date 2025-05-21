package com.nicha.furnier.network


interface RestApiCallback<T, K> {

    fun onSuccess(aApiCode: Int, aSuccessResponse: T)

    fun onApiError(aApiCode: Int, aFailureResponse: K)

}
