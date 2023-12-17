package com.example.swc2

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var cycleLayout: ConstraintLayout
    private lateinit var endLayout: ConstraintLayout
    private lateinit var resultLayout: ConstraintLayout

    private lateinit var cycleDuration: TextView
    private lateinit var endHour: TextView
    private lateinit var result: TextView

    private lateinit var button: AppCompatButton
    private lateinit var konfetti: KonfettiView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cycleLayout = findViewById(R.id.cycleLayout)
        endLayout = findViewById(R.id.endLayout)
        resultLayout = findViewById(R.id.resultLayout)

        cycleDuration = findViewById(R.id.cycleDuration)
        endHour = findViewById(R.id.endHour)
        result = findViewById(R.id.result)

        button = findViewById(R.id.calcul)
        konfetti = findViewById(R.id.konfettiView)

        pickTime()

        resultLayout.setOnClickListener{
            resultLayout.visibility=View.GONE
        }

        button.setOnClickListener{
            if(resultLayout.visibility==View.GONE){
                calculResult()
            }else{
                resultLayout.visibility=View.GONE
                button.text = getString(R.string.calculate)
                konfetti.reset()
            }
        }



    }

    private fun pickTime() {

        endLayout.setOnClickListener {
            TimePickerDialog(this,endTimePickerListener,6,0,true).show()
        }
        cycleLayout.setOnClickListener {
            TimePickerDialog(this,cycleTimePickerListener,0,0,true).show()
        }

    }
    private val endTimePickerListener =
        OnTimeSetListener { _, hourOfDay, minutes ->
            endHour.visibility = View.VISIBLE
            endHour.text = String.format("%02d:%02d",hourOfDay,minutes)
        }
    private val cycleTimePickerListener =
        OnTimeSetListener { _, hourOfDay, minutes ->
            cycleDuration.visibility = View.VISIBLE
            cycleDuration.text = String.format("%02d:%02d",hourOfDay,minutes)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMinutesDif(fromDate: ZonedDateTime, toTime: LocalTime): Long {
        var toDate: ZonedDateTime = ZonedDateTime.of(LocalDateTime.of(LocalDate.now(),toTime),ZoneId.of("Europe/Paris"))
        if(fromDate>toDate){
            toDate = toDate.plusDays(1)
        }
        Log.d("from",fromDate.toString())
        Log.d("to",toDate.toString())
        Log.d("result",ChronoUnit.MINUTES.between(fromDate, toDate).toString())
        return ChronoUnit.MINUTES.between(fromDate, toDate)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculResult() {
        if(cycleDuration.text.isNotEmpty() && endHour.text.isNotEmpty()){
            val cycles = cycleDuration.text.toString().split(':')
            val end = endHour.text.toString()
            val t2 = LocalTime.parse(end).minus(cycles[0].toLong()*60+cycles[1].toLong(),ChronoUnit.MINUTES)
            var diff = getMinutesDif(Instant.now().atZone(ZoneId.of("Europe/Paris")),t2)
            Log.d("diff1",diff.toString())
            if(diff<0){
                diff+=1440
            }
            Log.d("cycles",cycles.toString())
            Log.d("end",end)
            Log.d("t2",t2.toString())
            Log.d("diff2",diff.toString())
            var min = diff%60
            val hs = (diff/60).toInt()
            Log.d("min",min.toString())
            Log.d("hs",hs.toString())

            min = if(min>=30)
                30
            else 0

            val party = Party(
                speed = 0f,
                maxSpeed = 50f,
                damping = 0.9f,
                spread = 90,
                angle = -90,
                colors = listOf(0xd67b36, 0x232323, 0xc4c4c4),
                emitter = Emitter(duration = 200, TimeUnit.MILLISECONDS).max(100),
                position = Position.Relative(0.5, 0.93)
            )
            val mp = MediaPlayer.create(this, R.raw.party)
            mp.start()

            konfetti.start(party)
            resultLayout.visibility= View.VISIBLE
            button.text = getString(R.string.retry)
            result.text= String.format("%02d:%02d",hs,min)
        }


    }

}