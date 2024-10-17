package com.example.growinganchovyman

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kakao.sdk.user.UserApiClient
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var profileImageView: ImageView
    private lateinit var profileTitle: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        val headerView = navigationView.getHeaderView(0)

        profileImageView = headerView.findViewById(R.id.profile_image)
        profileTitle = headerView.findViewById(R.id.profile_text)


        // Set initial fragment
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Setup BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.running -> {
                    // 사용자 정보를 Bundle로 RunFragment에 전달
//                    val runFragment = RunFragment().apply {
//                        arguments = Bundle().apply {
//                            putString("profileImageUrl", profileImageUrl)
//                            putString("nickname", nickname)
//                        }
//                    }
                    replaceFragment(RunFragment())
                    true
                }

                R.id.calender -> {
                    replaceFragment(CalenderFragment())
                    true
                }

                R.id.game -> {
                    replaceFragment(GameFragment())
                    true
                }

                else -> false
            }
        }
        loadProfileInfo()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu) // 메뉴 연결
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_button ->{
                drawerLayout.openDrawer(GravityCompat.END)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            super.onBackPressed()
        }
    }





    private fun loadProfileInfo() {
        UserApiClient.instance.me { user, error ->
            if (user != null) {
                // 사용자 프로필 이미지 URL 가져오기
                val profileImageUrl = user.kakaoAccount?.profile?.profileImageUrl
                val nickname = user.kakaoAccount?.profile?.nickname
                Log.d("profile", "$profileImageUrl")

                // Glide를 사용하여 프로필 이미지를 ImageView에 로드
                profileImageUrl?.let { url ->
                    Glide.with(this)
                        .load(url)
                        .placeholder(R.drawable.profile) // 로드 중 표시할 기본 이미지
                        .into(profileImageView)
                }

                // 사용자 이름을 타이틀로 설정
                profileTitle.text = nickname ?: "프로필"

            } else if (error != null) {
                // 오류 처리
                error.printStackTrace()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}