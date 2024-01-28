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
            HomeScreen(viewModel,navController)
        }
        composable(Route.COMM.name) {
            CommScreen(viewModel,navController)
        }
        composable(Route.INPUT.name) {
            InputScreen(viewModel,navController)
        }
        composable(Route.REPOS.name) {
            ReposScreen(viewModel)
        }
    }
}

@Composable
fun HomeScreen(
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController = rememberNavController()
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        val isAndroid = viewModel.isAndroid.collectAsState()

        val dateText = viewModel.dateText.collectAsState()
        if (isAndroid.value) {
            Greeting(name = "Android")
        } else {
            Greeting(name = "iPhone")
        }
        Button(onClick = {
            viewModel.onClick()
        }) {
            Text(text = "Push me!!")
        }
        Text(text = dateText.value)
        
        Button(onClick = { navController.navigate(Route.COMM.name)
        }) {
            Text(text = "Navigate CommScreen")
        }
    }
}

@Composable
fun CommScreen(
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController = rememberNavController()
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth()) {
        Greeting(name = "CommScreen")
        val login = viewModel.login.collectAsState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.onUpdateDraftLogin(login.value)
                    navController.navigate(Route.INPUT.name)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = login.value)
            Button(
                onClick = {
                    viewModel.onGet()
                    navController.navigate(Route.REPOS.name)
                }
            ) {
                Text(text = "onGet!!")
            }
        }
        Divider()
        val names = viewModel.names.collectAsState()
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)){
            items(names.value) {
                Row(
                    modifier = Modifier
                        .clickable { viewModel.onSelect(it.login) }
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
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    navController: NavController = rememberNavController()
) {
    val draftLogin = viewModel.draftLogin.collectAsState()
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {
        TextField(
            value = draftLogin.value,
            onValueChange = {
                if (it.length < 20) {
                    viewModel.onUpdateDraftLogin(it)
                }
            },
            label = { Text(text = "user") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                viewModel.onSelect(draftLogin.value)
                navController.popBackStack()
            } ),
            singleLine = true
        )
    }
}

@Composable
fun ReposScreen(viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()){
    Column(modifier = Modifier
        .fillMaxWidth()) {
        val login = viewModel.login.collectAsState()
        val repos = viewModel.repos.collectAsState()
        Text(
            text = "user = ${login.value} size = ${repos.value.size}",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Divider()
        LazyColumn{
            items(repos.value) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = it.name, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    Text(text = it.updatedAt, fontSize = 8.sp, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
    val inProgress = viewModel.inProgress.collectAsState()
    if (inProgress.value) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
    val message = viewModel.message.collectAsState()
    if (message.value.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismiss() },
            buttons = {},
            text = { Text(text = message.value) })
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
       HomeScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CommPreview() {
    ComposeAppTheme {
        CommScreen()
    }
}
@Preview(showBackground = true)
@Composable
fun InputPreview() {
    ComposeAppTheme {
        InputScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ReposPreview() {
    ComposeAppTheme {
        ReposScreen()
    }
}