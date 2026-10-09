package top.kagg886.pmf.ui.route.main.setting

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.sp
import com.alorma.compose.settings.ui.SettingsSlider
import com.alorma.compose.settings.ui.SettingsSwitch
import io.ktor.http.Url
import korlibs.io.net.MimeType
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import org.koin.ext.getFullName
import org.koin.mp.KoinPlatform.getKoin
import top.kagg886.pmf.LocalColorScheme
import top.kagg886.pmf.LocalDarkSettings
import top.kagg886.pmf.LocalNavBackStack
import top.kagg886.pmf.LocalSnackBarHost
import top.kagg886.pmf.backend.AppConfig
import top.kagg886.pmf.backend.AppConfig.DetailSlideOpenFor
import top.kagg886.pmf.backend.Platform
import top.kagg886.pmf.backend.cachePath
import top.kagg886.pmf.backend.currentPlatform
import top.kagg886.pmf.backend.pixiv.PixivConfig
import top.kagg886.pmf.res.*
import top.kagg886.pmf.shareFile
import top.kagg886.pmf.ui.component.settings.SettingsDropdownMenu
import top.kagg886.pmf.ui.component.settings.SettingsFileUpload
import top.kagg886.pmf.ui.component.settings.SettingsGroup
import top.kagg886.pmf.ui.component.settings.SettingsMenuLink
import top.kagg886.pmf.ui.component.settings.SettingsTextField
import top.kagg886.pmf.ui.route.login.v2.LoginRoute
import top.kagg886.pmf.ui.route.main.about.AboutRoute
import top.kagg886.pmf.ui.route.main.download.DownloadScreenModel
import top.kagg886.pmf.ui.route.main.setting.filter.SettingFilterRoute
import top.kagg886.pmf.ui.util.UpdateCheckViewModel
import top.kagg886.pmf.ui.util.globalViewModel
import top.kagg886.pmf.ui.util.removeLastOrNullWorkaround
import top.kagg886.pmf.ui.util.useWideScreenMode
import top.kagg886.pmf.ui.util.withLink
import top.kagg886.pmf.util.ComposeI18N
import top.kagg886.pmf.util.SerializedTheme
import top.kagg886.pmf.util.b
import top.kagg886.pmf.util.deleteRecursively
import top.kagg886.pmf.util.getString
import top.kagg886.pmf.util.mb
import top.kagg886.pmf.util.setText
import top.kagg886.pmf.util.stringResource
import top.kagg886.pmf.util.zip

@Composable
fun SettingScreen() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        if (useWideScreenMode) {
            TopAppBar(
                title = {
                    Text(stringResource(Res.string.settings))
                },
            )
        }
        SettingsGroup(title = { Text(stringResource(Res.string.login_sessions)) }) {
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

            SettingsMenuLink(
                title = {
                    Text(stringResource(Res.string.export_login_session))
                },
                subtitle = {
                    Text(stringResource(Res.string.export_login_session_description))
                },
                onClick = {
                    scope.launch {
                        clip.setText(refreshToken)
                        snack.showSnackbar(getString(Res.string.login_session_copied))
                    }
                },
            )

            var show by remember { mutableStateOf(false) }
            if (show) {
                val stack = LocalNavBackStack.current
                AlertDialog(
                    onDismissRequest = {
                        stack.removeLastOrNullWorkaround()
                    },
                    title = {
                        Text(stringResource(Res.string.confirm_logout))
                    },
                    text = {
                        Text(stringResource(Res.string.confirm_logout_description))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                Snapshot.withMutableSnapshot {
                                    stack.clear()
                                    stack += LoginRoute(true)
                                }
                            },
                        ) {
                            Text(stringResource(Res.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                show = false
                            },
                        ) {
                            Text(stringResource(Res.string.cancel))
                        }
                    },
                )
            }
            SettingsMenuLink(
                title = {
                    Text(stringResource(Res.string.logout))
                },
                subtitle = {
                    Text(stringResource(Res.string.logout_description))
                },
                onClick = {
                    show = true
                },
            )
        }
    }
}

expect suspend fun getDownloadRootPath(): String?
