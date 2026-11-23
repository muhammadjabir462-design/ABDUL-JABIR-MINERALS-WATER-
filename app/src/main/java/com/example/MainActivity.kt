package com.example

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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Order
import com.example.ui.OrderViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WhatsAppGreen
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    AJWaterApp(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AJWaterApp(
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form fields observed from ViewModel
    val clientName by viewModel.clientName.collectAsState()
    val clientAddress by viewModel.clientAddress.collectAsState()
    val quantityString by viewModel.quantityString.collectAsState()
    val orders by viewModel.allOrders.collectAsState()

    // Handle ViewModel success/error messages
    LaunchedEffect(key1 = true) {
        viewModel.messageFlow.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // 1. Hero Section (Header) with custom styling and splash background
        HeroSection()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 2. Products Section (Showcasing 19L Bottles)
            ProductsSection()

            // 3. Simple Order Form Section
            OrderFormSection(
                nameValue = clientName,
                onNameChange = { viewModel.clientName.value = it },
                addressValue = clientAddress,
                onAddressChange = { viewModel.clientAddress.value = it },
                qtyValue = quantityString,
                onQtyChange = { viewModel.quantityString.value = it },
                onPlaceOrder = { viewModel.placeOrder() }
            )

            // 4. Order History Section (Room DB)
            if (orders.isNotEmpty()) {
                OrderHistorySection(
                    orders = orders,
                    onUpdateStatus = { id, status -> viewModel.updateStatus(id, status) },
                    onDeleteOrder = { order -> viewModel.deleteOrder(order) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 5. Contact / Footer Section
        ContactFooterSection(context = context)
    }
}

@Composable
fun HeroSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        // High-quality background image of water splash
        Image(
            painter = painterResource(id = R.drawable.img_water_hero_1783989826848),
            contentDescription = "Pristine Mineral Water Hero Splash Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Dark translucent overlay for maximum text readability and high contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
        )

        // Hero Content overlaid beautifully
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Circular app logo on white background
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(Color.White, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon_1783989813310),
                        contentDescription = "Abdul Jabir Mineral Water Logo",
                        modifier = Modifier.clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                
                Column {
                    Text(
                        text = "Abdul Jabir",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )
                    Text(
                        text = "Mineral Water",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        lineHeight = 22.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Purity in every drop. Freshness at your doorstep.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ProductsSection() {
    Column {
        Text(
            text = "Our Premium Product",
            color = MaterialTheme.colorScheme.secondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image container on the left with custom background
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(20.dp))
                        .clip(RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_water_bottle_1783989842991),
                        contentDescription = "19 Litre Mineral Water Bottle Showcase",
                        modifier = Modifier.size(80.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Product info on the right
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Premium 19L Bottle",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Ozone-treated & UV-purified water for healthy living.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rs. 50 / Refill",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun OrderFormSection(
    nameValue: String,
    onNameChange: (String) -> Unit,
    addressValue: String,
    onAddressChange: (String) -> Unit,
    qtyValue: String,
    onQtyChange: (String) -> Unit,
    onPlaceOrder: () -> Unit
) {
    val currentQty = qtyValue.toIntOrNull() ?: 0
    val estimatedTotal = if (currentQty > 0) currentQty * 50 else 50

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Instant Order",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Price: Rs. $estimatedTotal",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Full Name Input
            OutlinedTextField(
                value = nameValue,
                onValueChange = onNameChange,
                placeholder = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "User Icon", tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("client_name_input")
            )

            // Delivery Address Input
            OutlinedTextField(
                value = addressValue,
                onValueChange = onAddressChange,
                placeholder = { Text("Delivery Address") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Location Icon", tint = MaterialTheme.colorScheme.primary) },
                singleLine = false,
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("client_address_input")
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Input
                OutlinedTextField(
                    value = qtyValue,
                    onValueChange = onQtyChange,
                    placeholder = { Text("Qty") },
                    leadingIcon = { Icon(Icons.Default.LocalDrink, contentDescription = "Water bottle icon", tint = MaterialTheme.colorScheme.primary) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier
                        .width(110.dp)
                        .testTag("quantity_input")
                )

                // Place Order Button
                Button(
                    onClick = onPlaceOrder,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("place_order_button")
                ) {
                    Text(
                        text = "Place Order",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OrderHistorySection(
    orders: List<Order>,
    onUpdateStatus: (Int, String) -> Unit,
    onDeleteOrder: (Order) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Order History Icon",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "My Order History",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${orders.size} total",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            orders.forEach { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Order ID: #${order.id}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            
                            // Badge indicating status
                            val badgeColor = when (order.status) {
                                "Delivered" -> WhatsAppGreen
                                "Cancelled" -> Color.Red
                                else -> MaterialTheme.colorScheme.primary // Pending
                            }
                            Surface(
                                color = badgeColor.copy(alpha = 0.15f),
                                contentColor = badgeColor,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = order.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = order.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = order.address,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Quantity: ${order.quantity} Bottle(s)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Total Price: Rs. ${order.totalPrice.toInt()}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Interactive order simulator flow
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (order.status == "Pending") {
                                    // Complete Order Button
                                    IconButton(
                                        onClick = { onUpdateStatus(order.id, "Delivered") },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Mark as Delivered",
                                            tint = WhatsAppGreen,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    
                                    // Cancel Order Button
                                    IconButton(
                                        onClick = { onUpdateStatus(order.id, "Cancelled") },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Cancel Order",
                                            tint = Color.Red,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                // Delete Order History item
                                IconButton(
                                    onClick = { onDeleteOrder(order) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Record",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactFooterSection(context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "GET IN TOUCH",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "0316 8983150",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "muhammadjabir462@gmail.com",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // WhatsApp Button (Direct launch)
                IconButton(
                    onClick = {
                        val waUrl = "https://api.whatsapp.com/send?phone=923168983150&text=Assalam-o-Alaikum, I am interested in ordering Abdul Jabir Mineral Water."
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(WhatsAppGreen, CircleShape)
                        .testTag("whatsapp_button")
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.stat_sys_phone_call), // Fallback indicator icon
                        contentDescription = "Connect on WhatsApp",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Email Button (Direct launch)
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:muhammadjabir462@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Abdul Jabir Mineral Water Order/Query")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "No email client found.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                        .testTag("email_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Send Email",
                        tint = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Phone Call Button (Direct launch)
                IconButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:03168983150"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Call Us Now",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
