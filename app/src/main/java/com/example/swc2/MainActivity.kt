package com.example.swc2

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
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
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var hour = 0;
    var minute = 0;


    lateinit var cycleLayout: ConstraintLayout;
    lateinit var endLayout: ConstraintLayout;
    lateinit var resultLayout: ConstraintLayout

    lateinit var cycleDuration: TextView;
    lateinit var endHour: TextView;
    lateinit var result: TextView

    lateinit var button: AppCompatButton
    lateinit var konfetti: KonfettiView

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
                button.text = "calculate"
                konfetti.reset();
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
        OnTimeSetListener { view, hourOfDay, minutes ->
            endHour.visibility = View.VISIBLE
            endHour.text = String.format("%02d:%02d",hourOfDay,minutes)
        }
    private val cycleTimePickerListener =
        OnTimeSetListener { view, hourOfDay, minutes ->
            cycleDuration.visibility = View.VISIBLE
            cycleDuration.text = String.format("%02d:%02d",hourOfDay,minutes)
        }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMinutesDif(fromDate: LocalTime, toDate: LocalTime): Long {
        return ChronoUnit.MINUTES.between(fromDate, toDate)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculResult() {
        if(cycleDuration.text.isNotEmpty() && endHour.text.isNotEmpty()){
            val cycles = cycleDuration.text.toString().split(':')
            val end = endHour.text.toString()
            val t2 = LocalTime.parse(end).minus(cycles[0].toLong()*60+cycles[1].toLong(),ChronoUnit.MINUTES)

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
            val mp = MediaPlayer.create(this, R.raw.party);
            mp.start()

            konfetti.start(party)
            resultLayout.visibility= View.VISIBLE
            button.text = "retry"
            result.text= String.format("%02d:%02d",hs_res,min_res)
        }


    }

}