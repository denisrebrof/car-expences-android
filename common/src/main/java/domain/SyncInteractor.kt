package domain

import io.reactivex.Flowable
import javax.inject.Inject

interface ISyncRepository {
    fun getSyncState(): Flowable<SyncState>
}

class SyncInteractor @Inject constructor(private val repository: ISyncRepository) {
    fun getSyncState(): Flowable<SyncState> = repository.getSyncState()
}

sealed class SyncState {
    object PendingSync : SyncState()
    data class InProgress(val percent: Int) : SyncState()
    object Completed : SyncState()
}