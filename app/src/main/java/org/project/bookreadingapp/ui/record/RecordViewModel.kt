package org.project.bookreadingapp.ui.record

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecordViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Record and synthesize voice"
    }
    val text: LiveData<String> = _text
}