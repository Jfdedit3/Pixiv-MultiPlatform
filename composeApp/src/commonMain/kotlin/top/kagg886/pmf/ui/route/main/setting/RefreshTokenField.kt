package top.kagg886.pmf.ui.route.main.setting

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import top.kagg886.pmf.LocalSnackBarHost
import top.kagg886.pmf.backend.pixiv.PixivConfig
import top.kagg886.pmf.res.*
import top.kagg886.pmf.util.getString
import top.kagg886.pmf.util.setText
import top.kagg886.pmf.util.stringResource

@Composable
fun RefreshTokenField() {
    val clip = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val snack = LocalSnackBarHost.current
    val refreshToken = PixivConfig.refreshToken

    OutlinedTextField(
        value = refreshToken,
        onValueChange = {},
        readOnly = true,
        singleLine = false,
        label = { Text(stringResource(Res.string.login_session)) },
        supportingText = {
            Text(stringResource(Res.string.export_login_session_description))
        },
        trailingIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        clip.setText(refreshToken)
                        snack.showSnackbar(getString(Res.string.login_session_copied))
                    }
                },
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(Res.string.export_login_session),
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}
