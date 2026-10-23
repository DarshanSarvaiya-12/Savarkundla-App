package `in`.savarkundla.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Data Model for Shops (Ready for Firebase in Phase 2)
data class Shop(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val itemsSold: String = "",
    val address: String = "",
    val phone: String = "",
    val isVerified: Boolean = true
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

// 2. Custom Theme enforcing Roboto (SansSerif) globally
@Composable
fun SavarkundlaTheme(content: @Composable () -> Unit) {
    val robotoTypography = Typography(
        bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp),
        bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp),
        titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 18.sp),
        labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    )

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF1E3A8A),     // Deep Royal Blue
            secondary = Color(0xFFD97706),   // Warm Amber/Gold
            background = Color(0xFFF8FAFC),
            surface = Color.White
        ),
        typography = robotoTypography,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavarkundlaApp() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showRegisterDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Electronics", "Clothing", "Grocery", "Hardware & Scales", "Medical", "Food")

    // Sample Savarkundla Shops for Phase 1
    val sampleShops = remember {
        listOf(
            Shop(
                id = "1",
                name = "Kundla Weighing Scales & Hardware",
                category = "Hardware & Scales",
                itemsSold = "Digital scales, industrial weights, iron tools, hardware items",
                address = "Main Bazaar, Navapara, Savarkundla",
                phone = "9876543210"
            ),
            Shop(
                id = "2",
                name = "Patel Electronics & Mobile",
                category = "Electronics",
                itemsSold = "iPhones, Samsung phones, Smart TVs, AC, Refrigerators, washing machines",
                address = "Jeshar Road, Near Bus Stand, Savarkundla",
                phone = "9876543211"
            ),
            Shop(
                id = "3",
                name = "Shreeji Fashion Hub",
                category = "Clothing",
                itemsSold = "Men's wear, jeans, shirts, wedding sherwani, sarees, kids wear",
                address = "Ridhhi Siddhi Complex, Mahuva Road, Savarkundla",
                phone = "9876543212"
            ),
            Shop(
                id = "4",
                name = "Bapa Sitaram Supermart",
                category = "Grocery",
                itemsSold = "Atta, spices, dry fruits, cold drinks, daily snacks, household items",
                address = "Amreli Road, Savarkundla",
                phone = "9876543213"
            )
        )
    }

    val filteredShops = sampleShops.filter { shop ->
        val matchesCategory = selectedCategory == "All" || shop.category == selectedCategory
        val matchesSearch = shop.name.contains(searchQuery, ignoreCase = true) ||
                shop.itemsSold.contains(searchQuery, ignoreCase = true) ||
                shop.category.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 4.dp, color = Color.White) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LOGO: Georgia / Serif Font
                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = FontFamily.Serif, // Maps to Georgia style
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    color = Color(0xFF1E3A8A)
                                )
                            ) {
                                append("Savarkundla")
                            }
                            withStyle(
                                style = SpanStyle(
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 26.sp,
                                    color = Color(0xFFD97706)
                                )
                            ) {
                                append(".in")
                            }
                        }
                    )

                    TextButton(onClick = { showRegisterDialog = true }) {
                        Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("List Shop", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Search Bar ("Find anything you want to buy")
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search products (e.g., Mobile, Saree, Scale)...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            // 2. Categories Row
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 3. Shop Owner ₹2,000/month Banner
            item {
                MerchantPromoBanner(onJoinClick = { showRegisterDialog = true })
            }

            // 4. Shop Listings
            items(filteredShops) { shop ->
                ShopCard(shop = shop)
            }
        }
    }

    if (showRegisterDialog) {
        AlertDialog(
            onDismissRequest = { showRegisterDialog = false },
            title = { Text("Partner with Savarkundla.in", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Grow your business across Savarkundla!\n\n" +
                    "• Premium Verified Shop Profile\n" +
                    "• Unlimited Product Keywords\n" +
                    "• Direct Customer Calls & WhatsApp\n\n" +
                    "Subscription: ₹2,000 / month"
                )
            },
            confirmButton = {
                Button(onClick = { showRegisterDialog = false }) {
                    Text("Contact Admin on WhatsApp")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegisterDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun MerchantPromoBanner(onJoinClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF3B82F6))
                )
            )
            .clickable { onJoinClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Own a Shop in Savarkundla?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Get listed so customers find your products instantly. Just ₹2,000/month.",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                color = Color(0xFFD97706),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Join Now",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ShopCard(shop: Shop) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = shop.category,
                        color = Color(0xFF1E3A8A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                if (shop.isVerified) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verified Partner", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = shop.name, style = MaterialTheme.typography.titleMedium, color = Color(0xFF0F172A))

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Available: ${shop.itemsSold}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF475569)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = "Address",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = shop.address, fontSize = 13.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${shop.phone}"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Shop")
                }

                OutlinedButton(
                    onClick = {
                        val url = "https://api.whatsapp.com/send?phone=91${shop.phone}&text=Hi%20${shop.name},%20I%20found%20your%20shop%20on%20Savarkundla.in%20app!"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("WhatsApp")
                }
            }
        }
    }
}
