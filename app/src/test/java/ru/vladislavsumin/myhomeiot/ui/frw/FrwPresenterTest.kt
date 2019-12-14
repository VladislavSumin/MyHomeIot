package ru.vladislavsumin.myhomeiot.ui.frw

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.Mockito.`when` as when_
import org.mockito.junit.MockitoJUnitRunner
import ru.vladislavsumin.myhomeiot.app.AppComponent
import ru.vladislavsumin.myhomeiot.app.Injector
import ru.vladislavsumin.myhomeiot.domain.privacy.PrivacyPolicyInterractor

@RunWith(MockitoJUnitRunner::class)
class FrwPresenterTest {
    @Mock
    private lateinit var mAppComponent: AppComponent
    @Mock
    private lateinit var mPrivacyPolicyInterractor: PrivacyPolicyInterractor
    @Mock
    private lateinit var mViewState: `FrwView$$State`

    private lateinit var mPresenter: FrwPresenter

    @Before
    fun before() {
        when_(mAppComponent.inject(anyObject<FrwPresenter>()))
            .then {
                val frwPresenter = it.arguments[0] as FrwPresenter
                frwPresenter.mPrivacyPolicyInterractor = mPrivacyPolicyInterractor
                Unit
            }
        Injector = mAppComponent

        mPresenter = FrwPresenter()
        mPresenter.attachView(mViewState)
    }

    @Test
    fun `onClickPrivacyPolicyCheckbox set true`() {
        mPresenter.onClickPrivacyPolicyCheckbox(true)
        verify(mViewState, times(1)).setNextButtonEnabled(true)
    }

    @Test
    fun `onClickPrivacyPolicyCheckbox set false`() {
        mPresenter.onClickPrivacyPolicyCheckbox(false)
        verify(mViewState, times(1)).setNextButtonEnabled(false)
    }

    @Test
    fun onClickNextButton() {
        mPresenter.onClickPrivacyPolicyCheckbox(true)
        mPresenter.onClickNextButton()

        verify(mViewState, times(1)).goToMainScreen()
        verify(mPrivacyPolicyInterractor, times(1)).acceptPrivacyPolicy()
    }

    @Test
    fun `onClickNextButton before accept privacy policy`() {
        mPresenter.onClickNextButton()

        verify(mViewState, times(0)).goToMainScreen()
        verify(mPrivacyPolicyInterractor, times(0)).acceptPrivacyPolicy()
    }

    private fun <T> anyObject(): T {
        @Suppress("DEPRECATION")
        Mockito.anyObject<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}