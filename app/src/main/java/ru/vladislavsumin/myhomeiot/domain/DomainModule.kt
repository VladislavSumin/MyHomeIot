package ru.vladislavsumin.myhomeiot.domain

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.vladislavsumin.myhomeiot.domain.firebase.FirebaseInterractor
import ru.vladislavsumin.myhomeiot.domain.firebase.impl.FirebaseInterractorImpl
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

    @Provides
    @Singleton
    fun provideFirebaseInterractor(
        context: Context,
        privacyPolicyInterractor: PrivacyPolicyInterractor
    ): FirebaseInterractor {
        return FirebaseInterractorImpl(context, privacyPolicyInterractor)
    }

}