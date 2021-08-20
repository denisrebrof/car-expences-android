package di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import data.SyncRepositoryImpl
import domain.ISyncRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class SyncRepositoryModule {
    @Binds
    abstract fun bindRepository(impl: SyncRepositoryImpl): ISyncRepository
}