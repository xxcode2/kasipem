package com.kasipem.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasipem.data.*
import com.kasipem.ui.components.*

@Composable
fun HomeScreen() {
    var selectedLetterType by remember { mutableStateOf(LetterType.DOMISILI) }
    var headerData by remember { mutableStateOf(HeaderData()) }
    var letterData by remember { mutableStateOf<Any>(DomisiliData()) }
    var showSettings by remember { mutableStateOf(false) }
    var showPreview by remember { mutableStateOf(false) }

    val letterTypes = listOf(
        LetterTypeItem(
            type = LetterType.DOMISILI,
            title = "Keterangan Domisili",
            description = "Verifikasi tempat tinggal",
            icon = Icons.Default.Home,
            gradientStart = Color(0xFF0284C7),
            gradientEnd = Color(0xFF0369A1),
            classification = "140"
        ),
        LetterTypeItem(
            type = LetterType.BELUM_MENIKAH,
            title = "Belum Menikah",
            description = "Administrasi perkawinan",
            icon = Icons.Default.Favorite,
            gradientStart = Color(0xFFEC4899),
            gradientEnd = Color(0xFFBE185D),
            classification = "460.2"
        ),
        LetterTypeItem(
            type = LetterType.TIDAK_MAMPU,
            title = "Tidak Mampu (SKTM)",
            description = "Bantuan sosial",
            icon = Icons.Default.AttachMoney,
            gradientStart = Color(0xFF10B981),
            gradientEnd = Color(0xFF047857),
            classification = "460"
        ),
        LetterTypeItem(
            type = LetterType.BEDA_LUAS,
            title = "Beda Luas Tanah",
            description = "Sertifikat vs SPPT",
            icon = Icons.Default.Landscape,
            gradientStart = Color(0xFFF59E0B),
            gradientEnd = Color(0xFFD97706),
            classification = "141"
        ),
        LetterTypeItem(
            type = LetterType.UNDANGAN,
            title = "Undangan Resmi",
            description = "Acara resmi desa",
            icon = Icons.Default.Mail,
            gradientStart = Color(0xFFA855F7),
            gradientEnd = Color(0xFF7E22CE),
            classification = "Khusus"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "KASIPEM",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.headlineMedium.fontSize
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            if (showPreview) {
                FloatingActionButton(
                    onClick = { showPreview = false },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF0F9FF),
                            Color(0xFFF0FDFA),
                            Color(0xFFFCFDF5)
                        )
                    )
                )
        ) {
            if (showSettings) {
                SettingsSheet(
                    headerData = headerData,
                    onHeaderChange = { headerData = it },
                    onDismiss = { showSettings = false },
                    modifier = Modifier.fillMaxSize()
                )
            } else if (showPreview) {
                PreviewScreen(
                    headerData = headerData,
                    letterData = letterData,
                    letterType = selectedLetterType,
                    onBack = { showPreview = false },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                MainFormContent(
                    selectedLetterType = selectedLetterType,
                    letterTypes = letterTypes,
                    onLetterTypeChange = { type ->
                        selectedLetterType = type
                        letterData = getEmptyLetterData(type)
                    },
                    letterData = letterData,
                    onLetterDataChange = { letterData = it },
                    onSubmit = { showPreview = true },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun MainFormContent(
    selectedLetterType: LetterType,
    letterTypes: List<LetterTypeItem>,
    onLetterTypeChange: (LetterType) -> Unit,
    letterData: Any,
    onLetterDataChange: (Any) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Pembuat Surat Administrasi",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Desa/Kelurahan Stateless",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        // Letter Type Selection
        Text(
            "Langkah 1: Pilih Jenis Surat",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            letterTypes.forEach { item ->
                LetterTypeCard(
                    item = item,
                    isSelected = selectedLetterType == item.type,
                    onClick = { onLetterTypeChange(item.type) }
                )
            }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Form Fields
        Text(
            "Langkah 2: Isi Data Surat",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Render form based on letter type
        when (selectedLetterType) {
            LetterType.DOMISILI -> DomisiliForm(
                data = letterData as DomisiliData,
                onDataChange = { onLetterDataChange(it) }
            )

            LetterType.BELUM_MENIKAH -> BelumMenikahForm(
                data = letterData as BelumMenikahData,
                onDataChange = { onLetterDataChange(it) }
            )

            LetterType.TIDAK_MAMPU -> TidakMampuForm(
                data = letterData as TidakMampuData,
                onDataChange = { onLetterDataChange(it) }
            )

            LetterType.BEDA_LUAS -> BedaLuasForm(
                data = letterData as BedaLuasData,
                onDataChange = { onLetterDataChange(it) }
            )

            LetterType.UNDANGAN -> UndanganForm(
                data = letterData as UndanganData,
                onDataChange = { onLetterDataChange(it) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Pratinjau & Cetak",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun getEmptyLetterData(type: LetterType): Any = when (type) {
    LetterType.DOMISILI -> DomisiliData()
    LetterType.BELUM_MENIKAH -> BelumMenikahData()
    LetterType.TIDAK_MAMPU -> TidakMampuData()
    LetterType.BEDA_LUAS -> BedaLuasData()
    LetterType.UNDANGAN -> UndanganData()
}
