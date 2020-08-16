package com.bove.martin.manossolidarias.activities.utils

import com.github.bassaer.chatmessageview.util.ITimeFormatter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Martín Bove on 10/09/2018.
 * E-mail: mbove77@gmail.com
 */
class DateFormat : ITimeFormatter {
    override fun getFormattedTimeText(createdAt: Calendar): String {
        val sdf = SimpleDateFormat("dd/MMM/yyyy")
        return sdf.format(createdAt.time)
    }
}