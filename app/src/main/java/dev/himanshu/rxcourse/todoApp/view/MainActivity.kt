package dev.himanshu.rxcourse.todoApp.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.himanshu.rxcourse.todoApp.model.local.Todo
import dev.himanshu.rxcourse.todoApp.view.ui.theme.TodoAppTheme
import dev.himanshu.rxcourse.todoApp.viewModel.TodoViewModel
import dev.himanshu.rxcourse.todoApp.viewModel.UiState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: TodoViewModel by lazy {
        TodoViewModel(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val sheetState =
                    rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
                val scope = rememberCoroutineScope()
                var value by rememberSaveable { mutableStateOf("") }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            scope.launch {
                                sheetState.show()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        }
                    }) { innerPadding ->


                    ModalBottomSheetLayout(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        sheetContent = {

                            TextField(value = value, onValueChange = {
                                value = it
                            })
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = {
                                viewModel.upsert(
                                    Todo(
                                        title = value,
                                        isCompleted = false
                                    )
                                )
                                scope.launch { sheetState.hide() }
                            }) {
                                Text("Add new todo", color = Color.White)
                            }

                        }, sheetState = sheetState
                    ) {
                        MainContent(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            uiState = uiState,
                            onUpdateClick = {
                                viewModel.upsert(it)
                            },
                            onDeleteClick = {
                                viewModel.delete(it)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainContent(
    modifier: Modifier = Modifier, uiState: UiState,
    onUpdateClick: (Todo) -> Unit,
    onDeleteClick: (Todo) -> Unit
) {

    if (uiState.isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }

    if (uiState.error.isNotBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.error)
        }
    }

    uiState.list?.let { list ->
        if (list.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nothing found")
            }
        } else {
            LazyColumn(modifier.fillMaxSize()) {
                items(list) {
                    dev.himanshu.rxcourse.todoApp.view.Todo(
                        modifier = Modifier
                            .padding(
                                horizontal = 12.dp,
                                vertical = 8.dp
                            )
                            .fillMaxWidth(),
                        item = it,
                        onClick = onUpdateClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }

}

@Composable
fun Todo(
    modifier: Modifier = Modifier, item: Todo, onClick: (Todo) -> Unit,
    onDeleteClick: (Todo) -> Unit
) {
    var checked by rememberSaveable { mutableStateOf(item.isCompleted) }
    Row(
        modifier
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = {
                    onDeleteClick(item)
                })
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Checkbox(checked = checked, onCheckedChange = {
            val todo = item.copy(
                isCompleted = item.isCompleted.not()
            )
            checked = todo.isCompleted
            onClick(todo)
        })
        Spacer(Modifier.width(12.dp))
        Text(text = item.title)
    }

}