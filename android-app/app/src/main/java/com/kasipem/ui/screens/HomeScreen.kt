package com.kasipem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kasipem.data.*
import com.kasipem.ui.components.*

/**
 * Beranda: grid 33 kartu surat/formulir (cari + filter kategori).
 * Alur: klik kartu → layar form SATU surat → "Lihat Preview" → preview.
 * Tidak ada lagi semua form ditampilkan sekaligus di satu scroll panjang.
 * Tombol riwayat (ikon jam) di bilah atas membuka Riwayat Surat lokal.
 */
private enum class Screen { GRID, FORM, PREVIEW, HISTORY }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var screen by remember { mutableStateOf(Screen.GRID) }
    var selectedLetterType by remember { mutableStateOf(LetterType.DOMISILI) }
    var headerData by remember { mutableStateOf(HeaderData()) }
    var letterData by remember { mutableStateOf<Any>(getEmptyLetterData(LetterType.DOMISILI)) }
    var showSettings by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val letterTypes = remember { LetterType.values().map { it.toItem() } }
    val categories = remember { listOf("Semua") + LetterType.values().map { it.category }.distinct() }
    val filteredTypes = letterTypes.filter { item ->
        (selectedCategory == "Semua" || item.category == selectedCategory) &&
            (searchQuery.isBlank() ||
                item.title.contains(searchQuery, ignoreCase = true) ||
                item.description.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KASIPEM", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { screen = Screen.HISTORY }) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Riwayat Surat"
                        )
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Pengaturan"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (showSettings) {
                SettingsSheet(
                    headerData = headerData,
                    onHeaderChange = { headerData = it },
                    onDismiss = { showSettings = false },
                    modifier = Modifier.fillMaxSize()
                )
            } else when (screen) {
                Screen.GRID -> GridContent(
                    headerData = headerData,
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    letterTypes = filteredTypes,
                    selectedLetterType = selectedLetterType,
                    onLetterTypeClick = { type ->
                        selectedLetterType = type
                        letterData = getEmptyLetterData(type)
                        screen = Screen.FORM
                    },
                    modifier = Modifier.fillMaxSize()
                )
                Screen.FORM -> FormContent(
                    selectedLetterType = selectedLetterType,
                    letterData = letterData,
                    onLetterDataChange = { letterData = it },
                    onBack = { screen = Screen.GRID },
                    onPreview = { screen = Screen.PREVIEW },
                    modifier = Modifier.fillMaxSize()
                )
                Screen.PREVIEW -> PreviewScreen(
                    headerData = headerData,
                    letterData = letterData,
                    letterType = selectedLetterType,
                    onBack = { screen = Screen.FORM },
                    modifier = Modifier.fillMaxSize()
                )
                Screen.HISTORY -> HistoryScreen(
                    onBack = { screen = Screen.GRID },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

private fun titleCase(value: String): String =
    value.trim().split("\\s+".toRegex()).joinToString(" ") {
        it.lowercase().replaceFirstChar { c -> c.uppercase() }
    }

/** Ikon + aksen kalem per tipe (satu warna datar, bukan gradien). */
private fun iconFor(type: LetterType): ImageVector = when (type) {
    LetterType.DOMISILI -> Icons.Default.Home
    LetterType.PISAH_KK -> Icons.Default.Group
    LetterType.SKT_DAYAT -> Icons.Default.Landscape
    LetterType.BEDA_LUAS -> Icons.Default.Map
    LetterType.BEDA_NAMA -> Icons.Default.Badge
    LetterType.BELUM_MENIKAH -> Icons.Default.Favorite
    LetterType.TIDAK_MAMPU -> Icons.Default.AttachMoney
    LetterType.AHLI_WARIS -> Icons.Default.FamilyRestroom
    LetterType.NIKAH_TIDAK_TERCATAT -> Icons.Default.People
    LetterType.PEMAKAMAN -> Icons.Default.Park
    LetterType.TIDAK_MEMILIKI_RUMAH -> Icons.Default.House
    LetterType.UNDANGAN -> Icons.Default.Mail
    // ---- Fase 2: 18 tipe baru ----
    LetterType.KEMATIAN -> Icons.Default.SentimentDissatisfied
    LetterType.USAHA -> Icons.Default.Store
    LetterType.DUDA_JANDA -> Icons.Default.Person
    LetterType.F101 -> Icons.Default.Groups
    LetterType.F102 -> Icons.Default.Assignment
    LetterType.F103 -> Icons.Default.SwapHoriz
    LetterType.F104 -> Icons.Default.Description
    LetterType.F105 -> Icons.Default.FavoriteBorder
    LetterType.F106 -> Icons.Default.Edit
    LetterType.F115 -> Icons.Default.Contacts
    LetterType.F201 -> Icons.Default.ChildCare
    LetterType.F203 -> Icons.Default.ChildFriendly
    LetterType.F204 -> Icons.Default.SupervisorAccount
    LetterType.SPTJM_KEMATIAN -> Icons.Default.Article
    LetterType.SPTJM_PERKAWINAN_PERCERAIAN -> Icons.Default.GroupAdd
    LetterType.PBI_ASESMEN -> Icons.Default.Assessment
    LetterType.PBI_PERNYATAAN -> Icons.Default.RequestQuote
    LetterType.PBI_TIDAK_PUNYA_REKENING -> Icons.Default.AccountBalance
    // ---- Fase 5: 3 tipe baru ----
    LetterType.SKTM_PBI -> Icons.Default.VolunteerActivism
    LetterType.PBI_REKENING_LISTRIK -> Icons.Default.Receipt
    LetterType.PERNYATAAN_AKTA_KELAHIRAN -> Icons.Default.Description
}

/** Aksen kalem per kategori (teal tua / slate / stone). */
private fun accentFor(category: String): Color = when (category) {
    "Individu" -> Color(0xFF0F766E)
    "Properti" -> Color(0xFF475569)
    "Administrasi" -> Color(0xFF155E75)
    "Sosial" -> Color(0xFF57534E)
    "Hukum" -> Color(0xFF44403C)
    "Surat Desa" -> Color(0xFF0F766E)
    "Formulir Dukcapil" -> Color(0xFF1E40AF)
    "SPTJM" -> Color(0xFF7C2D12)
    "Bansos (PBI-JKN)" -> Color(0xFF166534)
    else -> Color(0xFF334155)
}

private fun LetterType.toItem(): LetterTypeItem = LetterTypeItem(
    type = this,
    title = displayTitle,
    description = description,
    icon = iconFor(this),
    accent = accentFor(category),
    classification = if (nomorPrefix.isEmpty()) "Manual" else "$nomorPrefix/.../$nomorSuffix",
    category = category
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GridContent(
    headerData: HeaderData,
    categories: List<String>,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    letterTypes: List<LetterTypeItem>,
    selectedLetterType: LetterType,
    onLetterTypeClick: (LetterType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(20.dp),
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
                "Desa ${titleCase(headerData.namaDesa)}, Kecamatan ${titleCase(headerData.namaKecamatan)}, Kabupaten ${titleCase(headerData.namaKabupaten.removePrefix("KABUPATEN "))}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Cari jenis surat…") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategoryChange(category) },
                    label = { Text(category) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Pilih Jenis Surat (${letterTypes.size} dari 33)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (letterTypes.isEmpty()) {
            Text(
                "Tidak ada surat yang cocok dengan pencarian.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(letterTypes) { item ->
                    LetterTypeCard(
                        item = item,
                        isSelected = selectedLetterType == item.type,
                        onClick = { onLetterTypeClick(item.type) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormContent(
    selectedLetterType: LetterType,
    letterData: Any,
    onLetterDataChange: (Any) -> Unit,
    onBack: () -> Unit,
    onPreview: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(selectedLetterType.displayTitle, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                navigationIconContentColor = MaterialTheme.colorScheme.onSurface
            )
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Isi Data Surat",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            LetterForm(
                type = selectedLetterType,
                letterData = letterData,
                onDataChange = onLetterDataChange
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onPreview,
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
                Text("Lihat Preview", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
