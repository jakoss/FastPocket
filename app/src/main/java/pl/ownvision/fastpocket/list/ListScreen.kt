package pl.ownvision.fastpocket.list

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.airbnb.mvrx.*
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import pl.ownvision.fastpocket.R
import pl.ownvision.fastpocket.api.models.PocketItemDto
import pl.ownvision.fastpocket.authentication.AuthenticationActivity
import pl.ownvision.fastpocket.infrastructure.ui.theme.FastPocketTheme
import pl.ownvision.fastpocket.infrastructure.ui.utility.FullscreenLoader

@Composable
fun ListScreen(navController: NavController) {
    val viewModel: PocketItemsListViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    FastPocketTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Fast Pocket") },
                    actions = {
                        IconButton(onClick = {
                            navController.navigate("settings")
                        }) {
                            Icon(imageVector = Icons.Filled.Settings, contentDescription = null)
                        }
                    }
                )
            },
            content = {
                when (val accessToken = state.accessToken) {
                    Uninitialized, is Loading -> {
                        FullscreenLoader()
                    }
                    is Success -> {
                        if (state.userAuthorized) {
                            PocketItemsScreen(
                                state.items,
                                reloadItems = {
                                    viewModel.loadPocketItems()
                                },
                                archiveItemAction = { pocketItem ->
                                    viewModel.archivePocketItem(pocketItem)
                                },
                                state.useExternalBrowser
                            )
                        } else {
                            LoginScreen()
                        }
                    }
                    is Fail -> {
                        Text("Error: ${accessToken.error}")
                    }
                }

            }
        )
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            context.startActivity(Intent(context, AuthenticationActivity::class.java))
        }) {
            Text(text = "Log in")
        }
    }
}

@Composable
fun PocketItemsScreen(
    pocketItems: Async<List<PocketItemDto>>,
    reloadItems: () -> Unit,
    archiveItemAction: (PocketItemDto) -> Unit,
    useExternalBrowser: Boolean
) {
    val currentPocketItems = pocketItems()
    when {
        currentPocketItems != null -> {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = pocketItems is Loading),
                onRefresh = {
                    reloadItems()
                }) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    itemsIndexed(currentPocketItems) { index, item ->
                        PocketItem(
                            pocketItem = item,
                            useExternalBrowser = useExternalBrowser,
                            archiveItemAction = archiveItemAction
                        )
                        if (index < currentPocketItems.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        }
        pocketItems is Uninitialized || pocketItems is Loading -> {
            FullscreenLoader()
        }
        pocketItems is Fail -> {
            Text("Error: ${pocketItems.error}")
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun PocketItem(
    pocketItem: PocketItemDto = PocketItemDto(
        "", "", "", "", "", "Test Title", false, null, "Very long except text", 3
    ),
    useExternalBrowser: Boolean = false,
    archiveItemAction: (PocketItemDto) -> Unit = {}
) {
    val context = LocalContext.current
    val title = if (pocketItem.resolvedTitle.isNotBlank()) {
        pocketItem.resolvedTitle
    } else {
        pocketItem.givenTitle
    }

    SwipeHandler(pocketItem = pocketItem, archiveItemAction = archiveItemAction) {
        ListItem(
            text = { Text(text = title) },
            secondaryText = pocketItem.excerpt?.let {
                { Text(text = pocketItem.excerpt, maxLines = 2, overflow = TextOverflow.Ellipsis) }
            },
            overlineText = pocketItem.timeToRead?.let {
                val minutes = pocketItem.timeToRead
                {
                    Text(
                        text = context.resources.getQuantityString(
                            R.plurals.minutes,
                            minutes,
                            minutes
                        )
                    )
                }
            },
            icon = pocketItem.topImageUrl?.let {
                {
                    Image(
                        painter = rememberCoilPainter(request = it),
                        contentDescription = null,
                        Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            },
            modifier = Modifier
                .clickable {
                    if (useExternalBrowser) {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                pocketItem.givenUrl.toUri()
                            )
                        )
                    } else {
                        val customTabsIntent = CustomTabsIntent
                            .Builder()
                            .build()
                        customTabsIntent.launchUrl(context, pocketItem.givenUrl.toUri())
                    }
                }
                .padding(vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeHandler(
    pocketItem: PocketItemDto,
    archiveItemAction: (PocketItemDto) -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                archiveItemAction(pocketItem)
                // TODO : move action methods up the stack
            } else if (it == DismissValue.DismissedToEnd) {
                // TODO : handle favorite change
            }
            false
        }
    )
    SwipeToDismiss(
        state = dismissState,
        modifier = Modifier.padding(vertical = 4.dp),
        dismissThresholds = {
            FractionalThreshold(0.25f)
        },
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    DismissValue.DismissedToEnd -> Color.Green
                    DismissValue.DismissedToStart -> Color.Red
                    DismissValue.Default -> Color.Transparent
                }
            )
            val alignment = when (direction) {
                DismissDirection.StartToEnd -> Alignment.CenterStart
                DismissDirection.EndToStart -> Alignment.CenterEnd
            }
            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Star
                DismissDirection.EndToStart -> Icons.Default.Delete
            }
            val scale by animateFloatAsState(
                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
            )

            Box(
                Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    icon,
                    contentDescription = null, // TODO : localise content description
                    modifier = Modifier.scale(scale)
                )
            }
        },
        dismissContent = content
    )
}