package com.example.composeapp

import android.icu.text.SimpleDateFormat
import android.text.format.DateFormat
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val loginDao: LoginDao
) : ViewModel() {
    enum class StateKey{
        IS_ANDROID,
        LOGIN,
        DRAFT_LOGIN
    }
    val isAndroid : StateFlow<Boolean> = savedStateHandle.getStateFlow(StateKey.IS_ANDROID.name, true)

    private val _inProgress = MutableStateFlow(false)
    val inProgress : StateFlow<Boolean> = _inProgress

    private val _dateText = MutableStateFlow("")
    val dateText : StateFlow<String> = _dateText

    private val _message = MutableStateFlow("")
    val message : StateFlow<String> = _message

    val login : StateFlow<String> = savedStateHandle.getStateFlow(StateKey.LOGIN.name, "")

    val draftLogin : StateFlow<String> = savedStateHandle.getStateFlow(StateKey.DRAFT_LOGIN.name, "")

    private val _repos = MutableStateFlow(listOf<GHRepos>())
    val repos : StateFlow<List<GHRepos>> = _repos

    private val _names = MutableStateFlow(listOf<LoginEntity>())
    val names : StateFlow<List<LoginEntity>> = _names

    init {
        viewModelScope.launch {
            kotlin.runCatching {
                while (true) {
                    _dateText.value = getDataText()
                    delay(1000)
                }
            }.onFailure {
                Log.w(TAG,it)
            }
        }
        viewModelScope.launch {
            kotlin.runCatching {
                loginDao.loadAll().collect {
                    Log.d(TAG, "loadAll ${it.size}")
                    _names.value = it
                }
            }
        }

    }

    private fun getDataText() :String {
        val date = Date()
        val local = Locale.getDefault()
        val pattern = DateFormat.getBestDateTimePattern(local,"yyyyMMddHHmmss")
        return SimpleDateFormat(pattern,local).format(date)
    }


    fun onClick() {
        Log.d(TAG,"MainViewModel onClick")
        savedStateHandle[StateKey.IS_ANDROID.name] = !isAndroid.value
    }
    fun onDismiss() {
        Log.d(TAG,"MainViewModel onDismiss")
        _message.value = ""
    }
    fun onSelect(name: String) {
        Log.d(TAG,"MainViewModel onSelect $name")
        savedStateHandle[StateKey.LOGIN.name] = name
    }

    fun onUpdateDraftLogin(name: String) {
        Log.d(TAG,"MainViewModel onUpdateDraftLogin $name")
        savedStateHandle[StateKey.DRAFT_LOGIN.name] = name
    }

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            isLenient = true
        }
    }
    fun onGet() {
        Log.d(TAG,"MainViewModel onGet")
        if(_inProgress.value) {
            Log.d(TAG,"inProgress")
            return
        } else {
            _inProgress.value = true
        }
        viewModelScope.launch {
            kotlin.runCatching {
                val (request, response, result) = Fuel.get("https://api.github.com/users/${login.value}")
                    .awaitStringResponseResult()
                Log.d(TAG,"request=$request")
                Log.d(TAG,"response=$response")
                Log.d(TAG,"result=$result")
                val text = result.get()
                val user = json.decodeFromString<GHUsers>(text)
                val (_, _, reposResult ) = Fuel.get(user.reposUrl).awaitStringResponseResult()
                val entity = _names.value.firstOrNull { it.login == login.value }
                    ?: LoginEntity(0, login.value, 0)
                loginDao.insert(entity.copy(updateAt = Date().time))
                val reposText = reposResult.get()
                json.decodeFromString<List<GHRepos>>(reposText)
            }.onSuccess {
                _repos.value = it
            }.onFailure {
                Log.e(TAG,"onFailureですよ$it")
                _message.value = it.message.toString()
            }.also {
                _inProgress.value = false
            }
        }
    }
}