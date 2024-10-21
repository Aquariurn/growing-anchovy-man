package com.example.growinganchovyman

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 카카오 SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        val kakaologinButton: ImageButton = findViewById(R.id.kakao_login_button)

        kakaologinButton.setOnClickListener {
            // 카카오톡으로 로그인 가능한지 확인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                // 카카오톡으로 로그인 시도
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    handleLoginResult(token, error)
                }
            } else {
                // 카카오톡이 설치되어 있지 않으면 카카오 계정으로 로그인 시도
                UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                    handleLoginResult(token, error)
                }
            }
        }
    }

    private fun handleLoginResult(token: OAuthToken?, error: Throwable?) {
        if (error != null) {
            Log.d("login", "login fail: ${error.message}")
        } else {
            Log.d("login", "login success: ${token?.accessToken}")

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}