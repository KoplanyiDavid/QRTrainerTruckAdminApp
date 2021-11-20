package com.vicegym.qrtrainertruckadminapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.GregorianCalendar
import android.icu.util.LocaleData
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vicegym.qrtrainertruckadminapp.data.TrainingData
import com.vicegym.qrtrainertruckadminapp.databinding.ActivityAddNewTrainingBinding
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class AddNewTrainingActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private lateinit var binding: ActivityAddNewTrainingBinding
    private lateinit var dateForSorting: String
    private lateinit var stringDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewTrainingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        binding.btnUploadTrainingData.setOnClickListener { uploadTrainingData() }
        binding.btnTrainingDate.setOnClickListener { showDatePickerDialog() }
    }

    private fun showDatePickerDialog() {
        val dialog = DatePickerDialog(
            this,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun showTimePickerDialog() {
        val dialog = TimePickerDialog(
            this,
            this,
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
            Calendar.getInstance().get(Calendar.MINUTE),
            true
        )
        dialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val formatter = SimpleDateFormat("yyyy.MM.dd(EEEE)", Locale.getDefault())
        stringDate = formatter.format(GregorianCalendar(year, month, dayOfMonth).time)
        val stringYear = year.toString()
        var stringMonth = (month + 1).toString()
        var stringDay = dayOfMonth.toString()
        if ((month + 1) / 10 == 0) {
            stringMonth = "0${(month + 1)}"
        }
        if (dayOfMonth / 10 == 0) {
            stringDay = "0$dayOfMonth"
        }
        //stringDate = "$stringYear.$stringMonth.$stringDay"
        dateForSorting = stringYear + stringMonth + stringDay
        showTimePickerDialog()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        stringDate += " ${formatter.format(Time(((hourOfDay - 1)* 60 * 60 * 1000 + minute * 60 * 1000).toLong()))}"
        var stringHour = hourOfDay.toString()
        var stringMinute = minute.toString()
        if (hourOfDay / 10 == 0) {
            stringHour = "0$hourOfDay"
        }
        if (minute / 10 == 0) {
            stringMinute = "0$minute"
        }
        //stringDate += " | $stringHour:$stringMinute"
        dateForSorting += "$stringHour$stringMinute"
        binding.tvDateText.text = stringDate
    }

    private fun uploadTrainingData() {
        val title = binding.etTrainingTitle.text.toString()
        val trainer = binding.etTrainingTrainer.text.toString()
        val location = binding.etTrainingLocation.text.toString()
        val date = stringDate
        val sorter = dateForSorting.toLong()

        val training = TrainingData(title, trainer, location, date, sorter)
        val data = hashMapOf(
            //"id" to training.id,
            "title" to training.title,
            "trainer" to training.trainer,
            "location" to training.location,
            "date" to training.date,
            "sorter" to sorter,
            "trainees" to arrayListOf<String>()
        )

        val db = Firebase.firestore
        if (training.sorter != null) {
            db.collection("trainings").document(training.sorter!!.toString())
                .set(data)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "Feltöltés sikeres", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(applicationContext, "Nincs ID-ja az edzésnek", Toast.LENGTH_SHORT).show()
        }
    }
}