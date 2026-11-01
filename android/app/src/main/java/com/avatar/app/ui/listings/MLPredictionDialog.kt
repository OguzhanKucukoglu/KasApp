package com.avatar.app.ui.listings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.avatar.app.data.model.ProducerListing
import com.avatar.app.ui.theme.ForestGreen
import com.avatar.app.ui.theme.KasAppDarkOlive
import com.avatar.app.ui.theme.KasAppOlive
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MLPredictionDialog(
    listing: ProducerListing,
    viewModel: ProducerListingsViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val totalInputKg = state.mlMaterials.values.sum()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    Surface(
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Kapat", tint = KasAppDarkOlive)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Yapay Zeka Üretim Çıktı Tahmini",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = KasAppDarkOlive,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${listing.productName} • ${listing.producerName ?: "Üretici"}",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                },
                bottomBar = {
                    Surface(tonalElevation = 6.dp, modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = KasAppOlive),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Tamam / Kapat", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            ) { padding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Bilgilendirme başlığı
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Fason üretime göndereceğiniz et ve hammadde miktarlarını girerek yapay zeka ile tahmini çıktı ve birim maliyeti simüle edebilirsiniz.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF166534),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // 1. Girdi Miktarları
                    item {
                        Text(
                            "Girdi Miktarları (kg)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    items(listing.requiredMaterialsParsed.entries.toList()) { (matName, spec) ->
                        val currentVal = state.mlMaterials[matName] ?: 0.0
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(matName, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text("Gereken: ${spec.amount} ${spec.unit}", fontSize = 11.sp, color = Color.Gray)
                            }
                            OutlinedTextField(
                                value = if (currentVal > 0) currentVal.toString() else "",
                                onValueChange = { str ->
                                    val d = str.toDoubleOrNull() ?: 0.0
                                    viewModel.updateMlMaterial(matName, d)
                                },
                                placeholder = { Text("0.0", fontSize = 12.sp) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.width(110.dp),
                                singleLine = true
                            )
                        }
                    }

                    // Toplam & Buton
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Toplam Girdi Ağırlığı:", fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    String.format(Locale.US, "%.2f kg", totalInputKg),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = { viewModel.runMlPrediction() },
                            enabled = !state.isMlPredicting && totalInputKg > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = KasAppOlive),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.isMlPredicting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Yapay Zeka Hesaplanıyor...", fontSize = 13.sp)
                            } else {
                                Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Yapay Zeka ile Tahmin Et", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    // 2. Tahmin Sonuçları Kartı
                    state.mlResult?.let { res ->
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF5FF)),
                                border = BorderStroke(1.dp, Color(0xFFD8B4FE)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Başlık
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Tahmin ve Verim Analizi",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = KasAppDarkOlive
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // İki Büyük Metrik Kutusu
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Paket Sayısı
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    "${res.predictedPackages ?: 1} Paket",
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = ForestGreen
                                                )
                                                Text(
                                                    "Tahmini Çıktı (${res.predictedPackages ?: 1} kg)",
                                                    fontSize = 10.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }

                                        // EKP / Paket
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.White,
                                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(
                                                    String.format(Locale.US, "%.2f €", res.predictedEkp),
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFE11D48)
                                                )
                                                Text(
                                                    "Tahmini EKP / paket",
                                                    fontSize = 10.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Fire Bilgisi
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.White,
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Tahmini Kuruma/Dolum Firesi:", fontSize = 11.sp, color = Color.Gray)
                                            Text(
                                                "${String.format(Locale.US, "%.2f", res.predictedFireKg ?: 0.0)} kg (%${String.format(Locale.US, "%.1f", res.predictedFireRate ?: 0.0)})",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color(0xFFDC2626)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Model Güven Skoru & Progress Bar
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Model Güven Skoru", fontSize = 11.sp, color = Color.Gray)
                                        Text(
                                            "%${(res.confidence * 100).toInt()}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp,
                                            color = Color(0xFF6D28D9)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { res.confidence.toFloat().coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = Color(0xFF7E22CE),
                                        trackColor = Color(0xFFE9D5FF)
                                    )

                                    if (!res.notes.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            res.notes,
                                            fontSize = 11.sp,
                                            color = Color.DarkGray
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Sipariş yönlendirme bilgi notu
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF1F5F9),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                "Bu simülasyon makine öğrenmesi modeliyle tahmini çıktı ve fire oranlarını gösterir. Sipariş oluşturmak isterseniz bu pencereyi kapatıp ilandaki 'Sipariş Ver' butonunu kullanabilirsiniz.",
                                                fontSize = 11.sp,
                                                color = Color(0xFF475569),
                                                lineHeight = 15.sp
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
    }
}
