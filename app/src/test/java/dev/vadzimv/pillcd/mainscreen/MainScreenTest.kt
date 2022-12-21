package dev.vadzimv.pillcd.mainscreen

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineExceptionHandler
import kotlinx.coroutines.test.createTestCoroutineScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MainScreenTest {
    @Test
    fun `change duration`() {
        val viewModel = createViewModel()
        viewModel.apply(Action.CoolDownTimeChanged("9"))
        assertEquals("9", viewModel.mainScreenState.value.coolDownTimeFormatted)
        assertEquals(true, viewModel.mainScreenState.value.addToCalendarButtonEnabled)
    }

    @Test
    fun `enter float duration`() {
        val viewModel = createViewModel()
        viewModel.apply(Action.CoolDownTimeChanged("9"))
        viewModel.apply(Action.CoolDownTimeChanged("9."))
        assertEquals("9", viewModel.mainScreenState.value.coolDownTimeFormatted)
    }

    @Test
    fun `enter negative duration`() {
        val viewModel = createViewModel()
        viewModel.apply(Action.CoolDownTimeChanged("9"))
        viewModel.apply(Action.CoolDownTimeChanged("-9"))
        assertEquals("9", viewModel.mainScreenState.value.coolDownTimeFormatted)
    }

    @Test
    fun `change title`() {
        val viewModel = createViewModel()
        viewModel.apply(Action.TitleChanged("test"))
        assertEquals("test", viewModel.mainScreenState.value.title)
    }

    @Test
    fun `empty duration makes button disabled`() {
        val viewModel = createViewModel()
        viewModel.apply(Action.CoolDownTimeChanged(""))
        assertEquals("", viewModel.mainScreenState.value.coolDownTimeFormatted)
        assertEquals(false, viewModel.mainScreenState.value.addToCalendarButtonEnabled)
    }

    @Test
    fun `insert 5 hours cd`() {
        var addedEvent: CalendarEvent? = null
        val viewModel = createViewModel(
            currentTimeProvider = { 3L },
            insertCalendarEvent = { addedEvent = it}
        )
        viewModel.apply(Action.CoolDownTimeChanged("5"))
        viewModel.apply(Action.TitleChanged("test"))
        viewModel.apply(Action.AddToCalendarClicked)

        assertNotNull(addedEvent)
        val event = addedEvent!!
        assertEquals(3, event.begin)
        assertEquals(5*60*60*1000 + 3, event.end)
        assertEquals("test", event.title)
        val latestPill = viewModel.mainScreenState.value.latestPills.first()
        assertEquals(5, latestPill.duration.inWholeHours)
        assertEquals("test", latestPill.title)
    }

    @Test
    fun `added five different events`() {
        var addedEventsCount = 0
        val viewModel = createViewModel(
            insertCalendarEvent = { addedEventsCount++ }
        )
        for (i in 1..5) {
            viewModel.apply(Action.CoolDownTimeChanged("$i"))
            viewModel.apply(Action.TitleChanged("test$i"))
            viewModel.apply(Action.AddToCalendarClicked)
        }

        assertEquals(5, addedEventsCount)
        val latestPills = viewModel.mainScreenState.value.latestPills
        assertEquals(listOf<Long>(5, 4, 3, 2, 1), latestPills.map { it.duration.inWholeHours })
    }

    @Test
    fun `added the same event many times when it's already in list`() {
        val viewModel = createViewModel()

        viewModel.apply(Action.CoolDownTimeChanged("2"))
        viewModel.apply(Action.TitleChanged("test2"))
        viewModel.apply(Action.AddToCalendarClicked)

        viewModel.apply(Action.CoolDownTimeChanged("1"))
        viewModel.apply(Action.TitleChanged("test"))
        viewModel.apply(Action.AddToCalendarClicked)

        for (i in 1..5) {
            viewModel.apply(Action.CoolDownTimeChanged("2"))
            viewModel.apply(Action.TitleChanged("test2"))
            viewModel.apply(Action.AddToCalendarClicked)
        }

        val latestPills = viewModel.mainScreenState.value.latestPills
        assertEquals(listOf<Long>(2, 1), latestPills.map { it.duration.inWholeHours })
    }

    @Test
    fun `selected last actions overrides selected values`() {
        val viewModel = createViewModel()

        viewModel.apply(Action.CoolDownTimeChanged("1"))
        viewModel.apply(Action.TitleChanged("test"))
        viewModel.apply(Action.AddToCalendarClicked)

        viewModel.apply(Action.CoolDownTimeChanged("2"))
        viewModel.apply(Action.TitleChanged("test2"))

        viewModel.apply(Action.LatestPillCoolDownClicked(viewModel.mainScreenState.value.latestPills.first()))

        val state = viewModel.mainScreenState.value
        assertEquals("test", state.title)
        assertEquals("1", state.coolDownTimeFormatted)
    }

    @Test
    fun `screen reviewModels latest pills cool down`() {
        val storage = InMemoryPillsCoolDownStorage()
        val firstviewModel = createViewModel(
            storage = storage
        )

        firstviewModel.apply(Action.CoolDownTimeChanged("1"))
        firstviewModel.apply(Action.TitleChanged("test"))
        firstviewModel.apply(Action.AddToCalendarClicked)
        firstviewModel.apply(Action.CoolDownTimeChanged("1"))
        firstviewModel.apply(Action.TitleChanged("test2"))
        firstviewModel.apply(Action.AddToCalendarClicked)

        val secondviewModel = createViewModel(
            storage = storage
        )

        val state = secondviewModel.mainScreenState.value
        assertEquals(listOf("test2", "test"), state.latestPills.map { it.title })
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun createViewModel(
    currentTimeProvider: () -> Long = { 0L },
    insertCalendarEvent: (CalendarEvent) -> Unit = {},
    storage: PillsCoolDownStorage = InMemoryPillsCoolDownStorage()
) =
    MainScreenViewModel(
        currentTimeProvider,
        insertCalendarEvent,
        storage,
        createTestCoroutineScope(TestCoroutineDispatcher() + TestCoroutineExceptionHandler())
    )