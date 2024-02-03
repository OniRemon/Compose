package com.example.composeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.ui.theme.ComposeAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.log


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

}

enum class Route {
    HOME,
    COMM,
    REPOS,
    INPUT,
}
const val TAG = "タグ"
@Composable
fun MainScreen(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Route.HOME.name ) {
        composable(Route.HOME.name) {
            val isAndroid = viewModel.isAndroid.collectAsState()
            val dateText = viewModel.dateText.collectAsState()
            HomeScreen(
                isAndroid = isAndroid.value,
                dateText = dateText.value,
                onClick = { viewModel.onClick()},
                onNavigate = { navController.navigate(Route.COMM.name)}
            )
        }
        composable(Route.COMM.name) {
            val login = viewModel.login.collectAsState()
            val names = viewModel.names.collectAsState()
            CommScreen(
                login = login.value,
                names = names.value,
                onUpdateDraftLogin = {viewModel.onUpdateDraftLogin(login.value)},
                onGet = {viewModel.onGet()},
                onSelect = {viewModel.onSelect(it)},
                onNavigateToInput = {navController.navigate(Route.INPUT.name)},
                onNavigateToRepos = {navController.navigate(Route.REPOS.name)}
            )
        }
        composable(Route.INPUT.name) {
            val draftLogin = viewModel.draftLogin.collectAsState()
            InputScreen(
                draftLogin = draftLogin.value,
                onSelect = { viewModel.onSelect(draftLogin.value) },
                onUpdateDraftLogin = {viewModel.onUpdateDraftLogin(it)},
                onPopBackStack = {navController.popBackStack()}
            )
        }
        composable(Route.REPOS.name) {
            val login = viewModel.login.collectAsState()
            val repos = viewModel.repos.collectAsState()
            val inProgress = viewModel.inProgress.collectAsState()
            val message = viewModel.message.collectAsState()
            ReposScreen(
                login = login.value,
                repos = repos.value,
                inProgress = inProgress.value,
                message = message.value,
                onDismiss = {viewModel.onDismiss()}
            )
        }
    }
}

@Composable
fun HomeScreen(
    isAndroid: Boolean,
    dateText: String,
    onClick: () -> Unit,
    onNavigate: () -> Unit,
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {

        if (isAndroid) {
            Greeting(name = "Android")
        } else {
            Greeting(name = "iPhone")
        }
        Button(
            onClick =  onClick
        ) {
            Text(text = "Push me!!")
        }
        Text(text = dateText)
        
        Button(
            onClick =  onNavigate
        ) {
            Text(text = "Navigate CommScreen")
        }
    }
}

@Composable
fun CommScreen(
    login: String,
    names: List<LoginEntity>,
    onUpdateDraftLogin: () -> Unit,
    onGet: () -> Unit,
    onSelect: (String) -> Unit,
    onNavigateToInput: () -> Unit,
    onNavigateToRepos: () -> Unit,
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Greeting(name = "CommScreen")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onUpdateDraftLogin()
                    onNavigateToInput()
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = login)
            Button(
                onClick = {
                    onGet()
                    onNavigateToRepos()
                }
            ) {
                Text(text = "onGet!!")
            }
        }
        Divider()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)){
            items(names) {
                Row(
                    modifier = Modifier
                        .clickable { onSelect(it.login) }
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                    Text(text = it.login)
                    Text(text = it.id.toString())
                    Text(text = it.updateAt.toString())
                }
            }
        }
    }
}

@Composable
fun InputScreen(
    draftLogin: String,
    onSelect: () -> Unit,
    onUpdateDraftLogin: (String) -> Unit,
    onPopBackStack: () -> Unit
) {

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        TextField(
            value = draftLogin,
            onValueChange = {
                if (it.length < 20) {
                    onUpdateDraftLogin(it)
                }
            },
            label = { Text(text = "user") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                onSelect()
                onPopBackStack()
            } ),
            singleLine = true
        )
    }
}

@Composable
fun ReposScreen(
    login: String,
    repos: List<GHRepos>,
    inProgress: Boolean,
    message: String,
    onDismiss: () -> Unit,
){
    Column(modifier = Modifier
        .fillMaxWidth()) {
        Text(
            text = "user = $login size = ${repos.size}",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Divider()
        LazyColumn{
            items(repos) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = it.name, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Text(text = it.updatedAt, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
    if (inProgress) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    if (message.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            buttons = {},
            text = { Text(text = message) })
    }
}
@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    ComposeAppTheme {
       HomeScreen(isAndroid = true, dateText = "2024/2/4", onClick = {}, onNavigate = {})
    }
}

@Preview(showBackground = true)
@Composable
fun CommPreview() {
    ComposeAppTheme {
        CommScreen(
            login = "a",
            names = emptyList(),
            onUpdateDraftLogin = {},
            onGet = {},
            onSelect = {},
            onNavigateToRepos = {},
            onNavigateToInput = {}
        )
    }
}
@Preview(showBackground = true)
@Composable
fun InputPreview() {
    ComposeAppTheme {
        InputScreen(
            draftLogin = "test",
            onSelect = {},
            onUpdateDraftLogin = {},
            onPopBackStack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReposPreview() {
    ComposeAppTheme {
        ReposScreen(
            login = "login",
            repos = emptyList(),
            inProgress = true,
            message = "message",
            onDismiss = {}
        )
    }
}