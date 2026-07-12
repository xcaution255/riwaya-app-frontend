package com.excaution.riwayaapp.presentation.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.excaution.riwayaapp.presentation.components.GradientButton
import com.excaution.riwayaapp.presentation.components.PressScaleButton
import com.excaution.riwayaapp.presentation.theme.GradientAccent
import com.excaution.riwayaapp.presentation.theme.GradientFeatured
import com.excaution.riwayaapp.presentation.theme.InkTheme
import com.excaution.riwayaapp.presentation.theme.LocalThemeController
import kotlinx.coroutines.launch

// ── Data ─────────────────────────────────────────────────────────────────────

data class UserProfile(
    val firstName: String  = "Augustino",
    val lastName: String   = "J",
    val username: String   = "@augustino",
    val email: String      = "augustino@email.com",
    val bio: String        = "Crafting worlds through words. Fantasy & Sci-Fi author. Writing \"The Last Starweaver\" series ✦",
    val website: String    = "augustino.dev",
    val memberSince: String = "Author since 2023",
    val reads: String      = "142k",
    val stories: Int       = 18,
    val followers: String  = "4.2k",
    val following: Int     = 389,
    val isOnline: Boolean  = true,
    val isPro: Boolean     = true,
    val monthlyEarnings: String = "$1,240",
    val subscriptionLabel: String = "Pro Writer · Renews Jan 1",
    val appVersion: String = "2.4.1",
)

sealed interface ProfileMenuItem {
    data class Navigable(
        val title: String,
        val subtitle: String? = null,
        val icon: ImageVector,
        val iconBg: Color,
        val iconTint: Color,
        val badge: String? = null,
        val isExternal: Boolean = false,
        val onClick: () -> Unit = {},
    ) : ProfileMenuItem

    data class Toggle(
        val title: String,
        val icon: ImageVector,
        val iconBg: Color,
        val iconTint: Color,
        val initialValue: Boolean = false,
    ) : ProfileMenuItem
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: UserProfile = UserProfile(),
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToStories: () -> Unit = {},
    onNavigateToEarnings: () -> Unit = {},
    onLogout: () -> Unit = {},
) {
    val sheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope        = rememberCoroutineScope()
    var showSheet    by remember { mutableStateOf(false) }
    var currentProfile by remember { mutableStateOf(profile) }

    // 1. Setup the Scroll Behavior for the Top Bar (EnterAlways = hides on downscroll, shows on upscroll)
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // Key perf trick: give LazyColumn its own stable state
    val listState = rememberLazyListState()

    Scaffold(
    )  { padding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                bottom = padding.calculateBottomPadding() + 8.dp,
            ),
            // Perf: tell compose items won't change size
            modifier = Modifier
                .fillMaxSize()
                .background(InkTheme.colors.bgDeep),
        ) {
            item(key = "header") {
                ProfileHero(
                    profile = currentProfile,
                    onEditClick = {
                        showSheet = true
                        scope.launch { sheetState.show() }
                    },
                )
                Spacer(Modifier.height(8.dp))
            }

            // Stats row
            item(key = "stats") {
                ProfileStats(profile = currentProfile)
                Spacer(Modifier.height(8.dp))
            }

            // Account section
            item(key = "section-account") {
                SectionLabel("Account")
            }
            item(key = "group-account") {
                MenuGroup(
                    items = buildAccountItems(
                        profile = currentProfile,
                        onStories = onNavigateToStories,
                        onEarnings = onNavigateToEarnings
                    )
                )
                Spacer(Modifier.height(8.dp))
            }

            // Preferences section
            item(key = "section-prefs") { SectionLabel("Preferences") }
            item(key = "group-prefs") {
                MenuGroup(items = buildPrefItems())
                Spacer(Modifier.height(8.dp))
            }

            // Support & Legal section
            item(key = "section-support") { SectionLabel("Support & Legal") }
            item(key = "group-support") {
                MenuGroup(items = buildSupportItems())
                Spacer(Modifier.height(16.dp))
            }

            // Sign out
            item(key = "signout") {
                SignOutButton(onClick = onLogout)
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    // Edit Profile Bottom Sheet
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = InkTheme.colors.bgSurface,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 4.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(InkTheme.colors.bgBorder),
                )
            },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        ) {
            EditProfileSheet(
                profile   = currentProfile,
                onSave    = { updated ->
                    currentProfile = updated
                    scope.launch { sheetState.hide() }
                    showSheet = false
                },
                onDismiss = {
                    scope.launch { sheetState.hide() }
                    showSheet = false
                },
            )
        }
    }
}

// ── Hero profile──────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileHero(
    profile: UserProfile,
    onEditClick: () -> Unit,
) {
    Column {
        // Cover image / gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(Brush.linearGradient(InkTheme.colors.coverGradient)),
        ) {
            // Decorative blobs
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .offset(x = (-30).dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(InkTheme.colors.accentPrimary.copy(alpha = 0.18f))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(x = 260.dp, y = 20.dp)
                    .clip(CircleShape)
                    .background(InkTheme.colors.accentLight.copy(alpha = 0.12f))
            )
            // Cover edit button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkTheme.colors.bgDeep.copy(alpha = 0.5f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) { }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = "Edit cover",
                    tint = InkTheme.colors.textSecondary,
                    modifier = Modifier.size(13.dp),
                )
                Text("Edit cover", fontSize = 11.sp, color = InkTheme.colors.textSecondary)
            }
        }

        // Avatar + info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .background(InkTheme.colors.bgDeep)
                .padding(bottom = 16.dp),
        ) {
            // Avatar (overlaps the cover)
            Box(
                modifier = Modifier
                    .offset(y = (-32).dp)
                    .size(68.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(GradientAccent))
                        .border(3.dp, InkTheme.colors.bgDeep, CircleShape),
                ) {
                    Text(
                        text = profile.firstName.first().uppercase(),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                    )
                }
                // Online dot
                if (profile.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(InkTheme.colors.successGreen)
                            .border(2.5.dp, InkTheme.colors. bgDeep, CircleShape),
                    )
                }
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "${profile.firstName} ${profile.lastName}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = InkTheme.colors.textPrimary,
                letterSpacing = (-0.4).sp,
            )
            Text(
                text = "${profile.username} · ${profile.memberSince}",
                fontSize = 12.sp,
                color = InkTheme.colors.textMuted,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = profile.bio,
                fontSize = 13.sp,
                color = InkTheme.colors.textSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier.padding(horizontal = 32.dp),
            )
            Spacer(Modifier.height(12.dp))
            // Edit button
            PressScaleButton(onClick = onEditClick) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(InkTheme.colors.bgSurface)
                        .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = null,
                        tint = InkTheme.colors.accentPrimary,
                        modifier = Modifier.size(15.dp),
                    )
                    Text(
                        text = "Edit profile",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = InkTheme.colors.accentPrimary,
                    )
                }
            }
        }
    }
}

// ── Stats ─────────────────────────────────────────────────────────────────────

@Composable
private fun ProfileStats(profile: UserProfile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(InkTheme.colors.bgSurface)
            .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(12.dp)),
    ) {
        listOf(
            profile.reads          to "Reads",
            profile.stories.toString() to "Stories",
            profile.followers      to "Followers",
            profile.following.toString() to "Following",
        ).forEachIndexed { index, (value, label) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (index > 0) Modifier.border(
                            width = 0.5.dp,
                            color = InkTheme.colors.bgBorder,
                            shape = RoundedCornerShape(0.dp),
                        ) else Modifier
                    )
                    .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) { }
                    .padding(vertical = 10.dp),
            ) {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = InkTheme.colors.textPrimary,
                    letterSpacing = (-0.3).sp,
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = InkTheme.colors.textMuted,
                    letterSpacing = 0.4.sp,
                )
            }
        }
    }
}

// ── Menu ──────────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = InkTheme.colors.textFaint,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp, bottom = 8.dp),
    )
}

@Composable
private fun MenuGroup(items: List<ProfileMenuItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(InkTheme.colors.bgSurface)
            .border(0.5.dp, InkTheme.colors.bgBorder, RoundedCornerShape(14.dp)),
    ) {
        items.forEachIndexed { index, item ->
            if (index > 0) {
                HorizontalDivider(
                    color = InkTheme.colors.bgBorder.copy(alpha = 0.6f),
                    thickness = 0.5.dp,
                    modifier  = Modifier.padding(start = 58.dp),
                )
            }
            when (item) {
                is ProfileMenuItem.Navigable -> NavigableItem(item)
                is ProfileMenuItem.Toggle    -> ToggleItem(item)
            }
        }
    }
}

@Composable
private fun NavigableItem(item: ProfileMenuItem.Navigable) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null){item.onClick}
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        // Icon box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(item.iconBg),
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(16.dp),
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = InkTheme.colors.textPrimary,
            )
            if (item.subtitle != null) {
                Text(
                    text = item.subtitle,
                    fontSize = 11.sp,
                    color = InkTheme.colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            if (item.badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(InkTheme.colors.accentPrimary)
                        .padding(horizontal = 7.dp, vertical = 2.dp),
                ) {
                    Text(item.badge, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Icon(
                imageVector = if (item.isExternal) Icons.Rounded.OpenInNew else Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint    = InkTheme.colors.textFaint,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun ToggleItem(
    item: ProfileMenuItem.Toggle
) {
    //var checked by remember { mutableStateOf(item.initialValue) }
    // 1. Grab the global controller reference directly from the environment
    val themeController = LocalThemeController.current
    val checked = themeController.value

    val thumbPos by animateFloatAsState(
        targetValue   = if (checked) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "toggleThumb",
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource =  remember { MutableInteractionSource() },
                indication = null) {  themeController.value = !checked} //2. Mutate global theme instantly
            .padding(horizontal = 14.dp, vertical = 13.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(item.iconBg),
        ) {
            Icon(imageVector = item.icon, contentDescription = null, tint = item.iconTint, modifier = Modifier.size(16.dp))
        }
        Text(item.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = InkTheme.colors.textPrimary, modifier = Modifier.weight(1f))
        // Custom toggle track
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (checked) InkTheme.colors.accentPrimary else InkTheme.colors.bgBorder)
                .clickable(
                    interactionSource =  remember { MutableInteractionSource() },
                    indication = null) {themeController.value = !checked} // 3. Mutate global theme instantly here too
        ) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .align(Alignment.CenterStart)
                    .offset(x = (3 + thumbPos * 16).dp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
    }
}

// ── Sign Out ──────────────────────────────────────────────────────────────────

@Composable
private fun SignOutButton(onClick: () -> Unit) {
    PressScaleButton(
        onClick  = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(InkTheme.colors.dangerRed.copy(alpha = 0.08f))
                .border(0.5.dp, InkTheme.colors.dangerRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(vertical = 13.dp),
        ) {
            Icon(
                imageVector        = Icons.Rounded.Logout,
                contentDescription = "Sign out",
                tint               = InkTheme.colors.dangerRed,
                modifier           = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text("Sign out", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = InkTheme.colors.dangerRed)
        }
    }
}

// ── Edit Profile Bottom Sheet ─────────────────────────────────────────────────

@Composable
private fun EditProfileSheet(
    profile: UserProfile,
    onSave: (UserProfile) -> Unit,
    onDismiss: () -> Unit,
) {
    var username  by remember { mutableStateOf(profile.username) }
    var bio       by remember { mutableStateOf(profile.bio) }
    var website   by remember { mutableStateOf(profile.website) }

    // Perf: sheet content is in its own scroll, separate from screen LazyColumn
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp),
    ) {
        // Title row
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
        ) {
            Text(
                text          = "Edit profile",
                fontSize      = 16.sp,
                fontWeight    = FontWeight.ExtraBold,
                color         = InkTheme.colors.textPrimary,
                letterSpacing = (-0.3).sp,
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(InkTheme.colors.bgBorder)
                    .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null){onDismiss()},
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Dismiss", tint = InkTheme.colors.textSecondary, modifier = Modifier.size(14.dp))
            }
        }

        Spacer(Modifier.height(16.dp))

        // Avatar picker
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(GradientAccent)),
                ) {
                    Text(username.firstOrNull()?.uppercase() ?: "?", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(InkTheme.colors.accentPrimary)
                        .border(2.dp, InkTheme.colors.bgSurface, CircleShape)
                        .clickable(interactionSource =  remember { MutableInteractionSource() }, indication = null) { },
                ) {
                    Icon(Icons.Rounded.CameraAlt, contentDescription = "Change photo", tint = Color.White, modifier = Modifier.size(11.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Change photo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = InkTheme.colors.accentPrimary, modifier = Modifier.clickable { })
        }

        Spacer(Modifier.height(20.dp))

        // Form fields
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            SheetField(label = "Username", value = username, onValueChange = { username = it })
            SheetField(label = "Bio", value = bio, onValueChange = { bio = it }, minLines = 2, maxLines = 4)
            SheetField(label = "Website", value = website, onValueChange = { website = it })
        }

        Spacer(Modifier.height(20.dp))

        // Save button
        GradientButton(
            text     = "Save changes",
            onClick  = {
                onSave(
                    profile.copy(
                        username  = username,
                        bio       = bio,
                        website   = website,
                    )
                )
            },
            icon     = Icons.Rounded.Check,
            modifier = Modifier.padding(horizontal = 16.dp),
        )
    }
}

@Composable
private fun SheetField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = 1,
) {
    var focused by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier,
    ) {
        Text(
            text          = label.uppercase(),
            fontSize      = 10.sp,
            fontWeight    = FontWeight.Bold,
            color         = InkTheme.colors.textMuted,
            letterSpacing = 0.6.sp,
        )
        BasicTextField(
            value         = value,
            onValueChange = onValueChange,
            minLines      = minLines,
            maxLines      = maxLines,
            textStyle = TextStyle(
                color    = InkTheme.colors.textPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
            ),
            decorationBox = { innerField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(InkTheme.colors.bgCard)
                        .border(
                            width = 0.5.dp,
                            color = if (focused) InkTheme.colors.accentPrimary else InkTheme.colors.bgBorder,
                            shape = RoundedCornerShape(10.dp),
                        )
                        .padding(horizontal = 13.dp, vertical = 11.dp),
                ) {
                    innerField()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focused = it.isFocused },
        )
    }
}

// ── Menu Builder helpers ──────────────────────────────────────────────────────

@Composable
private fun buildAccountItems(
    profile: UserProfile,
    onStories: () -> Unit,
    onEarnings: () -> Unit,
) = listOf(
    ProfileMenuItem.Navigable(
        title    = "Personal info",
        subtitle = profile.email,
        icon     = Icons.Rounded.Person,
        iconBg   = InkTheme.colors.accentPrimary.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.accentPrimary,
    ),
    ProfileMenuItem.Navigable(
        title    = "My stories",
        subtitle = "${profile.stories} published · 3 drafts",
        icon     = Icons.Rounded.Book,
        iconBg   = InkTheme.colors.genreSciFi.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.genreSciFi,
        badge    = "3 drafts",
        onClick  = onStories,
    ),
    ProfileMenuItem.Navigable(
        title    = "Earnings",
        subtitle = "${profile.monthlyEarnings} this month",
        icon     = Icons.Rounded.AttachMoney,
        iconBg   = InkTheme.colors.successGreen.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.successGreen,
        onClick  = onEarnings,
    ),
    ProfileMenuItem.Navigable(
        title    = "Subscription",
        subtitle = profile.subscriptionLabel,
        icon     = Icons.Rounded.Workspaces,
        iconBg   = InkTheme.colors.starColor.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.starColor,
        badge    = if (profile.isPro) "PRO" else null,
    ),
)

@Composable
private fun buildPrefItems() = listOf(
    ProfileMenuItem.Navigable(
        title    = "Notifications",
        subtitle = "Manage alerts",
        icon     = Icons.Rounded.Notifications,
        iconBg   = InkTheme.colors.accentPrimary.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.accentPrimary,
    ),
    ProfileMenuItem.Toggle(
        title        = "Dark mode",
        icon         = Icons.Rounded.DarkMode,
        iconBg       = InkTheme.colors.accentLight.copy(alpha = 0.12f),
        iconTint     = InkTheme.colors.accentLight,
        initialValue = true,
    ),
    ProfileMenuItem.Navigable(
        title    = "Language",
        subtitle = "English (US)",
        icon     = Icons.Rounded.Language,
        iconBg   = InkTheme.colors.genreSciFi.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.genreSciFi,
    ),
)

@Composable
private fun buildSupportItems() = listOf(
    ProfileMenuItem.Navigable(
        title    = "Send feedback",
        subtitle = "Help us improve inkflow",
        icon     = Icons.Rounded.Feedback,
        iconBg   = InkTheme.colors.successGreen.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.successGreen,
    ),
    ProfileMenuItem.Navigable(
        title    = "Help & support",
        icon     = Icons.Rounded.HeadsetMic,
        iconBg   = InkTheme.colors.accentPrimary.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.accentPrimary,
    ),
    ProfileMenuItem.Navigable(
        title      = "Privacy policy",
        icon       = Icons.Rounded.Lock,
        iconBg     = InkTheme.colors.accentLight.copy(alpha = 0.12f),
        iconTint   = InkTheme.colors.accentLight,
        isExternal = true,
    ),
    ProfileMenuItem.Navigable(
        title      = "Terms of service",
        icon       = Icons.Rounded.Description,
        iconBg     = InkTheme.colors.genreSciFi.copy(alpha = 0.12f),
        iconTint   = InkTheme.colors.genreSciFi,
        isExternal = true,
    ),
    ProfileMenuItem.Navigable(
        title    = "About RiwayaApp",
        subtitle = "Version 2.4.1",
        icon     = Icons.Rounded.Info,
        iconBg   = InkTheme.colors.genreSciFi.copy(alpha = 0.12f),
        iconTint = InkTheme.colors.genreSciFi,
    ),
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileHeroPrev() {
    ProfileHero(
        profile = UserProfile(),
        onEditClick = {}
    )
}