package dev.vadzimv.pillcd.mainscreen

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MainScreenTest {
    @Test
    fun `change duration`() {
        val store = createStore()
        store.apply(Action.CoolDownTimeChanged("9"))
        assertEquals("9", store.mainScreenState.value.coolDownTimeFormatted)
        assertEquals(true, store.mainScreenState.value.addToCalendarButtonEnabled)
    }

    @Test
    fun `enter float duration`() {
        val store = createStore()
        store.apply(Action.CoolDownTimeChanged("9"))
        store.apply(Action.CoolDownTimeChanged("9."))
        assertEquals("9", store.mainScreenState.value.coolDownTimeFormatted)
    }

    @Test
    fun `enter negative duration`() {
        val store = createStore()
        store.apply(Action.CoolDownTimeChanged("9"))
        store.apply(Action.CoolDownTimeChanged("-9"))
        assertEquals("9", store.mainScreenState.value.coolDownTimeFormatted)
    }

    @Test
    fun `change title`() {
        val store = createStore()
        store.apply(Action.TitleChanged("test"))
        assertEquals("test", store.mainScreenState.value.title)
    }

    @Test
    fun `empty duration makes button disabled`() {
        val store = createStore()
        store.apply(Action.CoolDownTimeChanged(""))
        assertEquals("", store.mainScreenState.value.coolDownTimeFormatted)
        assertEquals(false, store.mainScreenState.value.addToCalendarButtonEnabled)
    }

    @Test
    fun `insert 5 hours cd`() {
        var addedEvent: CalendarEvent? = null
        val store = createStore(
            currentTimeProvider = { 3L },
            insertCalendarEvent = { addedEvent = it}
        )
        store.apply(Action.CoolDownTimeChanged("5"))
        store.apply(Action.TitleChanged("test"))
        store.apply(Action.AddToCalendarClicked)

        assertNotNull(addedEvent)
        val event = addedEvent!!
        assertEquals(3, event.begin)
        assertEquals(5*60*60*1000 + 3, event.end)
        assertEquals("test", event.title)
        val latestPill = store.mainScreenState.value.latestPills.first()
        assertEquals(5, latestPill.duration.inWholeHours)
        assertEquals("test", latestPill.title)
    }

    @Test
    fun `added five different events`() {
        var addedEventsCount = 0
        val store = createStore(
            insertCalendarEvent = { addedEventsCount++ }
        )
        for (i in 1..5) {
            store.apply(Action.CoolDownTimeChanged("$i"))
            store.apply(Action.TitleChanged("test$i"))
            store.apply(Action.AddToCalendarClicked)
        }

        assertEquals(5, addedEventsCount)
        val latestPills = store.mainScreenState.value.latestPills
        assertEquals(listOf<Long>(5, 4, 3), latestPills.map { it.duration.inWholeHours })
    }

    @Test
    fun `added the same event many times when it's already in list`() {
        val store = createStore()

        store.apply(Action.CoolDownTimeChanged("2"))
        store.apply(Action.TitleChanged("test2"))
        store.apply(Action.AddToCalendarClicked)

        store.apply(Action.CoolDownTimeChanged("1"))
        store.apply(Action.TitleChanged("test"))
        store.apply(Action.AddToCalendarClicked)

        for (i in 1..5) {
            store.apply(Action.CoolDownTimeChanged("2"))
            store.apply(Action.TitleChanged("test2"))
            store.apply(Action.AddToCalendarClicked)
        }

        val latestPills = store.mainScreenState.value.latestPills
        assertEquals(listOf<Long>(2, 1), latestPills.map { it.duration.inWholeHours })
    }
}

private fun createStore(
    currentTimeProvider: () -> Long = { 0L },
    insertCalendarEvent: (CalendarEvent) -> Unit = {}
) =
    Store(
        currentTimeProvider,
        insertCalendarEvent,
    )