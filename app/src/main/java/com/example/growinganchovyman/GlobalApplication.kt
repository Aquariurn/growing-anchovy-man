package com.example.growinganchovyman

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapSdk

class GlobalApplication:  Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "c76adf64f85958c645afd63332bfd290")
        KakaoMapSdk.init(this,"c76adf64f85958c645afd63332bfd290")
    }
}