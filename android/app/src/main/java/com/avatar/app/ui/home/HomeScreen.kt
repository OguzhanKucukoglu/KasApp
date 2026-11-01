@file:Suppress("DEPRECATION")
package com.avatar.app.ui.home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.avatar.app.R
import com.avatar.app.data.sync.SyncManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.avatar.app.data.model.User
import com.avatar.app.ui.admin.AdminUsersScreen
import com.avatar.app.ui.admin.AdminViewModel
import com.avatar.app.ui.auth.AuthViewModel
import com.avatar.app.ui.butchery.ButcheryScreen
import com.avatar.app.ui.butchery.ButcheryViewModel
import com.avatar.app.ui.components.ServerConfigDialog
import com.avatar.app.ui.invoices.InvoicesScreen
import com.avatar.app.ui.invoices.InvoicesViewModel
import com.avatar.app.ui.listings.FarmerListingsScreen
import com.avatar.app.ui.listings.FarmerListingsViewModel
import com.avatar.app.ui.listings.ProducerListingsManageScreen
import com.avatar.app.ui.listings.ProducerListingsScreen
import com.avatar.app.ui.listings.ProducerListingsViewModel
import com.avatar.app.ui.lots.LotsScreen
import com.avatar.app.ui.lots.LotsViewModel
import com.avatar.app.ui.market.MarketListingsScreen
import com.avatar.app.ui.market.MarketPurchasesScreen
import com.avatar.app.ui.market.MarketViewModel
import com.avatar.app.ui.notifications.NotificationsDialog
import com.avatar.app.ui.notifications.NotificationsViewModel
import com.avatar.app.ui.orders.OrdersScreen
import com.avatar.app.ui.orders.OrdersViewModel
import com.avatar.app.ui.production.ProductionScreen
import com.avatar.app.ui.production.ProductionViewModel
import com.avatar.app.ui.rawmaterials.RawMaterialsScreen
import com.avatar.app.ui.rawmaterials.RawMaterialsViewModel
import com.avatar.app.ui.recalls.RecallsScreen
import com.avatar.app.ui.recalls.RecallsViewModel
import com.avatar.app.ui.sales.FarmerSalesScreen
import com.avatar.app.ui.sales.FarmerSalesViewModel
import com.avatar.app.ui.theme.*

data class KasAppDrawerItem(
    val index: Int,
    val title: String,
    val icon: ImageVector
)

fun getKasAppDrawerItems(role: String): List<KasAppDrawerItem> {
    return when (role) {
        "admin" -> listOf(
            KasAppDrawerItem(0, "Kullanıcı Yönetimi", Icons.Default.ManageAccounts),
            KasAppDrawerItem(1, "Profil & Ayarlar", Icons.Default.Person),
        )
        "hayvanci" -> listOf(
            KasAppDrawerItem(0, "Parçalama & Kesim", Icons.Default.ContentCut),
            KasAppDrawerItem(1, "Hammadde Stoğu", Icons.Default.Inventory2),
            KasAppDrawerItem(2, "Fason & Mamul İlanlar", Icons.Default.Storefront),
            KasAppDrawerItem(3, "Siparişler & Satışlar", Icons.Default.LocalShipping),
            KasAppDrawerItem(4, "Lotlar ve Recall", Icons.Default.QrCode),
            KasAppDrawerItem(5, "Profil & Faturalar", Icons.Default.Person),
        )
        "sucukcu" -> listOf(
            KasAppDrawerItem(0, "Fason İlanlarım", Icons.Default.Campaign),
            KasAppDrawerItem(1, "Gelen Siparişler", Icons.Default.Assignment),
            KasAppDrawerItem(2, "Üretim & EKP", Icons.Default.PrecisionManufacturing),
            KasAppDrawerItem(3, "Profil & Faturalar", Icons.Default.Person),
        )
        else -> listOf(
            KasAppDrawerItem(0, "Pazar İlanları", Icons.Default.Storefront),
            KasAppDrawerItem(1, "Satın Almalarım", Icons.Default.ShoppingBag),
            KasAppDrawerItem(2, "Profil & Faturalar", Icons.Default.Person),
        )
    }
}

fun getKasAppTabTitle(role: String, tab: Int): String {
    val items = getKasAppDrawerItems(role)
    return items.find { it.index == tab }?.title ?: "KasApp"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
 user: User,
 authViewModel: AuthViewModel
) {
 var selectedTab by remember { mutableIntStateOf(0) }
 var showNotificationsDialog by remember { mutableStateOf(false) }

 val butcheryViewModel: ButcheryViewModel = viewModel()
 val rawMaterialsViewModel: RawMaterialsViewModel = viewModel()
 val listingsViewModel: ProducerListingsViewModel = viewModel()
 val farmerListingsViewModel: FarmerListingsViewModel = viewModel()
 val ordersViewModel: OrdersViewModel = viewModel()
 val farmerSalesViewModel: FarmerSalesViewModel = viewModel()
 val productionViewModel: ProductionViewModel = viewModel()
 val lotsViewModel: LotsViewModel = viewModel()
 val recallsViewModel: RecallsViewModel = viewModel()
 val marketViewModel: MarketViewModel = viewModel()
 val invoicesViewModel: InvoicesViewModel = viewModel()
 val adminViewModel: AdminViewModel = viewModel()
 val notificationsViewModel: NotificationsViewModel = viewModel()

 val notifState by notificationsViewModel.uiState.collectAsState()
 val role = user.role.lowercase()

 if (showNotificationsDialog) {
 NotificationsDialog(
 viewModel = notificationsViewModel,
 onDismiss = { showNotificationsDialog = false }
 )
 }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showServerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val syncManager = remember { SyncManager.getInstance(context) }
    val syncState by syncManager.syncState.collectAsState()


    if (showServerDialog) {
        ServerConfigDialog(
            currentUrl = authViewModel.uiState.collectAsState().value.serverUrl,
            onDismiss = { showServerDialog = false },
            onSave = { newUrl ->
                authViewModel.updateServerUrl(newUrl)
                showServerDialog = false
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White,
                modifier = Modifier.width(310.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Üst Başlık & Kullanıcı Özeti
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(KasAppDarkOlive, KasAppOlive)
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.logo_last),
                                    contentDescription = "KasApp",
                                    modifier = Modifier.size(42.dp)
                                )
                                Text(
                                    text = "KasApp",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            Text(user.email, fontSize = 12.sp, color = Color.White.copy(alpha = 0.75f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, KasAppCyan)
                            ) {
                                Text(
                                    text = when (role) {
                                        "hayvanci" -> "İşletme"
                                        "sucukcu" -> "Üretici"
                                        "market" -> "Market"
                                        else -> "Sistem Yöneticisi"
                                    },
                                    color = KasAppCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Menü Kalemleri
                    val items = getKasAppDrawerItems(role)
                    items.forEach { item ->
                        val selected = selectedTab == item.index
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    item.icon,
                                    contentDescription = item.title,
                                    tint = if (selected) KasAppOlive else Color(0xFF555555)
                                )
                            },
                            label = {
                                Text(
                                    item.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            },
                            selected = selected,
                            onClick = {
                                selectedTab = item.index
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = KasAppOlive.copy(alpha = 0.12f),
                                selectedTextColor = KasAppOlive,
                                unselectedTextColor = Color(0xFF333333)
                            ),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 3.dp),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE), modifier = Modifier.padding(vertical = 8.dp))

                    // Sunucu Ayarları
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray) },
                        label = { Text("Sunucu Ayarları", fontSize = 13.sp, color = Color(0xFF555555)) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            showServerDialog = true
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Çıkış Yap
                    NavigationDrawerItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = ErrorRed) },
                        label = { Text("Çıkış Yap", fontSize = 13.sp, color = ErrorRed, fontWeight = FontWeight.SemiBold) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            authViewModel.logout()
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(R.drawable.logo_last),
                                contentDescription = "KasApp Logo",
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("KasApp", fontWeight = FontWeight.ExtraBold, color = Color.White, fontSize = 18.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menü", tint = Color.White)
                        }
                    },
                    actions = {
                        // Çevrimiçi / Çevrimdışı Durum Göstergesi
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (syncState.isOnline) Color(0xFF14532D).copy(alpha = 0.45f) else Color(0xFF7F1D1D).copy(alpha = 0.45f),
                            border = BorderStroke(
                                1.dp,
                                if (syncState.isOnline) Color(0xFF4ADE80).copy(alpha = 0.6f) else Color(0xFFEF4444).copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(
                                            color = if (syncState.isOnline) Color(0xFF4ADE80) else Color(0xFFEF4444),
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = if (syncState.isOnline) "Çevrimiçi" else "Çevrimdışı",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            IconButton(onClick = { showNotificationsDialog = true }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", tint = Color.White)
                            }

                            if (notifState.unreadCount > 0) {
                                Surface(
                                    shape = CircleShape,
                                    color = ErrorRed,
                                    shadowElevation = 2.dp,
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(x = 4.dp, y = 4.dp)
                                        .clickable { showNotificationsDialog = true }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                                            .padding(horizontal = 4.dp, vertical = 1.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = notifState.unreadCount.toString(),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            lineHeight = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = KasAppDarkOlive)
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                // Silik Çizim Deseni (Filigran Arka Plan)
                Image(
                    painter = painterResource(R.drawable.pattern_bg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alpha = 0.45f,
                    modifier = Modifier.fillMaxSize()
                )
 if (role == "admin") {
 when (selectedTab) {
 0 -> AdminUsersScreen(viewModel = adminViewModel, onOpenNotifications = { showNotificationsDialog = true })
 1 -> ProfileScreen(
 user = user,
 authViewModel = authViewModel,
 invoicesViewModel = invoicesViewModel,
 notificationsViewModel = notificationsViewModel,
 onOpenNotifications = { showNotificationsDialog = true }
 )
 }
 } else if (role == "hayvanci") {
 when (selectedTab) {
 0 -> ButcheryScreen(viewModel = butcheryViewModel)
 1 -> RawMaterialsScreen(viewModel = rawMaterialsViewModel)
 2 -> HayvanciListingsContainer(producerViewModel = listingsViewModel, farmerViewModel = farmerListingsViewModel)
 3 -> HayvanciOrdersAndSalesContainer(user = user, ordersViewModel = ordersViewModel, salesViewModel = farmerSalesViewModel)
 4 -> HayvanciLotsAndRecallsContainer(lotsViewModel = lotsViewModel, recallsViewModel = recallsViewModel)
 5 -> ProfileScreen(
 user = user,
 authViewModel = authViewModel,
 invoicesViewModel = invoicesViewModel,
 notificationsViewModel = notificationsViewModel,
 onOpenNotifications = { showNotificationsDialog = true }
 )
 }
 } else if (role == "sucukcu") {
 when (selectedTab) {
 0 -> ProducerListingsManageScreen(viewModel = listingsViewModel)
 1 -> OrdersScreen(user = user, viewModel = ordersViewModel, onNavigateToProduction = { selectedTab = 2 })
 2 -> ProductionScreen(viewModel = productionViewModel)
 3 -> ProfileScreen(
 user = user,
 authViewModel = authViewModel,
 invoicesViewModel = invoicesViewModel,
 notificationsViewModel = notificationsViewModel,
 onOpenNotifications = { showNotificationsDialog = true }
 )
 }
 } else {
 when (selectedTab) {
 0 -> MarketListingsScreen(viewModel = marketViewModel)
 1 -> MarketPurchasesScreen(viewModel = marketViewModel)
 2 -> ProfileScreen(
 user = user,
 authViewModel = authViewModel,
 invoicesViewModel = invoicesViewModel,
 notificationsViewModel = notificationsViewModel,
 onOpenNotifications = { showNotificationsDialog = true }
 )
 }
 }
 }
 }
}
}

@Composable
fun HayvanciListingsContainer(
 producerViewModel: ProducerListingsViewModel,
 farmerViewModel: FarmerListingsViewModel
) {
 var subTab by remember { mutableIntStateOf(0) }
 Column(modifier = Modifier.fillMaxSize()) {
 TabRow(
 selectedTabIndex = subTab,
 containerColor = Color.White,
 contentColor = PrimaryBlue
 ) {
 Tab(
 selected = subTab == 0,
 onClick = { subTab = 0 },
 text = { Text("Fason İlanlar", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
 )
 Tab(
 selected = subTab == 1,
 onClick = { subTab = 1 },
 text = { Text("Mamul İlanlarım", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
 )
 }
 Box(modifier = Modifier.weight(1f)) {
 if (subTab == 0) {
 ProducerListingsScreen(viewModel = producerViewModel)
 } else {
 FarmerListingsScreen(viewModel = farmerViewModel)
 }
 }
 }
}

@Composable
fun HayvanciOrdersAndSalesContainer(
 user: User,
 ordersViewModel: OrdersViewModel,
 salesViewModel: FarmerSalesViewModel
) {
 var subTab by remember { mutableIntStateOf(0) }
 Column(modifier = Modifier.fillMaxSize()) {
 TabRow(
 selectedTabIndex = subTab,
 containerColor = Color.White,
 contentColor = ForestGreen
 ) {
 Tab(
 selected = subTab == 0,
 onClick = { subTab = 0 },
 text = { Text("Fason Siparişler", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
 )
 Tab(
 selected = subTab == 1,
 onClick = { subTab = 1 },
 text = { Text("Market Satışlarım", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
 )
 }
 Box(modifier = Modifier.weight(1f)) {
 if (subTab == 0) {
 OrdersScreen(user = user, viewModel = ordersViewModel)
 } else {
 FarmerSalesScreen(viewModel = salesViewModel)
 }
 }
 }
}

@Composable
fun HayvanciLotsAndRecallsContainer(
 lotsViewModel: LotsViewModel,
 recallsViewModel: RecallsViewModel
) {
 var subTab by remember { mutableIntStateOf(0) }
 Column(modifier = Modifier.fillMaxSize()) {
 TabRow(
 selectedTabIndex = subTab,
 containerColor = Color.White,
 contentColor = Color(0xFFDC2626)
 ) {
 Tab(
 selected = subTab == 0,
 onClick = { subTab = 0 },
 text = { Text("Depo Lotları", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
 )
 Tab(
 selected = subTab == 1,
 onClick = { subTab = 1 },
 text = { Text("Geri Çağırma (Recall)", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
 )
 }
 Box(modifier = Modifier.weight(1f)) {
 if (subTab == 0) {
 LotsScreen(viewModel = lotsViewModel)
 } else {
 RecallsScreen(viewModel = recallsViewModel)
 }
 }
 }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
 user: User,
 authViewModel: AuthViewModel,
 invoicesViewModel: InvoicesViewModel,
 notificationsViewModel: NotificationsViewModel? = null,
 onOpenNotifications: () -> Unit = {}
) {
 val authState by authViewModel.uiState.collectAsState()
 val notifState = notificationsViewModel?.uiState?.collectAsState()?.value
 var showServerDialog by remember { mutableStateOf(false) }
 var showInvoicesScreen by remember { mutableStateOf(false) }

 if (showInvoicesScreen) {
 InvoicesScreen(
 user = user,
 viewModel = invoicesViewModel,
 onBack = { showInvoicesScreen = false }
 )
 return
 }

 if (showServerDialog) {
 ServerConfigDialog(
 currentUrl = authState.serverUrl,
 onDismiss = { showServerDialog = false },
 onSave = { newUrl ->
 authViewModel.updateServerUrl(newUrl)
 showServerDialog = false
 }
 )
 }

 Scaffold(
 topBar = {
 TopAppBar(
 title = { Text("Profil & Sistem Durumu", fontWeight = FontWeight.Bold, color = Color.White) },
 actions = {
 IconButton(onClick = onOpenNotifications) {
 BadgedBox(
 badge = {
 if ((notifState?.unreadCount ?: 0) > 0) {
 Badge { Text(notifState!!.unreadCount.toString()) }
 }
 }
 ) {
 Icon(Icons.Default.Notifications, contentDescription = "Bildirimler", tint = Color.White)
 }
 }
 },
 colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
 )
 }
 ) { padding ->
 Column(
 modifier = Modifier
 .fillMaxSize()
 .padding(padding)
 .verticalScroll(rememberScrollState())
 .padding(16.dp),
 verticalArrangement = Arrangement.spacedBy(16.dp)
 ) {
 // Kullanıcı Bilgisi Kartı
 Card(
 colors = CardDefaults.cardColors(containerColor = Color.White),
 shape = RoundedCornerShape(12.dp),
 elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
 modifier = Modifier.fillMaxWidth()
 ) {
 Column(modifier = Modifier.padding(16.dp)) {
 Row(
 verticalAlignment = Alignment.CenterVertically,
 horizontalArrangement = Arrangement.spacedBy(12.dp)
 ) {
 Surface(
 shape = RoundedCornerShape(50),
 color = when (user.role.lowercase()) {
 "hayvanci" -> ForestGreen.copy(alpha = 0.2f)
 "sucukcu" -> PrimaryBlue.copy(alpha = 0.2f)
 "market" -> OrangeMarket.copy(alpha = 0.2f)
 else -> Color(0xFF6B21A8).copy(alpha = 0.2f)
 },
 modifier = Modifier.size(52.dp)
 ) {
 Box(contentAlignment = Alignment.Center) {
 Text(
 when (user.role.lowercase()) {
 "hayvanci" -> ""
 "sucukcu" -> ""
 "market" -> ""
 else -> "️"
 },
 fontSize = 24.sp
 )
 }
 }

 Column {
 Text(user.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
 Text(user.email, fontSize = 13.sp, color = Color.Gray)
 Surface(
 shape = RoundedCornerShape(4.dp),
 color = when (user.role.lowercase()) {
 "hayvanci" -> ForestGreen
 "sucukcu" -> PrimaryBlue
 "market" -> OrangeMarket
 else -> Color(0xFF6B21A8)
 },
 modifier = Modifier.padding(top = 4.dp)
 ) {
 Text(
 when (user.role.lowercase()) {
 "hayvanci" -> "İşletme"
 "sucukcu" -> "Üretici"
 "market" -> "Market / Tüketici"
 else -> "Sistem Yöneticisi (Admin)"
 },
 color = Color.White,
 fontSize = 11.sp,
 fontWeight = FontWeight.Bold,
 modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
 )
 }
 }
 }
 }
 }

 // Bildirimler & Mesajlar Kartı
 Card(
 colors = CardDefaults.cardColors(containerColor = Color.White),
 shape = RoundedCornerShape(12.dp),
 elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
 modifier = Modifier
 .fillMaxWidth()
 .clickable { onOpenNotifications() }
 ) {
 Row(
 modifier = Modifier
 .fillMaxWidth()
 .padding(16.dp),
 horizontalArrangement = Arrangement.SpaceBetween,
 verticalAlignment = Alignment.CenterVertically
 ) {
 Row(verticalAlignment = Alignment.CenterVertically) {
 Surface(
 shape = RoundedCornerShape(10.dp),
 color = Color(0xFFFEF3C7),
 modifier = Modifier.size(44.dp)
 ) {
 Box(contentAlignment = Alignment.Center) {
 Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFFD97706))
 }
 }
 Spacer(modifier = Modifier.width(12.dp))
 Column {
 Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
 Text("Bildirimler & Yönetici Mesajları", fontWeight = FontWeight.Bold, fontSize = 14.sp)
 if ((notifState?.unreadCount ?: 0) > 0) {
 Surface(
 shape = RoundedCornerShape(10.dp),
 color = Color(0xFFDC2626)
 ) {
 Text(
 "${notifState!!.unreadCount} yeni",
 color = Color.White,
 fontSize = 10.sp,
 fontWeight = FontWeight.Bold,
 modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
 )
 }
 }
 }
 Text("Yönetici mesajları, geri çağırmalar ve sistem duyuruları", fontSize = 11.sp, color = Color.Gray)
 }
 }
 Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
 }
 }

 // E-Faturalar & E-Arşiv Belgesi Kartı
 Card(
 colors = CardDefaults.cardColors(containerColor = Color.White),
 shape = RoundedCornerShape(12.dp),
 elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
 modifier = Modifier
 .fillMaxWidth()
 .clickable { showInvoicesScreen = true }
 ) {
 Row(
 modifier = Modifier
 .fillMaxWidth()
 .padding(16.dp),
 horizontalArrangement = Arrangement.SpaceBetween,
 verticalAlignment = Alignment.CenterVertically
 ) {
 Row(verticalAlignment = Alignment.CenterVertically) {
 Surface(
 shape = RoundedCornerShape(10.dp),
 color = Color(0xFFEFF6FF),
 modifier = Modifier.size(44.dp)
 ) {
 Box(contentAlignment = Alignment.Center) {
 Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = PrimaryBlue)
 }
 }
 Spacer(modifier = Modifier.width(12.dp))
 Column {
 Text("E-Faturalar & E-Arşiv", fontWeight = FontWeight.Bold, fontSize = 14.sp)
 Text("Fason ve mamul satış faturalarını inceleyin / indirin", fontSize = 11.sp, color = Color.Gray)
 }
 }
 Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
 }
 }

 // Sunucu URL Ayarı Kartı
 Card(
 colors = CardDefaults.cardColors(containerColor = Color.White),
 shape = RoundedCornerShape(12.dp),
 elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
 modifier = Modifier.fillMaxWidth()
 ) {
 Column(modifier = Modifier.padding(16.dp)) {
 Row(
 modifier = Modifier.fillMaxWidth(),
 horizontalArrangement = Arrangement.SpaceBetween,
 verticalAlignment = Alignment.CenterVertically
 ) {
 Column {
 Text("Sunucu Bağlantısı", fontWeight = FontWeight.Bold, fontSize = 14.sp)
 Text(
 authState.serverUrl,
 fontSize = 12.sp,
 color = PrimaryBlue,
 fontWeight = FontWeight.Medium
 )
 }
 IconButton(onClick = { showServerDialog = true }) {
 Icon(Icons.Default.Settings, contentDescription = "Ayar", tint = PrimaryBlue)
 }
 }
 }
 }

 // Çıkış Yap Butonu
 OutlinedButton(
 onClick = { authViewModel.logout() },
 modifier = Modifier
 .fillMaxWidth()
 .height(48.dp),
 colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
 shape = RoundedCornerShape(8.dp)
 ) {
 Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
 Spacer(modifier = Modifier.width(8.dp))
 Text("Oturumu Kapat", fontWeight = FontWeight.Bold)
 }
 }
 }
}
