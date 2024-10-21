package com.example.growinganchovyman

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent.OnFinished
import android.content.ActivityNotFoundException
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import android.graphics.Color
import android.location.Location
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.gms.location.*

import com.kakao.sdk.user.UserApiClient
import com.kakao.vectormap.*
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.route.RouteLineManager
import com.kakao.vectormap.route.RouteLineOptions
import com.kakao.vectormap.route.RouteLinePattern
import com.kakao.vectormap.route.RouteLineSegment
import com.kakao.vectormap.route.RouteLineStyle
import com.kakao.vectormap.route.RouteLineStyles
import com.kakao.vectormap.route.RouteLineStylesSet

class RunFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var profileImageView: ImageView
    private lateinit var timerText: TextView
    private lateinit var startButton: ImageButton
    private lateinit var stopButton: ImageButton
    private var profileImageUrl: String? = null
    private var nickname: String? = null
    private var timer: CountDownTimer? = null
    private var isRunning = false
    private var isPaused = false
    private var time: Long = 0
    private var pauseTime: Long = 0
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var startPosition: LatLng? = null
    private var centerLabel: Label? = null
    private var requestingLocationUpdates = false
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    // 경로 추적을 위한 코드
    private var lastLocation: Location? = null
    private var totalDistance = 0f
    private var checkDistance = 0f // 계산 최적화를 위한 변수


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 프래그먼트의 레이아웃을 inflate
        return inflater.inflate(R.layout.fragment_run, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // XML에 정의된 View 요소를 찾아 초기화
        mapView = view.findViewById(R.id.map_view)
        timerText = view.findViewById(R.id.timer_text)
        startButton = view.findViewById(R.id.run_button)
        stopButton = view.findViewById(R.id.stop_button)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build()

        // 여기서 버튼 누르는거 인식함
        startButton.setOnClickListener{
            if(!isRunning && !isPaused){
                startTimer()
            } else if(isRunning){
                pauseTimer()
            }else if(isPaused){
                resumeTimer()
            }
        }

        stopButton.setOnClickListener{
            stopTimer()
        }


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                locationPermissions[0]
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                locationPermissions[1]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getStartLocation()
        } else {
            requestPermissions(
                locationPermissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        // Kakao API를 이용해 사용자 정보 가져오기
    }
    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient?.removeLocationUpdates(locationCallback!!)
    }


    private fun startTimer(){
        isRunning = true
        timerText.visibility = View.VISIBLE
        startButton.setImageResource(R.drawable.pause_button)
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000){
            override fun onTick(millisUnitFinished: Long){
                time += 1000
                updateTimerText()
            }

            override fun onFinish() {
            }
        }.start()
        totalDistance = 0f
    }

    private fun pauseTimer(){
        isRunning = false
        isPaused = true
        pauseTime = time
        timer?.cancel()
        startButton.setImageResource(R.drawable.resume_button)
    }

    private fun resumeTimer(){
        isRunning = true
        isPaused = false
        startButton.setImageResource(R.drawable.pause_button)

        timer = object : CountDownTimer(Long.MAX_VALUE, 1000){
            override fun onTick(millisUntilFinished: Long) {
                time += 1000
                updateTimerText()
            }

            override fun onFinish() {}
            }.start()
        }


    private fun stopTimer(){
        isRunning = false
        isPaused = false
        timer?.cancel()
        time = 0
        totalDistance = 0f
        updateTimerText()
        startButton.setImageResource(R.drawable.start_button)
        timerText.visibility = View.GONE
    }

    private fun updateTimerText(){
        val totalSeconds = (time/1000)
        val hours = (totalSeconds/3600)
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        val formatTime = String.format("%02d:%02d:%02d", hours,minutes,seconds)
        timerText.text = formatTime

        Log.d("run","시간: $formatTime")

    }

    // gps 위치 데이터를 이용한 거리 계산
    private fun getDistance(newLocation: Location): Float {
        if (lastLocation != null){
            val distance = lastLocation!!. distanceTo(newLocation)
            checkDistance += distance // 누적거리 업데이트
            Log.d("gps test", "$lastLocation")
            Log.d("gps test", "$newLocation")
            Log.d("distanceFun", "이동거리 체크: ${checkDistance}m")  // 누적거리 찍히는지 테스트 로그

            if (checkDistance >= 10f && checkDistance < 100f) {
                totalDistance += checkDistance
                checkDistance = 0f // 누적거리 초기화
                Log.d("distanceFun", "이동거리: ${totalDistance}m")
            }
        }
        lastLocation = newLocation
        return totalDistance
    }


    @SuppressLint("MissingPermission")
    private fun getStartLocation() {
        // 위치 권한이 있는지 확인
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 있으면 위치 요청
            fusedLocationClient?.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                ?.addOnSuccessListener { location ->
                    if (location != null) {
                        startPosition = LatLng.from(location.latitude, location.longitude)

                        // 지도 준비 콜백 및 MapLifeCycleCallback 설정
                        mapView.start(object : MapLifeCycleCallback() {
                            override fun onMapDestroy() {
                                // 지도 파괴 시 호출되는 콜백
                            }

                            override fun onMapError(error: Exception) {
                                // 지도 사용 중 에러가 발생할 때 호출되는 콜백
                                error.printStackTrace()
                            }
                        }, object : KakaoMapReadyCallback( ) {
                            override fun onMapReady(kakaoMap: KakaoMap) {
                                if (startPosition != null) {
                                    Log.e("kk", "map is ready")

                                    val routeLineManager: RouteLineManager? = kakaoMap.routeLineManager
                                    val routeStyles = RouteLineStyles.from(RouteLineStyle.from(16f,Color.GREEN))
                                    val stylesSet = RouteLineStylesSet.from(routeStyles)


                                    val styles = kakaoMap.labelManager?.addLabelStyles(
                                        LabelStyles.from(LabelStyle.from(R.drawable.marker))
                                    )

                                    val options = LabelOptions.from(startPosition).setStyles(styles)
                                    val layer = kakaoMap.labelManager?.layer
                                    if (layer != null) {
                                        centerLabel = layer.addLabel(options)
                                        Log.e("kk", "Label added successfully")
                                    } else {
                                        Log.e("kk", "Label layer is null")
                                    }

                                    if (centerLabel != null) {
                                        kakaoMap.trackingManager?.startTracking(centerLabel)
                                        Log.e("kk", "Tracking started")
                                    } else {
                                        Log.e("kk", "centerLabel is null")
                                    }

                                    locationCallback = object : LocationCallback() {
                                        override fun onLocationResult(locationResult: LocationResult) {
                                            for (location in locationResult.locations) {
                                                val newLatLng = LatLng.from(location.latitude,location.longitude)
                                                centerLabel?.moveTo(newLatLng)

                                                if(routeLineManager != null && startPosition != null){
                                                    val points = mutableListOf(startPosition,newLatLng)
                                                    val segment = RouteLineSegment.from(points).setStyles(stylesSet.getStyles(0))
                                                    val routeLineOptions = RouteLineOptions.from(segment).setStylesSet(stylesSet)
                                                    routeLineManager.layer.addRouteLine(routeLineOptions)
                                                }

                                                startPosition = newLatLng
                                                getDistance(location)
                                            }
                                        }
                                    }

                                    startLocationUpdates()

                                } else {
                                    Log.e("kk", "startposition is null")
                                }
                            }

                            override fun getPosition(): LatLng {
                                // 시작 위치 반환
                                return startPosition ?: LatLng.from(37.5665, 126.9780)
                            }
                             override fun getZoomLevel(): Int {
                                // 기본 줌 레벨 설정
                                return 19
                            }
                        })
                    } else{
                        Log.e("kk", "location is null")
                    }
                }?.addOnFailureListener {
                    // 실패 시 예외 처리
                    it.printStackTrace()
                }
        } else {
            // 권한이 없을 때 권한 요청
            requestPermissions(locationPermissions, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        requestingLocationUpdates = true
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest!!,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStartLocation()
            } else {
                showPermissionDeniedDialog()
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setMessage("위치 권한 거부시 앱을 사용할 수 없습니다.")
            .setPositiveButton("권한 설정하러 가기") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                        Uri.parse("package:${requireContext().packageName}")
                    )
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    startActivity(intent)
                }
            }
            .setNegativeButton("앱 종료하기") { _, _ ->
                requireActivity().finish()
            }
            .setCancelable(false)
            .show()
    }
}