package com.avatar.app.ui.auth



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avatar.app.R
import com.avatar.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("hayvanci") }
    var address by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.cow_mobile),
            contentDescription = "CowMobile Arka Plan",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = { Text("Yeni Hesap Oluştur", fontWeight = FontWeight.Bold, color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateToLogin) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "İşletme / Kullanıcı Bilgileri",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = KasAppDarkOlive
                        )

                        // Ad Soyad
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Ad Soyad veya Firma Adı *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = KasAppOlive) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // E-posta
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("E-posta Adresi *") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = KasAppOlive) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Şifre
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Şifre *") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = KasAppOlive) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Adres / Çiftlik Bilgisi
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("İl / İlçe veya Adres") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = KasAppOlive) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Rol Seçimi
                        Text(
                            text = "Hesap Rolünü Seçiniz:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = KasAppDarkOlive
                        )

                        RoleCard(
                            title = "İşletme",
                            subtitle = "Parçalama yapar, hammadde stoklar, fason üretim yaptırır, satış yapar.",
                            isSelected = selectedRole == "hayvanci",
                            onClick = { selectedRole = "hayvanci" }
                        )

                        RoleCard(
                            title = "Üretici",
                            subtitle = "Fason üretim ilanları açar, işletmelerden sipariş alıp mamul üretir.",
                            isSelected = selectedRole == "sucukcu",
                            onClick = { selectedRole = "sucukcu" }
                        )

                        RoleCard(
                            title = "Market (Toptan Alıcı)",
                            subtitle = "İşletmelerden üretilen mamulleri lot bazlı satın alır.",
                            isSelected = selectedRole == "market",
                            onClick = { selectedRole = "market" }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Kayıt Ol Butonu
                        Button(
                            onClick = {
                                viewModel.register(
                                    name = name,
                                    email = email,
                                    pass = password,
                                    role = selectedRole,
                                    phone = null,
                                    address = address.ifBlank { null }
                                )
                            },
                            enabled = !uiState.isLoading && name.isNotBlank() && email.isNotBlank() && password.length >= 6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = KasAppOlive)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hesap Oluştur", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Girişe Dön
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Zaten hesabınız var mı?", color = Color(0xFF666666), fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Giriş Yap",
                                color = KasAppOlive,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { onNavigateToLogin() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) KasAppOlive.copy(alpha = 0.08f) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) KasAppOlive else Color(0xFFE2E8F0)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = KasAppOlive)
            )
        }
    }
}
