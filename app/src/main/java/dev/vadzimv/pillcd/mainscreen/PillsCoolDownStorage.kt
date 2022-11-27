package dev.vadzimv.pillcd.mainscreen

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
    object Empty: RestoreResult
}

class InMemoryPillsCoolDownStorage() : PillsCoolDownStorage {

    private var values: List<PillCoolDown> ? = null

    override suspend fun save(values: List<PillCoolDown>): SaveResult {
        this.values = values
        return SaveResult.Success
    }

    override suspend fun restore(): RestoreResult {
        return values?.let { RestoreResult.Success(it) } ?: RestoreResult.Empty
    }
}