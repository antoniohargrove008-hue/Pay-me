package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.SubcomposeAsyncImage
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                PaymentAppScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentAppScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel()
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = Home, 1 = History, 2 = Account

    // State bindings
    val cashTag by viewModel.cashTag.collectAsState()
    val businessName by viewModel.businessName.collectAsState()
    val tagline by viewModel.tagline.collectAsState()
    val amount by viewModel.amount.collectAsState()
    val note by viewModel.note.collectAsState()

    // UI Helper: Live Links
    val payLink = viewModel.getCashAppLink()
    val qrCodeUrl = viewModel.getQrCodeUrl()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Branded Bottom navigation matching the Design HTML footer layout perfectly
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 4.dp,
                modifier = Modifier
                    .height(72.dp)
                    .border(BorderStroke(1.dp, Color(0xFFDDE4D8)), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00D632),
                        selectedTextColor = Color(0xFF00D632),
                        unselectedIconColor = Color(0xFF72796F),
                        unselectedTextColor = Color(0xFF72796F),
                        indicatorColor = Color(0xFFE6FBEB)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 1) Icons.Filled.History else Icons.Outlined.History,
                            contentDescription = "History"
                        )
                    },
                    label = { Text("History", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00D632),
                        selectedTextColor = Color(0xFF00D632),
                        unselectedIconColor = Color(0xFF72796F),
                        unselectedTextColor = Color(0xFF72796F),
                        indicatorColor = Color(0xFFE6FBEB)
                    )
                )

                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == 2) Icons.Filled.Settings else Icons.Outlined.Settings,
                            contentDescription = "Account"
                        )
                    },
                    label = { Text("Account", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF00D632),
                        selectedTextColor = Color(0xFF00D632),
                        unselectedIconColor = Color(0xFF72796F),
                        unselectedTextColor = Color(0xFF72796F),
                        indicatorColor = Color(0xFFE6FBEB)
                    )
                )
            }
        },
        containerColor = Color(0xFFF0F5ED) // Sage Green background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF0F5ED))
        ) {
            when (selectedTab) {
                0 -> HomeCustomerView(
                    cashTag = cashTag,
                    businessName = businessName,
                    tagline = tagline,
                    amount = amount,
                    note = note,
                    payLink = payLink,
                    qrCodeUrl = qrCodeUrl,
                    viewModel = viewModel,
                    onNavigateToCustomize = { selectedTab = 2 }
                )
                1 -> HistoryView()
                2 -> CustomizeSettingsView(
                    viewModel = viewModel,
                    cashTag = cashTag,
                    businessName = businessName,
                    tagline = tagline,
                    amount = amount,
                    note = note
                )
            }
        }
    }
}

@Composable
fun HomeCustomerView(
    cashTag: String,
    businessName: String,
    tagline: String,
    amount: String,
    note: String,
    payLink: String,
    qrCodeUrl: String,
    viewModel: MainViewModel,
    onNavigateToCustomize: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. Sleek Header Row (h-16 matching HTML)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateToCustomize,
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.04f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Customize Portal",
                    tint = Color(0xFF1A1C19),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "Support Business",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A1C19),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = {
                    viewModel.resetToDefaults()
                    Toast.makeText(context, "Reset to default profile", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.Black.copy(alpha = 0.04f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Settings",
                    tint = Color(0xFF1A1C19),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // 2. Main Column Body with exact Spacing
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Storefront Badge with Verified indicator
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .padding(bottom = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                // Branded Emerald Green Circle Icon Container
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(Color(0xFF00D632), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = "Storefront",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Overlapping Verified Badge
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(Color.White, CircleShape)
                        .border(3.dp, Color(0xFFF0F5ED), CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Verified profile",
                        tint = Color(0xFF00D632),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Business/Merchant Name
            Text(
                text = businessName.ifEmpty { "The Creative Studio" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C19),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Tagline/Supporting info
            Text(
                text = tagline.ifEmpty { "Supporting local artisans & design" },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF424940),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Solid White Rounded Card with Thin Border and custom tracking-widest label
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("payment_card")
                    .border(BorderStroke(1.dp, Color(0xFFDDE4D8)), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "PAY TO CASHTAG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF00D632),
                        letterSpacing = 1.8.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "$${cashTag.ifEmpty { "TheStudioArt" }}",
                        fontSize = 28.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1C19)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Three-Column Preset Support Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val presets = listOf("5.00", "10.00", "20.00")
                presets.forEach { preset ->
                    val isSelected = amount == preset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp)
                            .background(
                                color = if (isSelected) Color(0xFF00D632) else Color.White,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF00D632) else Color(0xFFDDE4D8),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (isSelected) {
                                    viewModel.updateAmount("")
                                } else {
                                    viewModel.updateAmount(preset)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$${preset.substringBefore(".")}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color(0xFF1A1C19)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Italicized Subtext Quote matching user HTML
            val quoteMessage = if (note.isNotEmpty()) {
                "Payment Request for: \"$note\""
            } else {
                "\"Your contribution helps us keep the workshop open and free for community classes. Thank you!\""
            }

            Text(
                text = quoteMessage,
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF72796F),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 18.sp
            )

            // Dynamic live QR code expansion section
            var showQr by remember { mutableStateOf(false) }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { showQr = !showQr },
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFDDE4D8)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF424940)),
                modifier = Modifier.height(40.dp)
            ) {
                Icon(
                    imageVector = if (showQr) Icons.Default.VisibilityOff else Icons.Default.QrCode,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (showQr) "Hide QR Code" else "Show Scan QR Code",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            AnimatedVisibility(
                visible = showQr,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(170.dp)
                            .background(Color.White, RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFDDE4D8), RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = qrCodeUrl,
                            contentDescription = "Scan QR",
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                CircularProgressIndicator(
                                    color = Color(0xFF00D632),
                                    strokeWidth = 3.dp,
                                    modifier = Modifier.size(32.dp)
                                )
                            },
                            error = {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = "Offline",
                                    tint = Color.LightGray
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Scan with another device to pay",
                        fontSize = 11.sp,
                        color = Color(0xFF72796F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Primary fully rounded payment action button
            Button(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(payLink))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Redirecting to web Cash App link.", Toast.LENGTH_SHORT).show()
                        try {
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(payLink))
                            context.startActivity(webIntent)
                        } catch (e2: Exception) {
                            Toast.makeText(context, "Could not open browser link", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D632)),
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("pay_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                val btnLabel = if (amount.isNotEmpty()) "Pay $$amount with Cash App" else "Pay with Cash App"
                Text(
                    text = btnLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Outlined Share Portal Link
            OutlinedButton(
                onClick = {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, businessName)
                        putExtra(Intent.EXTRA_TEXT, "Support my business! Pay with Cash App at: $payLink")
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Payment Portal"))
                },
                shape = RoundedCornerShape(50),
                border = BorderStroke(1.5.dp, Color(0xFF00D632)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00D632)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("share_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Share Payment Link",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HistoryView() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = Color(0xFF00D632),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Support Ledger",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C19)
            )
        }

        Text(
            text = "Track your generated request milestones and offline contribution progress below.",
            fontSize = 14.sp,
            color = Color(0xFF424940),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Target milestones
        Text(
            text = "ACTIVE SUPPORT CAMPAIGNS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00D632),
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Milestone 1
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFDDE4D8)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎨 Community Classes Free Fund", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A1C19))
                    Text(text = "85%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00D632))
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.85f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF00D632),
                    trackColor = Color(0xFFE6FBEB)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Helps keep weekly art classes fully free and stocked.", fontSize = 11.sp, color = Color(0xFF72796F))
            }
        }

        // Milestone 2
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFDDE4D8)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🛠️ Workshop Tool Upgrades", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1A1C19))
                    Text(text = "40%", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00D632))
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = 0.4f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = Color(0xFF00D632),
                    trackColor = Color(0xFFE6FBEB)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Upgrading community pottery wheels and safety kilns.", fontSize = 11.sp, color = Color(0xFF72796F))
            }
        }

        // Generated Receipts list
        Text(
            text = "GENERATED LINKS LOG",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00D632),
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val pastReceipts = listOf(
            Triple("Today", "$5.00", "Community Tip"),
            Triple("Yesterday", "$10.00", "Class supplies support"),
            Triple("June 28", "$20.00", "Studio session support")
        )

        pastReceipts.forEach { receipt ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFDDE4D8)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = receipt.third, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color(0xFF1A1C19))
                        Text(text = receipt.first, fontSize = 11.sp, color = Color(0xFF72796F))
                    }
                    Text(
                        text = receipt.second,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF00D632)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizeSettingsView(
    viewModel: MainViewModel,
    cashTag: String,
    businessName: String,
    tagline: String,
    amount: String,
    note: String
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = Color(0xFF00D632),
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Customize Portal",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C19)
            )
        }

        Text(
            text = "Configure your brand details and optional requests. Changes populate your payment screen instantly.",
            fontSize = 14.sp,
            color = Color(0xFF424940),
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 1. Interactive Custom Link Generator Output Card
        val generatedLink = viewModel.getCashAppLink()
        val context = LocalContext.current

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .testTag("generator_output_card"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFDDE4D8)),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "GENERATED CUSTOM LINK",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF00D632),
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.height(10.dp))

                // The dynamic URL text box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F5ED), RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color(0xFFDDE4D8)), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = generatedLink,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1A1C19),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action controls: Copy, Share, Open/Test
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy Link Button
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Cash App Payment Link", generatedLink)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Payment link copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D632)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(44.dp)
                            .testTag("copy_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Link", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }

                    // Share Link Button
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Cash App Payment Link")
                                putExtra(Intent.EXTRA_TEXT, "Support us with Cash App: $generatedLink")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Payment Link"))
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF00D632)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00D632)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("share_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Open/Test Link Button
                    OutlinedButton(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(generatedLink))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Opening in browser", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFDDE4D8)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF424940)),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("test_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowOutward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_card"),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFDDE4D8)),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // CashTag input
                OutlinedTextField(
                    value = cashTag,
                    onValueChange = { viewModel.updateCashTag(it) },
                    label = { Text("Your CashTag ($)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = Color(0xFF00D632)
                        )
                    },
                    placeholder = { Text("YourCashTag") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("cashtag_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D632),
                        focusedLabelColor = Color(0xFF00D632)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Business Name input
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { viewModel.updateBusinessName(it) },
                    label = { Text("Business / Display Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Business,
                            contentDescription = null,
                            tint = Color(0xFF00D632)
                        )
                    },
                    placeholder = { Text("The Creative Studio") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D632),
                        focusedLabelColor = Color(0xFF00D632)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tagline input
                OutlinedTextField(
                    value = tagline,
                    onValueChange = { viewModel.updateTagline(it) },
                    label = { Text("Subheading Message") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFF00D632)
                        )
                    },
                    placeholder = { Text("Supporting local artisans & design") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D632),
                        focusedLabelColor = Color(0xFF00D632)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))
                HorizontalDivider(color = Color(0xFFDDE4D8))
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Specify Payment Request (Optional)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF424940),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Optional Amount Input
                OutlinedTextField(
                    value = amount,
                    onValueChange = { viewModel.updateAmount(it) },
                    label = { Text("Amount ($)") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFF00D632)
                        )
                    },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D632),
                        focusedLabelColor = Color(0xFF00D632)
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Note Input
                OutlinedTextField(
                    value = note,
                    onValueChange = { viewModel.updateNote(it) },
                    label = { Text("Reason / Note") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Color(0xFF00D632)
                        )
                    },
                    placeholder = { Text("Art Supplies, Pottery Class, Tip...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00D632),
                        focusedLabelColor = Color(0xFF00D632)
                    )
                )

                // Clear Custom Payment Request if active
                if (amount.isNotEmpty() || note.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    TextButton(
                        onClick = {
                            viewModel.updateAmount("")
                            viewModel.updateNote("")
                        },
                        modifier = Modifier.align(Alignment.End),
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear Custom Request", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    // Kept intact to fully support existing screenshot testing suites
    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Hello $name!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00D632))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Welcome to Pay Me!", fontSize = 16.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        Greeting("Android")
    }
}
