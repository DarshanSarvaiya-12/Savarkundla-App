package `in`.savarkundla.app

// Why we use this: Imports Android intents, voice recognition, Compose UI, and Coil image loader.
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Why we use this: Holds data for each shop card on the Home page.
data class ShopItem(
    val id: String = "",
    val name: String = "",
    val shopType: String = "",
    val shortAddress: String = "",
    val reviewCount: Int = 0,
    val rating: Double = 4.8,
    val imageUrl: String = "",
    val itemsSold: String = "",
    val phone: String = ""
)

// Why we use this: Holds data for each job card on the Jobs page, including GST number.
data class JobPost(
    val id: String = "",
    val roleTitle: String = "",
    val shopName: String = "",
    val salary: String = "",
    val address: String = "",
    val phone: String = "",
    val gstNumber: String = ""
)

// Why we use this: Hides the system title bar and launches the Compose application.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContent {
            SavarkundlaTheme {
                SavarkundlaRoot()
            }
        }
    }
}

// Why we use this: Loads Bricolage Grotesque from res/font for the logo and tagline.
@Composable
fun rememberLogoFont(): FontFamily {
    val context = LocalContext.current
    return remember {
        val resId = context.resources.getIdentifier("bricolage_grotesque", "font", context.packageName)
        if (resId != 0) FontFamily(Font(resId, FontWeight.Bold)) else FontFamily.SansSerif
    }
}

// Why we use this: Loads Roboto font files so the app ignores the phone's default font.
@Composable
fun rememberAppRobotoFont(): FontFamily {
    val context = LocalContext.current
    return remember {
        val reg = context.resources.getIdentifier("roboto_regular", "font", context.packageName)
        val bold = context.resources.getIdentifier("roboto_bold", "font", context.packageName)
        if (reg != 0 && bold != 0) {
            FontFamily(
                Font(reg, FontWeight.Normal),
                Font(reg, FontWeight.Medium),
                Font(bold, FontWeight.Bold),
                Font(bold, FontWeight.ExtraBold)
            )
        } else {
            FontFamily.SansSerif
        }
    }
}

// Why we use this: Generates the moving RGB border animation for the Search bar and Mic button.
@Composable
fun rememberRgbAnimation(): Brush {
    val transition = rememberInfiniteTransition(label = "rgb")
    val offset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shift"
    )
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFFFF0055),
            Color(0xFFFFAA00),
            Color(0xFF00E676),
            Color(0xFF00B0FF),
            Color(0xFFAA00FF),
            Color(0xFFFF0055)
        ),
        start = Offset(offset, 0f),
        end = Offset(offset + 400f, 400f),
        tileMode = TileMode.Repeated
    )
}

// Why we use this: Sets Roboto as the global font across all screens and dialogs.
@Composable
fun SavarkundlaTheme(content: @Composable () -> Unit) {
    val roboto = rememberAppRobotoFont()
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1E3A8A),
            secondary = Color(0xFFFF6B00),
            background = Color(0xFFEBF5FF),
            surface = Color.White
        )
    ) {
        ProvideTextStyle(value = TextStyle(fontFamily = roboto)) {
            content()
        }
    }
}

// Why we use this: Manages navigation between Home and Jobs, plus dialogs and data lists.
@Composable
fun SavarkundlaRoot() {
    val logoFont = rememberLogoFont()
    val roboto = rememberAppRobotoFont()
    val rgbBrush = rememberRgbAnimation()

    var currentPage by remember { mutableStateOf("Home") }
    var searchQuery by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf("All") }
    var showFilterModal by remember { mutableStateOf(false) }
    var showAddShopModal by remember { mutableStateOf(false) }
    var showCreateJobModal by remember { mutableStateOf(false) }

    // Why we use this: Captures spoken words from the Mic button and puts them into Search.
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val spoken = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) searchQuery = spoken
        }
    }

    val categories = listOf("All", "Electronics", "Clothing", "Grocery", "Hardware & Scales", "Medical", "Food")

    val shopList = remember {
        listOf(
            ShopItem(
                id = "1",
                name = "Kundla Weighing Scales & Hardware",
                shopType = "Hardware & Scales",
                shortAddress = "Navapara, Main Bazaar",
                reviewCount = 142,
                rating = 4.9,
                imageUrl = "https://images.unsplash.com/photo-1581783898377-1c85bf937427?w=800&q=80",
                itemsSold = "Digital scales, industrial weights, iron tools, hardware",
                phone = "9876543210"
            ),
            ShopItem(
                id = "2",
                name = "Patel Electronics & Mobile",
                shopType = "Electronics",
                shortAddress = "Jeshar Road, Near Bus Stand",
                reviewCount = 218,
                rating = 4.8,
                imageUrl = "https://images.unsplash.com/photo-1550009158-9ebf69173e03?w=800&q=80",
                itemsSold = "iPhones, Samsung phones, Smart TVs, AC, Refrigerators",
                phone = "9876543211"
            ),
            ShopItem(
                id = "3",
                name = "Shreeji Fashion Hub",
                shopType = "Clothing",
                shortAddress = "Ridhhi Siddhi Complex, Mahuva Rd",
                reviewCount = 96,
                rating = 4.7,
                imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=800&q=80",
                itemsSold = "Men's wear, jeans, shirts, wedding sherwani, sarees",
                phone = "9876543212"
            ),
            ShopItem(
                id = "4",
                name = "Bapa Sitaram Supermart",
                shopType = "Grocery",
                shortAddress = "Amreli Road, Savarkundla",
                reviewCount = 310,
                rating = 4.9,
                imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&q=80",
                itemsSold = "Atta, spices, dry fruits, cold drinks, daily groceries",
                phone = "9876543213"
            )
        )
    }

    // Why we use this: Stores available local jobs and updates live when a new job is created.
    val jobList = remember {
        mutableStateListOf(
            JobPost(
                id = "1",
                roleTitle = "Showroom Sales Executive",
                shopName = "Patel Electronics & Mobile",
                salary = "₹12,000 - ₹15,000 / month",
                address = "Jeshar Road, Near Bus Stand",
                phone = "9876543211",
                gstNumber = "24AABCP1234F1Z5"
            ),
            JobPost(
                id = "2",
                roleTitle = "Billing & Computer Operator",
                shopName = "Bapa Sitaram Supermart",
                salary = "₹10,000 - ₹13,000 / month",
                address = "Amreli Road, Savarkundla",
                phone = "9876543213",
                gstNumber = "24BBCSP5678G1Z2"
            ),
            JobPost(
                id = "3",
                roleTitle = "Scale Assembly Technician",
                shopName = "Kundla Weighing Scales & Hardware",
                salary = "₹14,000 - ₹18,000 / month",
                address = "Navapara, Main Bazaar",
                phone = "9876543210",
                gstNumber = "24CCDKW9012H1Z9"
            )
        )
    }

    val visibleShops = shopList.filter { s ->
        val catOk = activeCategory == "All" || s.shopType == activeCategory
        val queryOk = s.name.contains(searchQuery, true) ||
                s.itemsSold.contains(searchQuery, true) ||
                s.shopType.contains(searchQuery, true) ||
                s.shortAddress.contains(searchQuery, true)
        catOk && queryOk
    }

    val skyBg = Color(0xFFEBF5FF)

    Scaffold(
        containerColor = skyBg,
        topBar = {
            // Why we use this: Renders the top header for either the Home page or the Jobs page.
            Surface(color = Color.White, shadowElevation = 2.dp, modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Why we use this: Clicking the Savarkundla.in logo always brings the user to Home.
                        BrandLogo(
                            logoFont = logoFont,
                            onClick = { currentPage = "Home" }
                        )

                        if (currentPage == "Home") {
                            // Why we use this: 92.dp width aligns Filter's left edge with the Mic button's left edge below.
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .width(92.dp)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(1.4.dp, Color(0xFF1E3A8A), RoundedCornerShape(10.dp))
                                    .clickable { showFilterModal = true }
                            ) {
                                Text(
                                    text = if (activeCategory == "All") "Filter" else activeCategory,
                                    fontFamily = roboto,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E3A8A),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(horizontal = 6.dp)
                                )
                            }
                        } else {
                            // Why we use this: '+' button on the Jobs page to create a new job.
                            BluePlusButton(onClick = { showCreateJobModal = true })
                        }
                    }

                    // Why we use this: Shows Search, Mic, and '+' row only on the Home page.
                    if (currentPage == "Home") {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(fontFamily = roboto, fontSize = 14.sp, color = Color.Black),
                                cursorBrush = SolidColor(Color(0xFF1E3A8A)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(1.2.dp, rgbBrush, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp),
                                decorationBox = { innerField ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = Color(0xFF64748B),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    text = "Ask Anything",
                                                    fontFamily = roboto,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF94A3B8)
                                                )
                                            }
                                            innerField()
                                        }
                                        if (searchQuery.isNotEmpty()) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier
                                                    .size(16.dp)
                                                    .clickable { searchQuery = "" }
                                            )
                                        }
                                    }
                                }
                            )

                            // Why we use this: 42.dp Mic button with moving RGB border.
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .width(42.dp)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)
                                    .border(1.2.dp, rgbBrush, RoundedCornerShape(10.dp))
                                    .clickable {
                                        try {
                                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                                putExtra(
                                                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                                                )
                                                putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask Anything...")
                                            }
                                            speechLauncher.launch(intent)
                                        } catch (_: Exception) {
                                        }
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Mic",
                                    tint = Color(0xFF1E3A8A),
                                    modifier = Modifier.size(19.dp)
                                )
                            }

                            // Why we use this: 42.dp '+' button on the right of the Mic button.
                            BluePlusButton(onClick = { showAddShopModal = true })
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Why we use this: Lower navigation bar with Home and Jobs buttons.
            BottomBarNavigation(
                currentPage = currentPage,
                roboto = roboto,
                onSelectPage = { currentPage = it }
            )
        }
    ) { innerPadding ->
        // Why we use this: Switches between the Home page and the Jobs page.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(skyBg)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (currentPage == "Home") {
                item {
                    Text(
                        text = "Find what you want to Buy - Amreli",
                        fontFamily = logoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        letterSpacing = (-0.5).sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 2.dp)
                    )
                }

                items(visibleShops) { shop ->
                    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                        ShopItemCard(shop = shop, roboto = roboto)
                    }
                }
            } else {
                item {
                    Text(
                        text = "Available Jobs in Savarkundla",
                        fontFamily = logoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        letterSpacing = (-0.5).sp,
                        color = Color.Black,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp, end = 12.dp, top = 14.dp, bottom = 2.dp)
                    )
                }

                items(jobList) { job ->
                    Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                        JobPostCard(job = job, roboto = roboto)
                    }
                }
            }

            // Why we use this: Blue footer displayed at the bottom of the scrollable list.
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E3A8A))
                        .padding(vertical = 18.dp, horizontal = 16.dp)
                ) {
                    Text(
                        text = "Savarkundla.in by PiCell Company.",
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    // Why we use this: Redesigned Filter popup with divider lines and heavy-weight Roboto text.
    if (showFilterModal) {
        RedesignedFilterDialog(
            categories = categories,
            selectedCategory = activeCategory,
            roboto = roboto,
            onSelect = { chosen ->
                activeCategory = chosen
                showFilterModal = false
            },
            onDismiss = { showFilterModal = false }
        )
    }

    // Why we use this: Shows the ₹2,000/month shop membership popup when '+' is clicked on Home.
    if (showAddShopModal) {
        AlertDialog(
            onDismissRequest = { showAddShopModal = false },
            title = {
                Text("List Your Shop on Savarkundla.in", fontFamily = roboto, fontWeight = FontWeight.ExtraBold)
            },
            text = {
                Text(
                    text = "Grow your business in Savarkundla!\n\n" +
                            "• Shop Photo & Verified Profile\n" +
                            "• Appear in Customer Product Searches\n" +
                            "• Direct Call & WhatsApp Leads\n\n" +
                            "Subscription Plan: ₹2,000 / month",
                    fontFamily = roboto,
                    fontWeight = FontWeight.Medium
                )
            },
            confirmButton = {
                Button(
                    onClick = { showAddShopModal = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Text("Join via WhatsApp", fontFamily = roboto, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddShopModal = false }) {
                    Text("Close", fontFamily = roboto, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Why we use this: Opens the Create New Job popup where GST number is compulsory.
    if (showCreateJobModal) {
        CreateJobDialog(
            roboto = roboto,
            onDismiss = { showCreateJobModal = false },
            onJobCreated = { newJob ->
                jobList.add(0, newJob)
                showCreateJobModal = false
            }
        )
    }
}

// Why we use this: Renders the Savarkundla.in logo and navigates to Home when clicked.
@Composable
fun BrandLogo(logoFont: FontFamily, onClick: () -> Unit) {
    Text(
        modifier = Modifier.clickable { onClick() },
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontFamily = logoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    letterSpacing = (-0.5).sp,
                    color = Color(0xFF1E3A8A)
                )
            ) {
                append("Savarkundla")
            }
            withStyle(
                SpanStyle(
                    fontFamily = logoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    letterSpacing = (-0.5).sp,
                    color = Color(0xFFFF6B00)
                )
            ) {
                append(".in")
            }
        }
    )
}

// Why we use this: Reusable 42x40.dp '+' icon button with blue border and blue '+' symbol.
@Composable
fun BluePlusButton(onClick: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(42.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .border(1.4.dp, Color(0xFF1E3A8A), RoundedCornerShape(10.dp))
            .clickable { onClick() }
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color(0xFF1E3A8A),
            modifier = Modifier.size(23.dp)
        )
    }
}

// Why we use this: Lower bar with Home and Jobs buttons so users can switch pages easily.
@Composable
fun BottomBarNavigation(currentPage: String, roboto: FontFamily, onSelectPage: (String) -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            HorizontalDivider(thickness = 1.dp, color = Color(0xFFCBD5E1))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Home", "Jobs").forEach { tab ->
                    val isSelected = currentPage == tab
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF1E3A8A) else Color(0xFFF8FAFC))
                            .border(
                                width = 1.2.dp,
                                color = Color(0xFF1E3A8A),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectPage(tab) }
                    ) {
                        Text(
                            text = tab,
                            fontFamily = roboto,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = if (isSelected) Color.White else Color(0xFF1E3A8A)
                        )
                    }
                }
            }
        }
    }
}

// Why we use this: Redesigned Filter popup with crisp divider lines and heavy-weight Roboto text.
@Composable
fun RedesignedFilterDialog(
    categories: List<String>,
    selectedCategory: String,
    roboto: FontFamily,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SELECT CATEGORY",
                        fontFamily = roboto,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "RESET",
                        fontFamily = roboto,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp,
                        color = Color(0xFFFF6B00),
                        modifier = Modifier.clickable { onSelect("All") }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 2.dp, color = Color(0xFF1E3A8A))
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                categories.forEachIndexed { index, category ->
                    val isPicked = selectedCategory == category
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isPicked) Color(0xFFEFF6FF) else Color.White)
                            .clickable { onSelect(category) }
                            .padding(vertical = 13.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = category,
                            fontFamily = roboto,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = if (isPicked) Color(0xFF1E3A8A) else Color(0xFF0F172A)
                        )
                        if (isPicked) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color(0xFF1E3A8A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    if (index < categories.lastIndex) {
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFCBD5E1))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("CLOSE", fontFamily = roboto, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            }
        }
    )
}

// Why we use this: Form popup for posting a new job where GST Number is compulsory.
@Composable
fun CreateJobDialog(
    roboto: FontFamily,
    onDismiss: () -> Unit,
    onJobCreated: (JobPost) -> Unit
) {
    var roleTitle by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Column {
                Text(
                    text = "Create New Job",
                    fontFamily = roboto,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 19.sp,
                    color = Color(0xFF1E3A8A)
                )
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(thickness = 1.5.dp, color = Color(0xFF1E3A8A))
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = gstNumber,
                    onValueChange = {
                        gstNumber = it.uppercase()
                        errorMsg = ""
                    },
                    label = { Text("GST Number (Compulsory *)", fontFamily = roboto, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = roleTitle,
                    onValueChange = { roleTitle = it },
                    label = { Text("Job Role (e.g., Salesman)", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop / Company Name", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = salary,
                    onValueChange = { salary = it },
                    label = { Text("Monthly Salary (e.g., ₹12,000)", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Shop Address", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorMsg.isNotEmpty()) {
                    Text(
                        text = errorMsg,
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFFDC2626)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    when {
                        gstNumber.isBlank() -> errorMsg = "GST Number is compulsory to create a job!"
                        roleTitle.isBlank() || shopName.isBlank() || phone.isBlank() ->
                            errorMsg = "Please fill Job Role, Shop Name, and Mobile Number."
                        else -> {
                            onJobCreated(
                                JobPost(
                                    id = System.currentTimeMillis().toString(),
                                    roleTitle = roleTitle.trim(),
                                    shopName = shopName.trim(),
                                    salary = if (salary.isBlank()) "Negotiable" else salary.trim(),
                                    address = if (address.isBlank()) "Savarkundla" else address.trim(),
                                    phone = phone.trim(),
                                    gstNumber = gstNumber.trim()
                                )
                            )
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
            ) {
                Text("Post Job", fontFamily = roboto, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontFamily = roboto, fontWeight = FontWeight.Bold)
            }
        }
    )
}

// Why we use this: Displays each available job in a bordered Card on the Jobs page.
@Composable
fun JobPostCard(job: JobPost, roboto: FontFamily) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.7.dp, Color.Black.copy(alpha = 0.28f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "GST: ${job.gstNumber}",
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFF1E3A8A),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = job.salary,
                    fontFamily = roboto,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp,
                    color = Color(0xFF16A34A)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = job.roleTitle,
                fontFamily = roboto,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = job.shopName,
                fontFamily = roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF1E3A8A)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = job.address,
                    fontFamily = roboto,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${job.phone}")))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Text("Call", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        val link = "https://api.whatsapp.com/send?phone=91${job.phone}&text=Hi%20${job.shopName},%20I%20want%20to%20apply%20for%20${job.roleTitle}%20on%20Savarkundla.in!"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text("WhatsApp", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// Why we use this: Displays each Shop Card with photo, rating, address, and Call/WhatsApp buttons.
@Composable
fun ShopItemCard(shop: ShopItem, roboto: FontFamily) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.7.dp, Color.Black.copy(alpha = 0.28f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .background(Color(0xFFE2E8F0))
            ) {
                AsyncImage(
                    model = shop.imageUrl,
                    contentDescription = shop.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = Color(0xFF0F172A).copy(alpha = 0.82f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = shop.shopType,
                        fontFamily = roboto,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.95f),
                    shape = RoundedCornerShape(8.dp),
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${shop.rating}",
                            fontFamily = roboto,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "(${shop.reviewCount} reviews)",
                            fontFamily = roboto,
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = shop.name,
                    fontFamily = roboto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = "Address",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = shop.shortAddress,
                        fontFamily = roboto,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}")))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Call", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            val link = "https://api.whatsapp.com/send?phone=91${shop.phone}&text=Hi%20${shop.name},%20I%20found%20your%20shop%20on%20Savarkundla.in!"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("WhatsApp", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
