package com.example.growinganchovyman

import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.HashMap
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var calendarView: MaterialCalendarView
    private lateinit var eventTextView: TextView
    private var currentMonth: Int = CalendarDay.today().month - 1

    // 샘플 일정 데이터 (날짜별로 일정 저장)
    private val eventMap: MutableMap<CalendarDay, String> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendar)
        eventTextView = view.findViewById(R.id.event_text)

        // 오늘 날짜 선택
        val today = CalendarDay.today()
        calendarView.selectedDate = today
        updateEventText(today)

        // 샘플 일정 데이터 추가
        eventMap[CalendarDay.from(2024, 3, 24)] = "월정이와 봄소풍 나가기"
        eventMap[CalendarDay.from(2024, 10, 11)] = "회의 일정: 프로젝트 발표"
        eventMap[CalendarDay.from(2024, 10, 12)] = "친구 생일 파티"

        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_ALL)

        // 데코레이터 추가 (일요일, 토요일 색상, 오늘 날짜 표시)
        calendarView.addDecorators(
            SundayDecorator(),
            SaturdayDecorator(),
            TodayDecorator(),
            OtherMonthDecorator(currentMonth),
            EventDecorator(eventMap.keys) // 일정 있는 날짜에 점 표시
        )

        calendarView.setTitleFormatter { day ->
            val calendar = Calendar.getInstance()
            calendar.set(day.year, day.month - 1, day.day)
            val dateFormat = SimpleDateFormat("yyyy년 MM월", Locale.getDefault())
            dateFormat.format(calendar.time) // "YYYY년 MM월" 형식으로 반환
        }


        // 날짜 선택 리스너 설정
        calendarView.setOnDateChangedListener(OnDateSelectedListener { _, date, _ ->
            updateEventText(date)
        })

        calendarView.setOnMonthChangedListener{widget, date  ->
            widget.clearSelection()

        }

    }

    // 선택한 날짜에 해당하는 일정을 표시하는 메서드
    private fun updateEventText(date: CalendarDay) {
        val event = eventMap[date]
        eventTextView.text = event ?: "일정이 없습니다."
    }


    // 일요일 텍스트 색상 변경 데코레이터
    class SundayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val calendar = Calendar.getInstance()
            calendar.set(day.year, day.month - 1, day.day)
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.RED))
        }
    }

    // 토요일 텍스트 색상 변경 데코레이터
    class SaturdayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            val calendar = Calendar.getInstance()
            calendar.set(day.year, day.month - 1, day.day)
            return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.BLUE))
        }
    }

    // 오늘 날짜 배경 변경 데코레이터
    class TodayDecorator : DayViewDecorator {
        private val today: CalendarDay = CalendarDay.today()

        override fun shouldDecorate(day: CalendarDay): Boolean {
            return day == today
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(10F, Color.GREEN)) // 점으로 오늘 날짜 표시
        }
    }

    class OtherMonthDecorator(private val currentMonth: Int) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            // 현재 달이 아닌 경우에만 회색으로 표시합니다.
            return day.month -1 != currentMonth // 현재 달의 날짜가 아닐 때
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.LTGRAY)) // 텍스트 색상을 연한 회색으로 변경
        }
    }

    // 이벤트 날짜에 점 표시하는 데코레이터
    class EventDecorator(private val dates: Set<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return dates.contains(day)
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(10F, Color.MAGENTA)) // 점 색상으로 이벤트 표시
        }
    }
}