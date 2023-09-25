package com.example.swc2

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    var hour = 0;
    var minute = 0;


    lateinit var cycleLayout: ConstraintLayout;
    lateinit var endLayout: ConstraintLayout;

    lateinit var cycleDuration: TextView;
    lateinit var endHour: TextView;
    lateinit var result: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cycleLayout = findViewById(R.id.cycleLayout)
        endLayout = findViewById(R.id.endLayout)

        cycleDuration = findViewById(R.id.cycleDuration)
        endHour = findViewById(R.id.endHour)
        result = findViewById(R.id.result)
        pickTime()

    }

    private fun pickTime() {

        endLayout.setOnClickListener {
            getTimeCalendar()
            TimePickerDialog(this,endTimePickerListener,hour,minute,true).show()
        }
        cycleLayout.setOnClickListener {
            getTimeCalendar()
            TimePickerDialog(this,cycleTimePickerListener,hour,minute,true).show()
        }

    }
    private val endTimePickerListener =
        OnTimeSetListener { view, hourOfDay, minutes ->
            endHour.visibility = View.VISIBLE
            endHour.text = String.format("%02d:%02d",hourOfDay,minutes)
        }
    private val cycleTimePickerListener =
        OnTimeSetListener { view, hourOfDay, minutes ->
            cycleDuration.visibility = View.VISIBLE
            cycleDuration.text = String.format("%02d:%02d",hourOfDay,minutes)
        }
    private fun getTimeCalendar() {
        val cal = Calendar.getInstance()
        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getMinutesDif(fromDate: LocalTime, toDate: LocalTime): Long {
        return ChronoUnit.MINUTES.between(fromDate, toDate)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculResult(view: View) {
        if(cycleDuration.text.isNotEmpty() && endHour.text.isNotEmpty()){
            val cycles = cycleDuration.text.toString().split(':')
            val end = endHour.text.toString()
            val t2 = LocalTime.parse(end).minus(cycles[0].toLong()*60+cycles[1].toLong(),ChronoUnit.MINUTES)

        getTimeCalendar()
            var diff = getMinutesDif(LocalTime.now().atOffset(ZoneOffset.UTC)
                .withOffsetSameInstant(ZoneOffset.of("+02:00")).toLocalTime(),t2)
            if(diff<0){
                diff+=1440
            }
            var min_res = diff%60
            val hs_res = (diff/60).toInt()

            if(min_res>=45)
                min_res=45
                else if(min_res>=30)
                min_res=30
            else if(min_res>=15)
                min_res=15
            else min_res=0
            result.visibility= View.VISIBLE
            result.text= String.format("%02d:%02d",hs_res,min_res)
            Toast.makeText(this, (diff%60).toString()+" "+(diff/60).toInt().toString(), Toast.LENGTH_LONG).show()

        }

    }

}