package dev.vadzimv.pillcd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class StoreTest {
    @Test
    fun `change duration`() {
        val store = createStore()
        store.apply(Action.CoolDownTimeChanged("9"))
        assertEquals("9", store.state.value.coolDownTimeFormatted)
        assertEquals(true, store.state.value.addToCalendarButtonEnabled)
    }

    @Test
    fun `enter float duration`() {
        val store = createStore()
        store.apply(Action.CoolDownTimeChanged("9"))
        store.apply(Action.CoolDownTimeChanged("9."))
        assertEquals("9", store.state.value.coolDownTimeFormatted)
    }

    @Test
    fun `enter negative duration`() {
        val store = createStore()
        store.apply(Action.CoolDownTimeChanged("9"))
        store.apply(Action.CoolDownTimeChanged("-9"))
        assertEquals("9", store.state.value.coolDownTimeFormatted)
    }

    @Test
    fun `change title`() {
        val store = createStore()
        store.apply(Action.TitleChanged("test"))
        assertEquals("test", store.state.value.title)
    }

    @Test
    fun `empty duration makes button disabled`() {
        val store = createStore()
        store.apply(Action.CoolDownTimeChanged(""))
        assertEquals("", store.state.value.coolDownTimeFormatted)
        assertEquals(false, store.state.value.addToCalendarButtonEnabled)
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