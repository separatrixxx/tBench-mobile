package com.megapixel.trashbench.Utils


import java.util.*

//Екстеншн. Проверяем дату на то в этом году она или нет
fun Date.isInCurrentYear(): Boolean {
    val currDate = Calendar.getInstance(Locale.getDefault())
    val srDate = Calendar.getInstance(Locale.getDefault()).apply { time = this@isInCurrentYear }
    return srDate.get(Calendar.YEAR) == currDate.get(Calendar.YEAR)
}

//Екстеншн. Проверяем дату на то сегодня она или нет
fun Date.isToday(): Boolean {
    val currDate = Calendar.getInstance(Locale.getDefault())
    val srDate = Calendar.getInstance(Locale.getDefault()).apply { time = this@isToday }
    return srDate.get(Calendar.YEAR) == currDate.get(Calendar.YEAR) && srDate.get(Calendar.MONTH) == currDate.get(
        Calendar.MONTH
    ) && srDate.get(
        Calendar.DAY_OF_MONTH
    ) == currDate.get(Calendar.DAY_OF_MONTH)
}