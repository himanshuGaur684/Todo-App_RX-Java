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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.himanshu.rxcourse.todoApp.model.local.Todo
import dev.himanshu.rxcourse.todoApp.view.ui.theme.TodoAppTheme
import dev.himanshu.rxcourse.todoApp.viewModel.TodoViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: TodoViewModel by lazy { TodoViewModel(this) }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoAppTheme {
                val list by viewModel.todo.collectAsStateWithLifecycle()
                val state =
                    androidx.compose.material.rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
                val scope = rememberCoroutineScope()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            scope.launch { state.show() }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = null)
                        }
                    }) { innerPadding ->

                    ModalBottomSheetLayout(
                        modifier = Modifier.padding(innerPadding),
                        sheetState = state,
                        sheetContent = {
                            var value by rememberSaveable { mutableStateOf("") }
                            Spacer(Modifier.height(32.dp))
                            TextField(
                                value = value, onValueChange = { value = it },
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth(),
                                onClick = {
                                    scope.launch {
                                        viewModel.upsert(
                                            Todo(title = value, isCompleted = false)
                                        )
                                        state.hide()
                                    }
                                }) {
                                Text("Save")
                            }
                            Spacer(Modifier.height(32.dp))
                        }) {
                        MainContent(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize(),
                            list = list,
                            onCheckChange = { viewModel.upsert(it) },
                            onLongClick = { viewModel.delete(it) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MainContent(
    modifier: Modifier = Modifier, list: List<Todo>,
    onCheckChange: (Todo) -> Unit,
    onLongClick: (Todo) -> Unit
) {
    if (list.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nothing found")
        }
    } else {
        LazyColumn(modifier.fillMaxSize()) {
            items(list) {
                Todo(
                    modifier = Modifier
                        .fillMaxWidth(),
                    item = it,
                    onCheckChange = onCheckChange,
                    onLongClick = onLongClick,
                )
            }
        }
    }
}

@Composable
fun Todo(
    modifier: Modifier = Modifier,
    item: Todo,
    onCheckChange: (Todo) -> Unit,
    onLongClick: (Todo) -> Unit
) {
    var isCompleted by rememberSaveable { mutableStateOf(item.isCompleted) }
    Row(
        modifier
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongClick(item) })
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = isCompleted, onCheckedChange = {
            isCompleted = !isCompleted
            onCheckChange(item.copy(isCompleted = isCompleted))
        })
        Spacer(Modifier.width(12.dp))
        Text(item.title, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(12.dp))
    }
}

