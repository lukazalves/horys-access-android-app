package com.example.klsdinfo.data

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.klsdinfo.data.models.Person2

class SelectPersonViewModel(
    private val semanticRepository: SemanticRepository, application: Application) : AndroidViewModel(application), LifecycleObserver{


    val mPeople = MutableLiveData<List<Person2>>().apply { value = emptyList() }
    val loadingProgress = MutableLiveData<Boolean>().apply { value = true }




    fun fetchPeople(){
        loadingProgress.postValue(true)
        semanticRepository.getAvailablePeople({
            mPeople.postValue(it)
            loadingProgress.postValue(false)

            Log.e("debug", it.toString())

        }, {
            loadingProgress.postValue(false)
//            mPeople.postValue(listOf())
//            loadingProgress.postValue(false)

        })
    }



}