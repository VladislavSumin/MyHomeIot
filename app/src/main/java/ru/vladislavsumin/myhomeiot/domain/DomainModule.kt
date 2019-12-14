package ru.vladislavsumin.myhomeiot.domain

import dagger.Module
import dagger.Provides
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.domain.privacy.impl.PrivacyPolicyInterractorImpl
import ru.vladislavsumin.myhomeiot.strorage.PrivacyPolicyStorage
import javax.inject.Singleton

@Module
class DomainModule {
    @Provides
    @Singleton
    fun providePrivacyPolicyInterractor(
        privacyPolicyStorage: PrivacyPolicyStorage
    ): PrivacyPolicyInterractor {
        return PrivacyPolicyInterractorImpl(privacyPolicyStorage)
    }
}