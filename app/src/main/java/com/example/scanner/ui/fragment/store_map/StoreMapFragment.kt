package com.example.scanner.ui.fragment.store_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.example.scanner.data.db.Shelf
import com.example.scanner.data.db.ShelfRepository
import com.example.scanner.ui.theme.ScannerTheme
import io.github.sceneview.math.Position
import io.github.sceneview.SceneView
import io.github.sceneview.math.Color
import io.github.sceneview.gesture.CameraGestureDetector
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader

class StoreMapFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                ScannerTheme {
                    // Gọi hàm Composable chứa toàn bộ logic bản đồ ở đây
                    Store3DMapScreen()
                }
            }
        }
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Store3DMapScreen() {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    var distance by remember { mutableFloatStateOf(55f) }

    val context = androidx.compose.ui.platform.LocalContext.current
    var userPosition by remember { mutableStateOf(ShelfRepository.entrance) }
    var userRotationY by remember { mutableFloatStateOf(0f) }

    // Khởi tạo SensorTracker
    val sensorTracker = remember {
        com.example.scanner.util.SensorTracker(context) { newPos, newRotY ->
            userPosition = newPos
            userRotationY = newRotY
        }
    }

    // Xin quyền và lắng nghe sensor
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) sensorTracker.startTracking()
    }

    DisposableEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            permissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            sensorTracker.startTracking()
        }
        onDispose { sensorTracker.stopTracking() }
    }

    val cameraManipulator = remember {
        CameraGestureDetector.DefaultCameraManipulator(
            orbitHomePosition = Position(x = 0f, y = 25f, z = distance),
            targetPosition = Position(0f, 5f, 0f),
            pinchZoomSpeed = 1f,
        )
    }

    // Search state
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<Shelf>>(emptyList()) }
    var selectedShelf by remember { mutableStateOf<Shelf?>(null) }
    var showSearchResults by remember { mutableStateOf(false) }

    val pathPoints = remember(selectedShelf) {
        selectedShelf?.let { ShelfRepository.getPathTo(it) } ?: emptyList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ── 3D Scene ──
        SceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            materialLoader = materialLoader,
            cameraManipulator = cameraManipulator,
        ) {
            // Model cửa hàng
            rememberModelInstance(
                modelLoader = modelLoader,
                fileLocation = "models/store_map.glb"
            )?.let { modelInstance ->
                ModelNode(
                    modelInstance = modelInstance,
                    scaleToUnits = 32f,
                    centerOrigin = Position(0f, 0f, 0f)
                )
            }

            val redMaterial = remember(materialLoader) {
                materialLoader.createColorInstance(Color(
                        x = 1f,
                        y = 0f,
                        z = 0f,
                        w = 1f
                    )
                )
            }

            // Vẽ đường đi
            if (pathPoints.size >= 2) {
                for (i in 0 until pathPoints.size - 1) {
                    LineNode(
                        start = pathPoints[i],
                        end   = pathPoints[i + 1],
                        materialInstance = redMaterial
                    )
                }

                selectedShelf?.let { shelf ->
                    SphereNode(
                        radius = 0.3f,
                        center = Position(shelf.position.x, 0.5f, shelf.position.z),
                        materialInstance = redMaterial
                    )
                }
            }

            val blueMaterial = remember(materialLoader) {
                materialLoader.createColorInstance(Color(x = 0f, y = 0.5f, z = 1f, w = 1f))
            }

            // Chấm tròn (vị trí user)
            SphereNode(
                radius = 0.3f,
                center = Position(userPosition.x, 0.5f, userPosition.z),
                materialInstance = blueMaterial
            )

            // Đuôi chỉ hướng
            val dirX = 1.5f * kotlin.math.sin(Math.toRadians(userRotationY.toDouble())).toFloat()
            val dirZ = -1.5f * kotlin.math.cos(Math.toRadians(userRotationY.toDouble())).toFloat()
            LineNode(
                start = Position(userPosition.x, 0.5f, userPosition.z),
                end = Position(userPosition.x + dirX, 0.5f, userPosition.z + dirZ),
                materialInstance = blueMaterial
            )
        }

        // ── Search Bar ──
        Column(
            modifier = Modifier.align(Alignment.TopStart).fillMaxWidth().padding(16.dp).padding(top = 24.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    searchQuery = query
                    searchResults = ShelfRepository.search(query)
                    showSearchResults = query.isNotBlank()
                },
                onSearch = { showSearchResults = false },
                active = showSearchResults,
                onActiveChange = { showSearchResults = it },
                placeholder = { Text("Tìm kệ hàng (VD: K1, K5...)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            selectedShelf = null
                            showSearchResults = false
                        }) {
                            Icon(Icons.Default.Close, contentDescription = "Xóa")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                LazyColumn {
                    items(searchResults) { shelf ->
                        ListItem(
                            headlineContent = { Text("Kệ ${shelf.name}") },
                            modifier = Modifier.clickable {
                                selectedShelf = shelf
                                searchQuery = shelf.name
                                showSearchResults = false
                            }
                        )
                    }
                    if (searchResults.isEmpty() && searchQuery.isNotBlank()) {
                        item {
                            ListItem(headlineContent = { Text("Không tìm thấy kệ \"$searchQuery\"") })
                        }
                    }
                }
            }
        }

        // ── Zoom buttons ──
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    cameraManipulator.scrollBegin(0, 0, 100f)
                    cameraManipulator.scrollUpdate(0, 0, 100f, 800f)
                    cameraManipulator.scrollEnd()
                }
            ) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
            }
            FloatingActionButton(
                onClick = {
                    cameraManipulator.scrollBegin(0, 0, 800f)
                    cameraManipulator.scrollUpdate(0, 0, 800f, 100f)
                    cameraManipulator.scrollEnd()
                }
            ) {
                Text("–", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }
}