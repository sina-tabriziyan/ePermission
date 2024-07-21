package ir.sina.epermission

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import ir.sina.epermission.ui.theme.EPermissionTheme

class MainActivity : ComponentActivity() {
    private val viewmodel by viewModels<MainViewModel>()

    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EPermissionTheme {
                val showDialog = viewmodel.showDialog.collectAsState().value
                val launchAppSettings = viewmodel.launchAppSetting.collectAsState().value
                val permissionsResultsActivityLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions(),
                    onResult = { result ->
                        permissions.forEach { permission ->
                            if (result[permission] == false) {
                                if (!shouldShowRequestPermissionRationale(permission)) {
                                    viewmodel.updateLaunchAppSetting(true)
                                }
                                viewmodel.updateShowDialog(true)
                            }
                        }
                    })
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Button(onClick = {
                        permissions.forEach { permission ->
                            val isGranted =
                                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
                            if (!isGranted) {
                                if (shouldShowRequestPermissionRationale(permission)) {
                                    viewmodel.updateShowDialog(true)
                                } else {
                                    permissionsResultsActivityLauncher.launch(permissions)
                                }
                            }
                        }
                    }) {
                        Text(text = "Reuest Permission")
                    }
                }

                if (showDialog) {
                    PermissionDialog(
                        onDismiss = {
                            viewmodel.updateShowDialog(false)
                        }, onConfirm = {
                            viewmodel.updateShowDialog(false)
                            if (launchAppSettings) {
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                ).also { startActivity(it) }
                                viewmodel.updateLaunchAppSetting(false)
                            } else {
                                permissionsResultsActivityLauncher.launch(permissions)
                            }
                        })
                }
            }
        }
    }
}

@Composable
fun PermissionDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {

    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        Button(onClick = onConfirm) {
            Text(text = "OK")
        }
    }, title = {
        Text(
            text = "Camera and Microphone permissions are needed",
            fontWeight = FontWeight.SemiBold
        )
    }, text = {
        Text(text = "This app needs access this permissions")
    })
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EPermissionTheme {
        Greeting("Android")
    }
}