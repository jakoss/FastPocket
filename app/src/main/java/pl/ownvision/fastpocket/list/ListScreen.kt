package pl.ownvision.fastpocket.list

import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.airbnb.mvrx.*
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.coil.rememberCoilPainter
import pl.ownvision.fastpocket.R
import pl.ownvision.fastpocket.api.models.PocketItemDto
import pl.ownvision.fastpocket.authentication.AuthenticationActivity
import pl.ownvision.fastpocket.infrastructure.ui.theme.FastPocketTheme
import pl.ownvision.fastpocket.infrastructure.ui.utility.FullscreenLoader

@Composable
fun ListScreen() {
    val viewModel: PocketItemsListViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()
    FastPocketTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Fast Pocket") }
                )
            },
            content = {
                when (val accessToken = state.accessToken) {
                    Uninitialized, is Loading -> {
                        FullscreenLoader()
                    }
                    is Success -> {
                        if (state.userAuthorized) {
                            PocketItemsScreen(state.items)
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
fun PocketItemsScreen(pocketItems: Async<List<PocketItemDto>>) {
    when (pocketItems) {
        Uninitialized, is Loading -> {
            FullscreenLoader()
        }
        is Success -> {
            val items = pocketItems()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(items) { index, item ->
                    PocketItem(item)
                    if (index < items.size - 1) {
                        Divider()
                    }
                }
            }
        }
        is Fail -> {
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
    )
) {
    val context = LocalContext.current

    ListItem(
        text = { Text(text = pocketItem.resolvedTitle) },
        secondaryText = pocketItem.excerpt?.let {
            { Text(text = pocketItem.excerpt, maxLines = 2, overflow = TextOverflow.Ellipsis) }
        },
        overlineText = pocketItem.timeToRead?.let {
            val minutes = pocketItem.timeToRead
            {
                Text(
                    text = context.resources.getQuantityString(R.plurals.minutes, minutes, minutes)
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
                // TODO : open url in external browser (option)
                val customTabsIntent = CustomTabsIntent
                    .Builder()
                    .build()
                customTabsIntent.launchUrl(context, pocketItem.givenUrl.toUri())
            }
            .padding(vertical = 4.dp)
    )
}