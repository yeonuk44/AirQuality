package com.chamber.airquality.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitConnection {
    /**
     * INFO:
     * singleton pattern의 객체 생성이 필요함. retrofit의 객체는 많은 자원을 소모하기 때문임.
     * 이를 위해 Companion Object를 선언함. 어떤 클래스의 모든 인스턴스가 공유하는 객체를 만들고 싶을 때 사용하며, 클래스당 한 개만 가질 수 있다.
     */
    companion object {
        private const val BASE_URL = "http://api.airvisual.com/v2/"
        private var INSTANCE: Retrofit? = null

        fun getInstance() : Retrofit {
            if(INSTANCE == null){
                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return INSTANCE!!
        }
    }
}