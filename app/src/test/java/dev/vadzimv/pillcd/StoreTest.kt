package dev.vadzimv.pillcd

import org.junit.Assert.*
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
}

private fun createStore() = Store()