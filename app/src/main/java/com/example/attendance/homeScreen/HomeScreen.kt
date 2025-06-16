package com.example.attendance.homeScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.attendance.database.Subject
import kotlinx.serialization.Serializable
import androidx.compose.runtime.getValue

@Serializable
object HomeScreen

@Preview
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel()
) {
    val subjectList: List<Subject> by viewModel.subjectList.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {HomeScreenTopBar()}
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            item {
                SubjectCard(Subject(20, "haha"))
            }

            items(count = subjectList.size) {
                SubjectCard(subjectList[it])
            }
        }
    }
}

@Composable
fun HomeScreenTopBar() {

}

@Composable
fun SubjectCard(subject: Subject) {
    Text(subject.subjectName)
}