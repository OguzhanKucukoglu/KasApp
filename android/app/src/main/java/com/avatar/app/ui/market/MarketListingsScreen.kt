package com.avatar.app.ui.market
import com.avatar.app.ui.components.KasAppPullRefreshLayout

import com.avatar.app.ui.theme.*

import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avatar.app.data.model.FarmerListing
import com.avatar.app.ui.theme.ForestGreen
import com.avatar.app.ui.theme.PrimaryBlue
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketListingsScreen(
 viewModel: MarketViewModel
) {
 val state by viewModel.uiState.collectAsState()
 var selectedListingForBuy by remember { mutableStateOf<FarmerListing?>(null) }
 val snackbarHostState = remember { SnackbarHostState() }

 LaunchedEffect(state.errorMessage) {
 state.errorMessage?.let {
 snackbarHostState.showSnackbar(it)
 viewModel.clearMessages()
 }
 }

 LaunchedEffect(state.successMessage) {
 state.successMessage?.let {
 snackbarHostState.showSnackbar(it)
 viewModel.clearMessages()
 }
 }

 selectedListingForBuy?.let { listing ->
 BuyProductDialog(
 listing = listing,
 isSubmitting = state.isPurchasing,
 onDismiss = { selectedListingForBuy = null },
 onConfirm = { qty ->
 viewModel.buyProduct(listing, qty) {
 selectedListingForBuy = null
 }
 }
 )
 }

 KasAppPullRefreshLayout(
        isRefreshing = state.isLoading,
        onRefresh = { viewModel.loadListings() },
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
 snackbarHost = { SnackbarHost(snackbarHostState) },
 topBar = {
 TopAppBar(
 title = {
 Column {
 Text("Market Toptan Pazar", fontWeight = FontWeight.Bold, fontSize = 17.sp)
 Text("İşletmelerin satışa sunduğu mamul ilanları", fontSize = 11.sp, color = Color.Gray)
 }
 },
 )
 }
 ) { padding ->
		Column(
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(14.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {
 // Arama Çubuğu
 OutlinedTextField(
 value = state.searchQuery,
 onValueChange = { viewModel.setSearchQuery(it) },
 placeholder = { Text("Ürün adı, üretici veya lot no ara...") },
 leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
 singleLine = true,
 modifier = Modifier.fillMaxWidth(),
 shape = RoundedCornerShape(12.dp)
 )

        // Liste
        if (state.isLoading && state.listings.isEmpty()) {
 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
 CircularProgressIndicator(color = PrimaryBlue)
 }
 } else if (state.filteredListings.isEmpty()) {
 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
 Column(horizontalAlignment = Alignment.CenterHorizontally) {
 Text("", fontSize = 48.sp)
 Spacer(modifier = Modifier.height(8.dp))
 Text(
 if (state.searchQuery.isBlank()) "Şu an satışta olan mamul ilanı bulunmuyor" else "Aramaya uygun ilan bulunamadı",
 fontWeight = FontWeight.SemiBold,
 fontSize = 14.sp
 )
 Spacer(modifier = Modifier.height(4.dp))
 Text("İşletmeler fason mamullerini satışa açtıkça burada görünecektir.", fontSize = 12.sp, color = Color.Gray)
 }
 }
 } else {
 LazyColumn(
 verticalArrangement = Arrangement.spacedBy(10.dp),
 modifier = Modifier.fillMaxSize()
 ) {
 items(state.filteredListings) { listing ->
 Card(
 modifier = Modifier.fillMaxWidth(),
 shape = RoundedCornerShape(12.dp),
 colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
 elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
 ) {
 Column(modifier = Modifier.padding(14.dp)) {
 Row(
 modifier = Modifier.fillMaxWidth(),
 horizontalArrangement = Arrangement.SpaceBetween,
 verticalAlignment = Alignment.CenterVertically
 ) {
 Column(modifier = Modifier.weight(1f)) {
 Text(listing.productName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
 Row(
 verticalAlignment = Alignment.CenterVertically,
 horizontalArrangement = Arrangement.spacedBy(6.dp)
 ) {
 Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, Color(0xFF7E22CE))
                                ) {
                                    Text(
                                        "Lot: ${listing.lotNumber}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF7E22CE),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
 Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, ForestGreen)
                                ) {
                                    Text(
                                        "Mevcut: ${listing.quantity} ${listing.unit}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForestGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
 }
 }

 Column(horizontalAlignment = Alignment.End) {
 Text(
 "${String.format(Locale.US, "%.2f", listing.unitPrice)} ${listing.currency ?: "EUR"}",
 fontWeight = FontWeight.Bold,
 fontSize = 16.sp,
 color = ForestGreen
 )
 Text("Birim Fiyat", fontSize = 10.sp, color = Color.Gray)
 }
 }

 Spacer(modifier = Modifier.height(8.dp))
 Row(verticalAlignment = Alignment.CenterVertically) {
 Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryBlue)
 Spacer(modifier = Modifier.width(4.dp))
 Text("Üretici: ${listing.farmerName ?: "İşletme"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
 if (!listing.phone.isNullOrBlank()) {
 Spacer(modifier = Modifier.width(8.dp))
 Text("• Tel: ${listing.phone}", fontSize = 11.sp, color = Color.Gray)
 }
 }

 if (!listing.description.isNullOrBlank()) {
 Spacer(modifier = Modifier.height(6.dp))
 Text(listing.description, fontSize = 11.sp, color = Color.DarkGray)
 }

 Spacer(modifier = Modifier.height(10.dp))
 HorizontalDivider(color = Color(0xFFF1F5F9))
 Spacer(modifier = Modifier.height(6.dp))

 Row(
 modifier = Modifier.fillMaxWidth(),
 horizontalArrangement = Arrangement.End
 ) {
 Button(
 onClick = { selectedListingForBuy = listing },
 colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
 shape = RoundedCornerShape(8.dp),
 contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
 ) {
 Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
 Spacer(modifier = Modifier.width(6.dp))
 Text("Satın Al", fontWeight = FontWeight.Bold, fontSize = 12.sp)
 }
 }
 }
 }
 }
 }
 }
        }
 }
 }
}
