package com.example.attendance.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.attendance.database.Subject
import com.example.attendance.ui.theme.mediumRoundedCornerShape
import com.example.attendance.viewModel.AttendanceViewModel

@Composable
fun AddSubjectButton(
    viewModel: AttendanceViewModel
) {
    var showAddSubjectDialog by remember {mutableStateOf(false)}

    AddSubjectDialog(
        showDialog = showAddSubjectDialog,
        addSubject = {
            viewModel.addSubject(Subject(name = it))
        },
        hideDialog = {
            showAddSubjectDialog = false
        }
    )

    FilledTonalIconButton(
        modifier = Modifier
            .size(75.dp),
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ) ,
        onClick = {
            showAddSubjectDialog = true
        }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "add subject"
        )
    }
}

@Composable
fun AddSubjectDialog(
    showDialog: Boolean,
    addSubject: (String) -> Unit,
    hideDialog: () -> Unit,
) {
    if (!showDialog) {
        return
    }

    var subjectName by remember {
        mutableStateOf("")
    }

    val internalAddSubject = {
        val name = subjectName
        subjectName = ""
        if (name.isNotBlank()) {
            addSubject(name.trim())
            hideDialog()
        }
    }

    Dialog(
        onDismissRequest = hideDialog,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .padding(10.dp),
            shape = mediumRoundedCornerShape
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add Subject",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = CircleShape,
                    value = subjectName,
                    onValueChange = {
                        subjectName = it
                    },
                    label = {
                        Text(text = "Add new subject")
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions { internalAddSubject() }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    FilledTonalButton(
                        onClick = hideDialog
                    ) {
                        Text(text = "Cancel")
                    }

                    FilledTonalButton(
                        onClick = internalAddSubject
                    ) {
                        Text(text = "Add")
                    }
                }
            }
        }
    }
}