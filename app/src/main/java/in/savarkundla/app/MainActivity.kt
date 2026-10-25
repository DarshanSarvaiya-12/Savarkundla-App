package `in`.savarkundla.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SavarkundlaTheme {
                SavarkundlaApp()
            }
        }
    }
}

// Loads Bricolage Grotesque for the Savarkundla.in Logo
@Composable
fun rememberBricolageGrotesqueFont(): FontFamily {
    val context = LocalContext.current
    return remember {
        val fontResId = context.resources.getIdentifier(
            "bricolage_grotesque",
            "font",
            context.packageName
        )
        if (fontResId != 0) {
            FontFamily(Font(fontResId, FontWeight.Bold))
        } else {
            FontFamily.SansSerif
        }
    }
}

// Forces Roboto (FontFamily.SansSerif) as the default font across the entire app
@Composable
fun SavarkundlaTheme(content: @Composable () -> Unit) {
    val defaultTypo = Typography()
    val roboto font = FontFamily.SansSerif

    val robotoTypography = Typography(
        displayLarge = defaultTypo.displayLarge.copy(fontFamily = robotoFont),
        displayMedium = defaultTypo.displayMedium.copy(fontFamily = robotoFont),
        displaySmall = defaultTypo.displaySmall.copy(fontFamily = robotoFont),
        headlineLarge = defaultTypo.headlineLarge.copy(fontFamily = robotoFont),
        headlineMedium = defaultTypo.headlineMedium.copy(fontFamily = robotoFont),
        headlineSmall = defaultTypo.headlineSmall.copy(fontFamily = robotoFont),
        titleLarge = defaultTypo.titleLarge.copy(fontFamily = robotoFont, fontWeight = FontWeight.Bold),
        titleMedium = defaultTypo.titleMedium.copy(fontFamily = robotoFont, fontWeight = FontWeight.Bold),
        titleSmall = defaultTypo.titleSmall.copy(fontFamily = robotoFont, fontWeight = FontWeight.SemiBold),
        bodyLarge = defaultTypo.bodyLarge.copy(fontFamily = robotoFont),
        bodyMedium = defaultTypo.bodyMedium.copy(fontFamily = robotoFont),
        bodySmall = defaultTypo.bodySmall.copy(fontFamily = robotoFont),
        labelLarge = defaultTypo.labelLarge.copy(fontFamily = robotoFont, fontWeight = FontWeight.Medium),
        labelMedium = defaultTypo.labelMedium.copy(fontFamily = robotoFont),
        labelSmall = defaultTypo.labelSmall.copy(fontFamily = robotoFont)
    )

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1E3A8A),
            secondary = Color(0xFFD97706),
            background = Color(0xFFF1F5F9),
            surface = Color.White
        ),
        typography = robotoTypography,
        content = content
    )
}

@Composable
fun SavarkundlaApp() {
    val bricolageFont = rememberBricolageGrotesqueFont()

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
            // HEADER: Logo, Add Button, Compact Search Bar, and Filter Button
            Surface(
                shadowElevation = 4.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    // Header Row 1: Logo + Add Button
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
                                        fontSize = 25.sp,
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
                                        fontSize = 25.sp,
                                        letterSpacing = (-0.5).sp,
                                        color = Color(0xFFD97706)
                                    )
                                ) {
                                    append(".in")
                                }
                            }
                        )

                        Button(
                            onClick = { showAddShopDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A)
                            )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Shop",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Header Row 2: Compact 42dp Search Bar + 42dp Filter Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Sleek 42dp Search Bar
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = FontFamily.SansSerif,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            ),
                            cursorBrush = SolidColor(Color(0xFF1E3A8A)),
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp)
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

                        // Sleek 42dp Filter Button
                        val isFiltered = selectedCategory != "All"
                        OutlinedButton(
                            onClick = { showFilterDialog = true },
                            modifier = Modifier.height(42.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isFiltered) Color(0xFFD97706) else Color(0xFFCBD5E1)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isFiltered) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                                contentColor = if (isFiltered) Color(0xFFD97706) else Color(0xFF1E3A8A)
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
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // SHOP CARDS LIST BELOW HEADER
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredShops) { shop ->
                ShopCard(shop = shop)
            }
        }
    }

    // Category Filter Dialog
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter by Shop Type", fontWeight = FontWeight.Bold) },
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
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFilterDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Add Shop Dialog (₹2,000/month)
    if (showAddShopDialog) {
        AlertDialog(
            onDismissRequest = { showAddShopDialog = false },
            title = { Text("List Your Shop on Savarkundla.in", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Grow your business in Savarkundla!\n\n" +
                    "• Full Shop Banner Photo & Profile\n" +
                    "• Appear in Customer Product Searches\n" +
                    "• Direct Call & WhatsApp Leads\n\n" +
                    "Subscription Plan: ₹2,000 / month"
                )
            },
            confirmButton = {
                Button(onClick = { showAddShopDialog = false }) {
                    Text("Join via WhatsApp")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddShopDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun ShopCard(shop: Shop) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. Shop Image with Floating Shop Type Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(165.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                AsyncImage(
                    model = shop.imageUrl,
                    contentDescription = shop.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Shop Type Badge (Top-Left over Image)
                Surface(
                    color = Color(0xFF0F172A).copy(alpha = 0.82f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = shop.shopType,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                // Reviews Pill (Bottom-Right over Image)
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
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color(0xFF0F172A)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "(${shop.reviewCount} reviews)",
                            fontSize = 11.sp,
                            color = Color(0xFF475569)
                        )
                    }
                }
            }

            // 2. Shop Info Section
            Column(modifier = Modifier.padding(16.dp)) {
                // Shop Name
                Text(
                    text = shop.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Short Shop Address
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
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Call & WhatsApp Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Call Button
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
                        Text("Call", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    // WhatsApp Button
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
                        Text("WhatsApp", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
