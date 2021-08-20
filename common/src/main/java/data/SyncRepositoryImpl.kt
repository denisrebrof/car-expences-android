package data

import domain.ISyncRepository
import domain.SyncState
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.realm.kotlin.syncSession
import io.realm.mongodb.sync.ProgressListener
import io.realm.mongodb.sync.ProgressMode
import javax.inject.Inject

class SyncRepositoryImpl @Inject constructor(
    private val realmProvider: SyncedRealmProvider
) : ISyncRepository {
    override fun getSyncState(): Flowable<SyncState> {
        val session = realmProvider.getRealmInstance().syncSession
        return Observable.create<SyncState> { emitter ->
            val listener = ProgressListener { progress ->
                when {
                    progress.isTransferComplete -> SyncState.Completed
                    progress.fractionTransferred <= 0 -> SyncState.PendingSync
                    else -> (progress.fractionTransferred * 100).toInt().let(SyncState::InProgress)
                }.let(emitter::onNext)
            }.also {
                session.addDownloadProgressListener(ProgressMode.INDEFINITELY, it)
            }
            emitter.setCancellable {
                session.removeProgressListener(listener)
            }
        }.toFlowable(BackpressureStrategy.LATEST)
    }
}