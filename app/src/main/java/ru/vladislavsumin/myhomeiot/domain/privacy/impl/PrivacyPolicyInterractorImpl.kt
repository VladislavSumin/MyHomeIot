package ru.vladislavsumin.myhomeiot.domain.privacy.impl

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor
import ru.vladislavsumin.myhomeiot.strorage.PrivacyPolicyStorage

class PrivacyPolicyInterractorImpl(
    private val privacyPolicyStorage: PrivacyPolicyStorage
) : PrivacyPolicyInterractor {
    private val mPrivacyPolicySubject = BehaviorSubject.createDefault(false)

    init {
        mPrivacyPolicySubject.onNext(isPrivacyPolicyAccepted())
    }

    override fun isPrivacyPolicyAccepted(): Boolean {
        return privacyPolicyStorage.isAccepted()
    }

    override fun acceptPrivacyPolicy() {
        privacyPolicyStorage.setIsAccepted(true)
        mPrivacyPolicySubject.onNext(true)
    }

    override fun observerPrivacyPolicyAccepted(): Observable<Boolean> {
        return mPrivacyPolicySubject
    }
}