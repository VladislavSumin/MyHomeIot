package ru.vladislavsumin.myhomeiot

import android.content.SharedPreferences
import java.lang.RuntimeException

class SharedPreferencesMock : SharedPreferences {
    private val mData: MutableMap<String, Any> = HashMap()


    override fun contains(key: String): Boolean = mData.contains(key)

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mData[key] as Boolean? ?: defValue
    }

    override fun getInt(key: String, defValue: Int): Int {
        return mData[key] as Int? ?: defValue
    }

    override fun getLong(key: String, defValue: Long): Long {
        return mData[key] as Long? ?: defValue
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return mData[key] as Float? ?: defValue
    }

    override fun getString(key: String, defValue: String?): String? {
        return mData[key] as String? ?: defValue
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAll(): MutableMap<String, *> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun edit(): SharedPreferences.Editor {
        return Editor()
    }

    inner class Editor : SharedPreferences.Editor {
        private val mEditorData: MutableMap<String, Any> = HashMap()

        override fun clear(): SharedPreferences.Editor {
            mData.clear()
            return this
        }

        override fun putLong(key: String, value: Long): SharedPreferences.Editor {
            mEditorData[key] = value
            return this
        }

        override fun putInt(key: String, value: Int): SharedPreferences.Editor {
            mEditorData[key] = value
            return this
        }

        override fun remove(key: String?): SharedPreferences.Editor {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor {
            mEditorData[key] = value
            return this
        }

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?
        ): SharedPreferences.Editor {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun commit(): Boolean {
            apply()
            return true
        }

        override fun putFloat(key: String, value: Float): SharedPreferences.Editor {
            mEditorData[key] = value
            return this
        }

        override fun apply() {
            mEditorData.forEach { (k, v) ->
                mData[k] = v
            }
        }

        override fun putString(key: String, value: String?): SharedPreferences.Editor {
            if (value == null) throw RuntimeException("Mock not check this situaction")
            mEditorData[key] = value
            return this
        }
    }
}