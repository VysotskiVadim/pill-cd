package dev.vadzimv.pillcd.mainscreen

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration

interface PillsCoolDownStorage {
    suspend fun save(values: List<PillCoolDown>): SaveResult
    suspend fun restore(): RestoreResult
}

sealed interface SaveResult {
    object Success : SaveResult
    object Failure : SaveResult
}

sealed interface RestoreResult {
    data class Success(val values: List<PillCoolDown>) : RestoreResult
    object Failure: RestoreResult
}

class InMemoryPillsCoolDownStorage() : PillsCoolDownStorage {

    private var values: List<PillCoolDown> ? = null

    override suspend fun save(values: List<PillCoolDown>): SaveResult {
        this.values = values
        return SaveResult.Success
    }

    override suspend fun restore(): RestoreResult {
        return RestoreResult.Success(values.orEmpty())
    }
}

class SharedPreferencesCoolDownStorage(context: Context): PillsCoolDownStorage {

    private val sharedPreferences = context.getSharedPreferences("pills-cool-down", Context.MODE_PRIVATE)

    override suspend fun save(values: List<PillCoolDown>): SaveResult {
        return withContext(Dispatchers.IO) {
            try {
                val result = sharedPreferences.edit()
                    .putString("pills", serialize(values))
                    .commit()
                if (result) SaveResult.Success else SaveResult.Failure
            } catch (t: Throwable) {
                SaveResult.Failure
            }
        }
    }

    override suspend fun restore(): RestoreResult {
        return withContext(Dispatchers.IO) {
            try {
                val result = sharedPreferences.getString("pills", "")!!
                RestoreResult.Success(deserialize(result))
            } catch (t: Throwable) {
                RestoreResult.Failure
            }
        }
    }

}

fun serialize(values: List<PillCoolDown>): String {
    return values.joinToString(separator = ";") { "${it.duration},${it.title}" }
}

fun deserialize(serializesValues: String): List<PillCoolDown> {
    if (serializesValues.isBlank()) return emptyList()
    return serializesValues.split(";").map {
        val items = it.split(",")
        PillCoolDown(
            duration = Duration.parse(items[0]),
            title = items[1],
        )
    }
}