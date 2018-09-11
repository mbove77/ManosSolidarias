package com.bove.martin.manossolidarias.activities.utils;

import com.github.bassaer.chatmessageview.util.ITimeFormatter;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Martín Bove on 10/09/2018.
 * E-mail: mbove77@gmail.com
 */
public class DateFormat implements ITimeFormatter {
    @NotNull
    @Override
    public String getFormattedTimeText(Calendar createdAt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy");
        return sdf.format(createdAt.getTime());
    }
}
