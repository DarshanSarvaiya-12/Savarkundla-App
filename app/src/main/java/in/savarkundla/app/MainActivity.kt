package `in`.savarkundla.app

// Why we use this
// Imports Android intents, activity result launchers, Firebase Firestore, and Firebase Storage.
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Window
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import java.util.UUID

// Why we use this
// Blueprint mapping directly to Firebase Firestore "Shops" collection documents.
data class ShopItem(
    val id: String = "",
    val name: String = "",
    val shopType: String = "",
    val shortAddress: String = "",
    val reviewCount: Int = 0,
    val rating: Double = 4.5,
    val imageUrl: String = "",
    val itemsSold: String = "",
    val phone: String = "",
    val isActive: Boolean = true
)

// Why we use this
// Blueprint mapping directly to Firebase Firestore "Jobs" collection documents.
data class JobPost(
    val id: String = "",
    val roleTitle: String = "",
    val shopName: String = "",
    val salary: String = "",
    val education: String = "10th / 12th / Graduate",
    val experience: String = "Freshers & Experienced",
    val jobTime: String = "9:00 AM - 8:00 PM",
    val fullAddress: String = "Main Market, Savarkundla",
    val aboutJob: String = "Counter sales and general store management.",
    val interviewerName: String = "Manager",
    val interviewerNumber: String = "",
    val gstNumber: String = "",
    val isActive: Boolean = true
)

// Why we use this
// Hides system title bar and launches the app UI.
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

// Why we use this
// Loads Bricolage Grotesque font for logo and headers.
@Composable
fun rememberLogoFont(): FontFamily {
    val context = LocalContext.current
    return remember {
        val resId = context.resources.getIdentifier("bricolage_grotesque", "font", context.packageName)
        if (resId != 0) FontFamily(Font(resId, FontWeight.Bold)) else FontFamily.SansSerif
    }
}

// Why we use this
// Loads Roboto font family across the app.
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

// Why we use this
// Generates the moving RGB border animation for Search and Mic buttons.
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

// Why we use this
// Premium Black & Sky-Blue gradient for Shop buttons.
val premiumBlackSkyBlueBrush = Brush.horizontalGradient(
    colors = listOf(
        Color(0xFF030712),
        Color(0xFF075985),
        Color(0xFF38BDF8)
    )
)

// Why we use this
// Sets Roboto as the global font.
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

// Why we use this
// Animates between Review and Shop Type inside a compact upper-right corner badge.
@Composable
fun CompactCornerBadge(
    rating: Double,
    reviewCount: Int,
    shopType: String,
    roboto: FontFamily,
    modifier: Modifier = Modifier
) {
    var showReview by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2200)
            showReview = !showReview
        }
    }

    Surface(
        color = Color.White.copy(alpha = 0.96f),
        shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 10.dp),
        border = BorderStroke(0.7.dp, Color(0xFF1E3A8A).copy(alpha = 0.6f)),
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        AnimatedContent(
            targetState = if (showReview) "⭐ $rating ($reviewCount)" else shopType,
            transitionSpec = {
                (slideInVertically { h -> h } + fadeIn(tween(250)))
                    .togetherWith(slideOutVertically { h -> -h } + fadeOut(tween(250)))
            },
            label = "compactCornerTicker"
        ) { textVal ->
            Text(
                text = textVal,
                fontFamily = roboto,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                color = if (textVal.startsWith("⭐")) Color.Black else Color(0xFF1E3A8A),
                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
            )
        }
    }
}

// Why we use this
// Coordinates live Firestore synchronization for both Shops and Jobs sections.
@Composable
fun SavarkundlaRoot() {
    val logoFont = rememberLogoFont()
    val roboto = rememberAppRobotoFont()
    val rgbBrush = rememberRgbAnimation()

    var currentPage by remember { mutableStateOf("Home") }
    var selectedShop by remember { mutableStateOf<ShopItem?>(null) }
    var selectedJob by remember { mutableStateOf<JobPost?>(null) }

    val homeListState = rememberLazyListState()
    val jobListState = rememberLazyListState()

    var searchQuery by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf("All") }
    var showFilterModal by remember { mutableStateOf(false) }
    var showAddShopModal by remember { mutableStateOf(false) }
    var showCreateJobModal by remember { mutableStateOf(false) }

    // Live state from Firebase Firestore
    var shopList by remember { mutableStateOf<List<ShopItem>>(emptyList()) }
    var isLoadingShops by remember { mutableStateOf(true) }

    var jobList by remember { mutableStateOf<List<JobPost>>(emptyList()) }
    var isLoadingJobs by remember { mutableStateOf(true) }

    // Why we use this
    // Real-time listener for Firestore "Shops" collection.
    LaunchedEffect(Unit) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("Shops")
                .addSnapshotListener { snapshot, error ->
                    isLoadingShops = false
                    if (error == null && snapshot != null) {
                        val fetched = snapshot.documents.mapNotNull { doc ->
                            try {
                                ShopItem(
                                    id = doc.id,
                                    name = doc.getString("name") ?: "",
                                    shopType = doc.getString("shopType") ?: "General",
                                    shortAddress = doc.getString("shortAddress") ?: "",
                                    reviewCount = doc.getLong("reviewCount")?.toInt() ?: 0,
                                    rating = doc.getDouble("rating") ?: 4.5,
                                    imageUrl = doc.getString("imageUrl") ?: "",
                                    itemsSold = doc.getString("itemsSold") ?: "",
                                    phone = doc.getString("phone") ?: "",
                                    isActive = doc.getBoolean("isActive") ?: true
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        shopList = fetched.filter { it.isActive }
                    }
                }
        } catch (e: Exception) {
            isLoadingShops = false
        }
    }

    // Why we use this
    // Real-time listener for Firestore "Jobs" collection.
    LaunchedEffect(Unit) {
        try {
            val firestore = FirebaseFirestore.getInstance()
            firestore.collection("Jobs")
                .addSnapshotListener { snapshot, error ->
                    isLoadingJobs = false
                    if (error == null && snapshot != null) {
                        val fetchedJobs = snapshot.documents.mapNotNull { doc ->
                            try {
                                JobPost(
                                    id = doc.id,
                                    roleTitle = doc.getString("roleTitle") ?: "",
                                    shopName = doc.getString("shopName") ?: "",
                                    salary = doc.getString("salary") ?: "",
                                    education = doc.getString("education") ?: "10th / 12th / Graduate",
                                    experience = doc.getString("experience") ?: "Freshers & Experienced",
                                    jobTime = doc.getString("jobTime") ?: "9:00 AM - 8:00 PM",
                                    fullAddress = doc.getString("fullAddress") ?: "Savarkundla",
                                    aboutJob = doc.getString("aboutJob") ?: "Counter sales & service",
                                    interviewerName = doc.getString("interviewerName") ?: "Manager",
                                    interviewerNumber = doc.getString("interviewerNumber") ?: "",
                                    gstNumber = doc.getString("gstNumber") ?: "",
                                    isActive = doc.getBoolean("isActive") ?: true
                                )
                            } catch (e: Exception) {
                                null
                            }
                        }
                        jobList = fetchedJobs.filter { it.isActive }
                    }
                }
        } catch (e: Exception) {
            isLoadingJobs = false
        }
    }

    BackHandler(enabled = selectedShop != null) {
        selectedShop = null
    }

    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val spoken = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) searchQuery = spoken
        }
    }

    val categories = listOf("All", "Electronics", "Clothing", "Grocery", "Hardware & Scales", "Medical", "Food")

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
                        BrandLogo(
                            logoFont = logoFont,
                            onClick = {
                                selectedShop = null
                                currentPage = "Home"
                            }
                        )

                        if (currentPage == "Home" && selectedShop == null) {
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
                        } else if (currentPage == "Jobs") {
                            BluePlusButton(onClick = { showCreateJobModal = true })
                        }
                    }

                    if (currentPage == "Home" && selectedShop == null) {
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

                            BluePlusButton(onClick = { showAddShopModal = true })
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomBarNavigation(
                currentPage = currentPage,
                roboto = roboto,
                onSelectPage = { page ->
                    selectedShop = null
                    currentPage = page
                }
            )
        }
    ) { innerPadding ->
        if (selectedShop != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(skyBg)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ShopDetailPage(
                        shop = selectedShop!!,
                        roboto = roboto
                    )
                }
                item {
                    AppFooter(roboto = roboto)
                }
            }
        } else if (currentPage == "Home") {
            LazyColumn(
                state = homeListState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(skyBg)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                if (isLoadingShops) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1E3A8A))
                        }
                    }
                } else if (visibleShops.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No active shops found in database.",
                                fontFamily = roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    items(visibleShops, key = { it.id }) { shop ->
                        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                            ShopItemCard(
                                shop = shop,
                                roboto = roboto,
                                onOpenShop = { selectedShop = it }
                            )
                        }
                    }
                }

                item {
                    AppFooter(roboto = roboto)
                }
            }
        } else {
            LazyColumn(
                state = jobListState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(skyBg)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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

                if (isLoadingJobs) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1E3A8A))
                        }
                    }
                } else if (jobList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No jobs posted yet. Be the first to create one!",
                                fontFamily = roboto,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                } else {
                    items(jobList, key = { it.id }) { job ->
                        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                            JobPostCard(
                                job = job,
                                roboto = roboto,
                                onViewJob = { selectedJob = it }
                            )
                        }
                    }
                }

                item {
                    AppFooter(roboto = roboto)
                }
            }
        }
    }

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

    if (selectedJob != null) {
        CompactJobCardPopup(
            job = selectedJob!!,
            roboto = roboto,
            onDismiss = { selectedJob = null }
        )
    }

    if (showAddShopModal) {
        AddShopWithImageUploadDialog(
            roboto = roboto,
            onDismiss = { showAddShopModal = false }
        )
    }

    if (showCreateJobModal) {
        CreateJobDialog(
            roboto = roboto,
            onDismiss = { showCreateJobModal = false }
        )
    }
}

// Why we use this
// Compact footer.
@Composable
fun AppFooter(roboto: FontFamily) {
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E3A8A))
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        Text(
            text = "Savarkundla.in by PiCell Company.",
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

// Why we use this
// Brand Logo.
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

// Why we use this
// Reusable '+' button.
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

// Why we use this
// Bottom navigation bar.
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

// Why we use this
// Shop Card displaying shop image directly loaded from Firebase Storage.
@Composable
fun ShopItemCard(
    shop: ShopItem,
    roboto: FontFamily,
    onOpenShop: (ShopItem) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenShop(shop) },
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

                CompactCornerBadge(
                    rating = shop.rating,
                    reviewCount = shop.reviewCount,
                    shopType = shop.shopType,
                    roboto = roboto,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = shop.name,
                    fontFamily = roboto,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFCBD5E1))
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = shop.shortAddress,
                    fontFamily = roboto,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = Color(0xFF1E3A8A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFCBD5E1))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PremiumShopButton(
                        text = "View",
                        roboto = roboto,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenShop(shop) }
                    )

                    PremiumShopButton(
                        text = "Call",
                        roboto = roboto,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}")))
                        }
                    )

                    PremiumShopButton(
                        text = "WhatsApp",
                        roboto = roboto,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val link = "https://api.whatsapp.com/send?phone=91${shop.phone}&text=Hi%20${shop.name},%20I%20found%20your%20shop%20on%20Savarkundla.in!"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                        }
                    )
                }
            }
        }
    }
}

// Why we use this
// Bottom-aligned button text.
@Composable
fun PremiumShopButton(
    text: String,
    roboto: FontFamily,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(premiumBlackSkyBlueBrush)
            .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.7f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(bottom = 6.dp)
    ) {
        Text(
            text = text,
            fontFamily = roboto,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = Color.White
        )
    }
}

// Why we use this
// Full Shop Page.
@Composable
fun ShopDetailPage(
    shop: ShopItem,
    roboto: FontFamily
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(0.7.dp, Color.Black.copy(alpha = 0.28f)),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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

                    CompactCornerBadge(
                        rating = shop.rating,
                        reviewCount = shop.reviewCount,
                        shopType = shop.shopType,
                        roboto = roboto,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = shop.name,
                        fontFamily = roboto,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E3A8A)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFCBD5E1))
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = shop.shortAddress,
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1E3A8A)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFCBD5E1))
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Available Products & Services",
                        fontFamily = roboto,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = shop.itemsSold,
                        fontFamily = roboto,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        PremiumShopButton(
                            text = "Call",
                            roboto = roboto,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}")))
                            }
                        )

                        PremiumShopButton(
                            text = "WhatsApp",
                            roboto = roboto,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                val link = "https://api.whatsapp.com/send?phone=91${shop.phone}&text=Hi%20${shop.name},%20I%20saw%20your%20shop%20page%20on%20Savarkundla.in!"
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                            }
                        )
                    }
                }
            }
        }
    }
}

// Why we use this
// Job Card displaying data directly synced from Firebase Firestore.
@Composable
fun JobPostCard(
    job: JobPost,
    roboto: FontFamily,
    onViewJob: (JobPost) -> Unit
) {
    val context = LocalContext.current
    val darkBluePrefixColor = Color(0xFF0F296B)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(0.7.dp, Color.Black.copy(alpha = 0.28f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    color = Color(0xFF800000).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomStart = 10.dp),
                    border = BorderStroke(0.8.dp, Color(0xFF800000).copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "GST: ${job.gstNumber}",
                        fontFamily = roboto,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 11.sp,
                        color = Color(0xFF800000),
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Job : ",
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = darkBluePrefixColor
                    )
                    Text(
                        text = job.roleTitle,
                        fontFamily = roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFCBD5E1))
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "At : ",
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = darkBluePrefixColor
                    )
                    Text(
                        text = job.shopName,
                        fontFamily = roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFCBD5E1))
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Sal : ",
                        fontFamily = roboto,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = darkBluePrefixColor
                    )
                    Text(
                        text = job.salary,
                        fontFamily = roboto,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(thickness = 0.6.dp, color = Color(0xFFCBD5E1))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onViewJob(job) },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF1E3A8A)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1E3A8A)
                        )
                    ) {
                        Text("View", fontFamily = roboto, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            val link = "https://api.whatsapp.com/send?phone=91${job.interviewerNumber}&text=Hi%20${job.shopName},%20I%20am%20applying%20for%20${job.roleTitle}%20on%20Savarkundla.in!"
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                    ) {
                        Text("Apply", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

// Why we use this
// Compact Job Details modal.
@Composable
fun CompactJobCardPopup(
    job: JobPost,
    roboto: FontFamily,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = BorderStroke(0.8.dp, Color.Black.copy(alpha = 0.28f)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .heightIn(max = 480.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Job Details",
                        fontFamily = roboto,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF1E3A8A)
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF64748B),
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.2.dp, color = Color(0xFF1E3A8A))
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PopupLineRow(prefix = "Job : ", value = job.roleTitle, roboto = roboto, isJobNameHighlight = true)
                    PopupLineRow(prefix = "Business : ", value = job.shopName, roboto = roboto, isBusinessHighlight = true)
                    PopupLineRow(prefix = "Sal : ", value = job.salary, roboto = roboto)
                    PopupLineRow(prefix = "Edu : ", value = job.education, roboto = roboto)
                    PopupLineRow(prefix = "Exp : ", value = job.experience, roboto = roboto)
                    PopupLineRow(prefix = "Time : ", value = job.jobTime, roboto = roboto)
                    PopupLineRow(prefix = "Address : ", value = job.fullAddress, roboto = roboto)
                    PopupLineRow(prefix = "About : ", value = job.aboutJob, roboto = roboto)
                    PopupLineRow(prefix = "Interviewer : ", value = job.interviewerName, roboto = roboto)
                    PopupLineRow(prefix = "Number : ", value = job.interviewerNumber, roboto = roboto)
                    PopupLineRow(prefix = "GST : ", value = job.gstNumber, roboto = roboto)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text("Close", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

// Why we use this
// Formats single popup lines.
@Composable
fun PopupLineRow(
    prefix: String,
    value: String,
    roboto: FontFamily,
    isJobNameHighlight: Boolean = false,
    isBusinessHighlight: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = prefix,
                fontFamily = roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF0F296B)
            )
            Text(
                text = value,
                fontFamily = roboto,
                fontWeight = when {
                    isJobNameHighlight || isBusinessHighlight -> FontWeight.ExtraBold
                    else -> FontWeight.SemiBold
                },
                fontSize = when {
                    isJobNameHighlight -> 15.sp
                    else -> 14.sp
                },
                color = when {
                    isJobNameHighlight -> Color(0xFF1E3A8A)
                    isBusinessHighlight -> Color(0xFFFF6B00)
                    else -> Color.Black
                }
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFCBD5E1))
    }
}

// Why we use this
// Allows selecting and uploading shop images to Firebase Storage and saving them directly to Firestore.
@Composable
fun AddShopWithImageUploadDialog(
    roboto: FontFamily,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var shopName by remember { mutableStateOf("") }
    var shopCategory by remember { mutableStateOf("Electronics") }
    var address by remember { mutableStateOf("") }
    var itemsSold by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        containerColor = Color.White,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIST NEW SHOP",
                        fontFamily = roboto,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF1E3A8A)
                    )
                    Surface(
                        color = Color(0xFFFFF7ED),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(0.8.dp, Color(0xFFFF6B00))
                    ) {
                        Text(
                            text = "₹2,000 / mo",
                            fontFamily = roboto,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = Color(0xFFFF6B00),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(thickness = 1.5.dp, color = Color(0xFF1E3A8A))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Image Picker Box connected to Firebase Storage
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                        .clickable { photoPickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Shop Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add", tint = Color(0xFF1E3A8A), modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Select Shop Photo (Storage)", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E3A8A))
                        }
                    }
                }

                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Shop Name", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = shopCategory,
                    onValueChange = { shopCategory = it },
                    label = { Text("Category (e.g. Electronics, Clothing)", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Short Address", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = itemsSold,
                    onValueChange = { itemsSold = it },
                    label = { Text("Products Sold (e.g. Mobile, AC)", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Contact Phone", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (isUploading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color(0xFF1E3A8A))
                        Text("Uploading image to Firebase Storage...", fontFamily = roboto, fontSize = 12.sp, color = Color(0xFF1E3A8A))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isUploading,
                onClick = {
                    if (shopName.isBlank() || phone.isBlank()) {
                        Toast.makeText(context, "Fill Shop Name and Phone", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isUploading = true
                    val firestore = FirebaseFirestore.getInstance()
                    val storage = FirebaseStorage.getInstance()

                    // Upload photo to Firebase Storage if selected, else default placeholder
                    if (selectedImageUri != null) {
                        val imageRef = storage.reference.child("Shops/${UUID.randomUUID()}.jpg")
                        imageRef.putFile(selectedImageUri!!)
                            .addOnSuccessListener {
                                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                                    val newDoc = firestore.collection("Shops").document()
                                    val shopData = hashMapOf(
                                        "name" to shopName.trim(),
                                        "shopType" to shopCategory.trim(),
                                        "shortAddress" to address.trim(),
                                        "itemsSold" to itemsSold.trim(),
                                        "phone" to phone.trim(),
                                        "imageUrl" to downloadUrl.toString(),
                                        "rating" to 4.5,
                                        "reviewCount" to 1,
                                        "isActive" to true
                                    )
                                    newDoc.set(shopData).addOnCompleteListener {
                                        isUploading = false
                                        onDismiss()
                                    }
                                }
                            }
                            .addOnFailureListener {
                                isUploading = false
                                Toast.makeText(context, "Upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        val newDoc = firestore.collection("Shops").document()
                        val shopData = hashMapOf(
                            "name" to shopName.trim(),
                            "shopType" to shopCategory.trim(),
                            "shortAddress" to address.trim(),
                            "itemsSold" to itemsSold.trim(),
                            "phone" to phone.trim(),
                            "imageUrl" to "https://images.unsplash.com/photo-1550009158-9ebf69173e03?w=800&q=80",
                            "rating" to 4.5,
                            "reviewCount" to 1,
                            "isActive" to true
                        )
                        newDoc.set(shopData).addOnCompleteListener {
                            isUploading = false
                            onDismiss()
                        }
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SAVE SHOP TO FIREBASE", fontFamily = roboto, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isUploading, modifier = Modifier.fillMaxWidth()) {
                Text("CANCEL", fontFamily = roboto, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    )
}

// Why we use this
// Filter Dialog.
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

// Why we use this
// Saves newly created jobs directly into Firebase Firestore "Jobs" collection.
@Composable
fun CreateJobDialog(
    roboto: FontFamily,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var roleTitle by remember { mutableStateOf("") }
    var shopName by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var jobTime by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var aboutJob by remember { mutableStateOf("") }
    var interviewerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var gstNumber by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { if (!isPosting) onDismiss() },
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
                    label = { Text("Job Role (e.g., Sales Executive)", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = shopName,
                    onValueChange = { shopName = it },
                    label = { Text("Business / Shop Name", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = salary,
                    onValueChange = { salary = it },
                    label = { Text("Monthly Salary", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = education,
                    onValueChange = { education = it },
                    label = { Text("Education Required", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = experience,
                    onValueChange = { experience = it },
                    label = { Text("Experience Required", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = jobTime,
                    onValueChange = { jobTime = it },
                    label = { Text("Job Timing (e.g., 9 AM - 8 PM)", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Full Address", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = aboutJob,
                    onValueChange = { aboutJob = it },
                    label = { Text("About Job Responsibilities", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = interviewerName,
                    onValueChange = { interviewerName = it },
                    label = { Text("Interviewer / Contact Person", fontFamily = roboto) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Interviewer Mobile Number", fontFamily = roboto) },
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
                enabled = !isPosting,
                onClick = {
                    when {
                        gstNumber.isBlank() -> errorMsg = "GST Number is compulsory to create a job!"
                        roleTitle.isBlank() || shopName.isBlank() || phone.isBlank() ->
                            errorMsg = "Please fill Job Role, Shop Name, and Mobile Number."
                        else -> {
                            isPosting = true
                            val firestore = FirebaseFirestore.getInstance()
                            val newDoc = firestore.collection("Jobs").document()

                            val jobMap = hashMapOf(
                                "roleTitle" to roleTitle.trim(),
                                "shopName" to shopName.trim(),
                                "salary" to if (salary.isBlank()) "Negotiable" else salary.trim(),
                                "education" to if (education.isBlank()) "Open for All" else education.trim(),
                                "experience" to if (experience.isBlank()) "Freshers Welcome" else experience.trim(),
                                "jobTime" to if (jobTime.isBlank()) "Full Time" else jobTime.trim(),
                                "fullAddress" to if (address.isBlank()) "Savarkundla" else address.trim(),
                                "aboutJob" to if (aboutJob.isBlank()) "General store duties." else aboutJob.trim(),
                                "interviewerName" to if (interviewerName.isBlank()) "Manager" else interviewerName.trim(),
                                "interviewerNumber" to phone.trim(),
                                "gstNumber" to gstNumber.trim(),
                                "isActive" to true
                            )

                            newDoc.set(jobMap)
                                .addOnSuccessListener {
                                    isPosting = false
                                    Toast.makeText(context, "Job successfully posted!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                }
                                .addOnFailureListener {
                                    isPosting = false
                                    errorMsg = "Failed to post job: ${it.message}"
                                }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
            ) {
                if (isPosting) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White)
                } else {
                    Text("Post Job", fontFamily = roboto, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isPosting) {
                Text("Close", fontFamily = roboto, fontWeight = FontWeight.Bold)
            }
        }
    )
}
