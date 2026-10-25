package `in`.savarkundla.app

// Why we use this: Imports Android tools, Compose UI components, and Coil image loader.
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// Why we use this: Blueprint for shop details, ready for Firebase in Phase 2.
data class Shop(
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

// Why we use this: Hides the system title bar and launches the app UI.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        setContent {
            SavarkundlaTheme {
                SavarkundlaApp()
            }
        }
    }
}

// Why we use this: Loads the real Bricolage Grotesque font file for the logo.
@Composable
fun rememberBricolageFont(): FontFamily {
    val context = LocalContext.current
    return remember {
        val id = context.resources.getIdentifier("bricolage_grotesque", "font", context.packageName)
        if (id != 0) FontFamily(Font(id, FontWeight.Bold)) else FontFamily.SansSerif
    }
}

// Why we use this: Loads the real Roboto font files so the phone's default font is ignored.
@Composable
fun rememberRobotoFont(): FontFamily {
    val context = LocalContext.current
    return remember {
        val regId = context.resources.getIdentifier("roboto_regular", "font", context.packageName)
        val boldId = context.resources.getIdentifier("roboto_bold", "font", context.packageName)
        if (regId != 0 && boldId != 0) {
            FontFamily(
                Font(regId, FontWeight.Normal),
                Font(regId, FontWeight.Medium),
                Font(boldId, FontWeight.SemiBold),
                Font(boldId, FontWeight.Bold)
            )
        } else {
            FontFamily.SansSerif
        }
    }
}

// Why we use this: Applies Roboto font and brand colors across the entire app.
@Composable
fun SavarkundlaTheme(content: @Composable () -> Unit) {
    val robotoFont = rememberRobotoFont()
    val base = Typography()

    val robotoTypography = Typography(
        displayLarge = base.displayLarge.copy(fontFamily = robotoFont),
        displayMedium = base.displayMedium.copy(fontFamily = robotoFont),
        displaySmall = base.displaySmall.copy(fontFamily = robotoFont),
        headlineLarge = base.headlineLarge.copy(fontFamily = robotoFont),
        headlineMedium = base.headlineMedium.copy(fontFamily = robotoFont),
        headlineSmall = base.headlineSmall.copy(fontFamily = robotoFont),
        titleLarge = base.titleLarge.copy(fontFamily = robotoFont, fontWeight = FontWeight.Bold),
        titleMedium = base.titleMedium.copy(fontFamily = robotoFont, fontWeight = FontWeight.Bold),
        titleSmall = base.titleSmall.copy(fontFamily = robotoFont, fontWeight = FontWeight.SemiBold),
        bodyLarge = base.bodyLarge.copy(fontFamily = robotoFont),
        bodyMedium = base.bodyMedium.copy(fontFamily = robotoFont),
        bodySmall = base.bodySmall.copy(fontFamily = robotoFont),
        labelLarge = base.labelLarge.copy(fontFamily = robotoFont, fontWeight = FontWeight.Medium),
        labelMedium = base.labelMedium.copy(fontFamily = robotoFont),
        labelSmall = base.labelSmall.copy(fontFamily = robotoFont)
    )

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1E3A8A),
            secondary = Color(0xFFFF6B00),
            background = Color(0xFFF8FAFC),
            surface = Color.White
        ),
        typography = robotoTypography
    ) {
        ProvideTextStyle(value = TextStyle(fontFamily = robotoFont)) {
            content()
        }
    }
}

// Why we use this: Manages search, category filtering, header layout, and shop list.
@Composable
fun SavarkundlaApp() {
    val bricolageFont = rememberBricolageFont()
    val robotoFont = rememberRobotoFont()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showAddShopDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Electronics", "Clothing", "Grocery", "Hardware & Scales", "Medical", "Food")

    val sampleShops = remember {
        listOf(
            Shop(
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
            Shop(
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
            Shop(
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
            Shop(
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

    // Why we use this: Filters shops in real time based on search text and category.
    val filteredShops = sampleShops.filter { shop ->
        val matchesCategory = selectedCategory == "All" || shop.shopType == selectedCategory
        val matchesSearch = shop.name.contains(searchQuery, ignoreCase = true) ||
                shop.itemsSold.contains(searchQuery, ignoreCase = true) ||
                shop.shopType.contains(searchQuery, ignoreCase = true) ||
                shop.shortAddress.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            // Why we use this: Header with downward orange shadow effect.
            Column(modifier = Modifier.fillMaxWidth()) {
                Surface(
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 10.dp,
                            spotColor = Color(0xFFFF6B00),
                            ambientColor = Color(0xFFFF6B00)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        // Why we use this: Top row with Bricolage Grotesque logo and bold '+' button.
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = bricolageFont,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 26.sp,
                                            letterSpacing = (-0.5).sp,
                                            color = Color(0xFF1E3A8A)
                                        )
                                    ) {
                                        append("Savarkundla")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontFamily = bricolageFont,
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

                            // Why we use this: Bold '+' icon button in upper right corner.
                            Surface(
                                onClick = { showAddShopDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF1E3A8A),
                                modifier = Modifier.size(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Shop",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Why we use this: Second row with compact 40dp Search bar and Filter button.
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontFamily = robotoFont,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A)
                                ),
                                cursorBrush = SolidColor(Color(0xFF1E3A8A)),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp),
                                decorationBox = { innerTextField ->
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
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(modifier = Modifier.weight(1f)) {
                                            if (searchQuery.isEmpty()) {
                                                Text(
                                                    text = "Search shops or products...",
                                                    fontFamily = robotoFont,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF94A3B8)
                                                )
                                            }
                                            innerTextField()
                                        }
                                        if (searchQuery.isNotEmpty()) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Clear",
                                                tint = Color(0xFF64748B),
                                                modifier = Modifier
                                                    .size(18.dp)
                                                    .clickable { searchQuery = "" }
                                            )
                                        }
                                    }
                                }
                            )

                            val isFiltered = selectedCategory != "All"
                            OutlinedButton(
                                onClick = { showFilterDialog = true },
                                modifier = Modifier.height(40.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isFiltered) Color(0xFFFF6B00) else Color(0xFFCBD5E1)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isFiltered) Color(0xFFFFF7ED) else Color(0xFFF8FAFC),
                                    contentColor = if (isFiltered) Color(0xFFFF6B00) else Color(0xFF1E3A8A)
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isFiltered) selectedCategory else "Filter",
                                    fontFamily = robotoFont,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                // Why we use this: Casts a visible downward orange shadow glow below the header.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF6B00).copy(alpha = 0.30f),
                                    Color.Transparent
                                )
                            )
                        )
                )
            }
        }
    ) { paddingValues ->
        // Why we use this: Scrollable list of Shop Cards starting right below the header.
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredShops) { shop ->
                ShopCard(shop = shop, robotoFont = robotoFont)
            }
        }
    }

    // Why we use this: Popup dialog to filter shops by category.
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter by Shop Type", fontFamily = robotoFont, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) Color(0xFFEFF6FF) else Color.Transparent,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory = category
                                    showFilterDialog = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category,
                                    fontFamily = robotoFont,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFF1E293B)
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF1E3A8A),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    selectedCategory = "All"
                    showFilterDialog = false
                }) {
                    Text("Reset", fontFamily = robotoFont)
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Close", fontFamily = robotoFont)
                }
            }
        )
    }

    // Why we use this: Popup dialog showing the ₹2,000/month shop listing plan.
    if (showAddShopDialog) {
        AlertDialog(
            onDismissRequest = { showAddShopDialog = false },
            title = { Text("List Your Shop on Savarkundla.in", fontFamily = robotoFont, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "Grow your business in Savarkundla!\n\n" +
                            "• 1:1 Shop Photo & Verified Profile\n" +
                            "• Appear in Customer Product Searches\n" +
                            "• Direct Call & WhatsApp Leads\n\n" +
                            "Subscription Plan: ₹2,000 / month",
                    fontFamily = robotoFont
                )
            },
            confirmButton = {
                Button(onClick = { showAddShopDialog = false }) {
                    Text("Join via WhatsApp", fontFamily = robotoFont)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddShopDialog = false }) {
                    Text("Close", fontFamily = robotoFont)
                }
            }
        )
    }
}

// Why we use this: Modern Shop Card with 1:1 square image, rating, address, Call & WhatsApp.
@Composable
fun ShopCard(shop: Shop, robotoFont: FontFamily) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Why we use this: aspectRatio(1f) enforces a 1:1 square image for the shop.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color(0xFFE2E8F0))
            ) {
                AsyncImage(
                    model = shop.imageUrl,
                    contentDescription = shop.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Why we use this: Shows the Shop Type badge on the top-left of the 1:1 image.
                Surface(
                    color = Color(0xFF0F172A).copy(alpha = 0.82f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = shop.shopType,
                        fontFamily = robotoFont,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                // Why we use this: Shows star rating and number of reviews on the bottom-right.
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
                            fontFamily = robotoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "(${shop.reviewCount} reviews)",
                            fontFamily = robotoFont,
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            // Why we use this: Displays Shop Name, Short Address, and Call/WhatsApp buttons.
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = shop.name,
                    fontFamily = robotoFont,
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
                        fontFamily = robotoFont,
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
                    // Why we use this: Opens phone dialer with the shop's phone number.
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A8A)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Call", fontFamily = robotoFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    // Why we use this: Opens WhatsApp chat directly with the shop owner.
                    Button(
                        onClick = {
                            val url = "https://api.whatsapp.com/send?phone=91${shop.phone}&text=Hi%20${shop.name},%20I%20found%20your%20shop%20on%20Savarkundla.in!"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF16A34A)
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp", fontFamily = robotoFont, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
