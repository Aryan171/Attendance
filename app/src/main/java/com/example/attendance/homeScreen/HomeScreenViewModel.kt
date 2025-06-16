package com.example.attendance.homeScreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class HomeScreenViewModel : ViewModel() {
    private var _text = MutableStateFlow("")
    val text = _text

    private var _subjectList =
}