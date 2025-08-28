package com.klifora.shop.networking

import com.klifora.shop.utils.showSnackBar

fun interface CallHandlerTranslate <T> {

    suspend fun sendRequestTranslate(apiInterface: ApiTranslateInterface): T

    fun loading(){
    }

    fun success(response: T){
    }

    fun error(message: String){
//        if(message.contains("DOCTYPE html")){
//            MainActivity.context?.get()?.resources?.getString(R.string.something_went_wrong)
//                ?.let { showSnackBar(it) }
//        }else{
        showSnackBar(message)
//        }
    }

}