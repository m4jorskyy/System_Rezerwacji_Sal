package com.example.rezerwacje.ui.util // Dostosuj pakiet do swojej struktury folderów

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar

fun showDateTimePicker(
    context: Context,
    onDateTimeSelected: (LocalDateTime) -> Unit
) {
    val now = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context, { _, y, m, d ->
            TimePickerDialog(
                context, { _, h, min ->
                    val date = LocalDate.of(y, m + 1, d)
                    val time = LocalTime.of(h, min)
                    onDateTimeSelected(date.atTime(time))
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true
            ).show()
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
    )

    datePickerDialog.datePicker.minDate = now.timeInMillis

    datePickerDialog.show()
}