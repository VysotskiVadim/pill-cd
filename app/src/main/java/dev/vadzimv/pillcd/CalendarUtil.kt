package dev.vadzimv.pillcd

import android.app.Activity
import android.content.Intent
import android.provider.CalendarContract
import dev.vadzimv.pillcd.mainscreen.CalendarEvent

fun Activity.insertEvent(event: CalendarEvent) {
    val addEventIntent = Intent(Intent.ACTION_EDIT).apply {
        type = "vnd.android.cursor.item/event"
        putExtra(CalendarContract.Events.TITLE, event.title)
        putExtra(
            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
            event.begin
        )
        putExtra(
            CalendarContract.EXTRA_EVENT_END_TIME,
            event.end
        )
        putExtra(CalendarContract.Events.ALL_DAY, false)
    }
    startActivity(addEventIntent)
}