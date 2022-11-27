package dev.vadzimv.pillcd.mainscreen

import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class PillsCoolDownStorageTest {
    @Test
    fun `serialize empty list`() {
        val testList = emptyList<PillCoolDown>()
        val serialized = serialize(testList)
        val deserialized = deserialize(serialized)
        assertEquals(testList, deserialized)
    }

    @Test
    fun `serialize two elements list`() {
        val testList = listOf(
            createPillCoolDown(6, "test1"),
            createPillCoolDown(99, "test99")
        )
        val serialized = serialize(testList)
        val deserialized = deserialize(serialized)
        assertEquals(testList, deserialized)
    }

    @Test
    @Ignore("TODO: fix me")
    fun `serialize elements with special character`() {
        val testList = listOf(
            createPillCoolDown(6, "test1"),
            createPillCoolDown(99, "tes;t99")
        )
        val serialized = serialize(testList)
        val deserialized = deserialize(serialized)
        assertEquals(testList, deserialized)
    }
}

private fun createPillCoolDown(
    duration: Long = 6,
    title: String = "test"
) = PillCoolDown(
    duration = duration.toDuration(DurationUnit.MILLISECONDS),
    title = title
)