package `in`.savarkundla.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

// Loads Bricolage Grotesque from res/font if present, with safe fallback so builds never fail
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

// Global Roboto (SansSerif) Theme for the whole app
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
            secondary = Color(0xFFD97706),   // Warm Amber
            background = Color(0xFFF1F5F9),
            surface = Color.White
        ),
        typography = robotoTypography,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
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
            // HEADER: Contains Logo, Add Button, Search Bar, and Filter Button
            Surface(
                shadowElevation = 6.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Header Row 1: Logo + Add Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Savarkundla.in Logo in Bricolage Grotesque
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
                                        color = Color(0xFFD97706)
                                    )
                                ) {
                                    append(".in")
                                }
                            }
                        )

                        // Upper Right Corner: Add Button
                        Button(
                            onClick = { showAddShopDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E3A8A)
                            )
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add Shop",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Header Row 2: Search Bar (left) + Filter Button (right)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Search anything to buy...",
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF1E3A8A)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedBorderColor = Color(0xFF1E3A8A),
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )

                        // Right side of Search Bar: Filter Button
                        val isFiltered = selectedCategory != "All"
                        OutlinedButton(
                            onClick = { showFilterDialog = true },
                            modifier = Modifier.height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isFiltered) Color(0xFFD97706) else Color(0xFFCBD5E1)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isFiltered) Color(0xFFFFFBEB) else Color(0xFFF8FAFC),
                                contentColor = if (isFiltered) Color(0xFFD97706) else Color(0xFF1E3A8A)
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp)
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = "Filter",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFiltered) selectedCategory else "Filter",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        // SHOP CARDS START IMMEDIATELY BELOW THE HEADER
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredShops) { shop ->
                ShopCard(shop = shop)
            }
        }
    }

    // Filter Popup Dialog when clicking "Filter" button
    if (showFilterDialog) {
        AlertDialog(
            onDismissRequest = { showFilterDialog = false },
            title = { Text("Filter by Category", fontWeight = FontWeight.Bold) },
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

    // Add Shop Popup Dialog when clicking "+ Add" button
    if (showAddShopDialog) {
        AlertDialog(
            onDismissRequest = { showAddShopDialog = false },
            title = { Text("List Your Shop on Savarkundla.in", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Reach every customer in Savarkundla!\n\n" +
                    "• Verified Shop Listing\n" +
                    "• Unlimited Products in Search\n" +
                    "• Direct Customer Call & WhatsApp\n\n" +
                    "Membership Plan: ₹2,000 / month"
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
                        Text(
                            "Verified Partner",
                            fontSize = 12.sp,
                            color = Color(0xFF16A34A),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = shop.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFF0F172A)
            )

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
