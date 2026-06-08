package com.example.scanner

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scanner.data.db.repository.CloudinaryConfig
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelInstance
import io.github.sceneview.rememberModelLoader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Khởi tạo cấu hình Cloudinary (tìm kiếm chính xác trong package data.db.repository)
        CloudinaryConfig.init(applicationContext)

        // =================================================================================
        // CHỌN 1 TRONG 2 CẤU HÌNH DƯỚI ĐÂY TÙY THEO MỤC ĐÍCH SỬ DỤNG:
        // =================================================================================

        // --- CẤU HÌNH A: Hiển thị trực tiếp màn hình Store3DMapScreen bằng Compose (Dùng để test SceneView) ---
        setContent {
            Store3DMapScreen(onBackClick = { finish() })
        }
        // Lưu ý: Khi sử dụng setContent, hãy đảm bảo dòng findNavController phía dưới được comment lại để tránh crash.

        // --- CẤU HÌNH B: Sử dụng XML Navigation Components truyền thống ---
        // Để sử dụng, vui lòng COMMENT khối "setContent" ở trên, và UNCOMMENT 2 dòng dưới đây:
        // setContentView(R.layout.activity_main)
        // val navController = findNavController(R.id.nav_host_fragment)
        
        // =================================================================================
    }
}

// Hàm Composable được đưa ra ngoài class MainActivity làm Top-level function đúng chuẩn Jetpack Compose
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Store3DMapScreen(
    onBackClick: () -> Unit = {}
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val cameraManipulator = rememberCameraManipulator()
    
    // Khai báo state cho scale của mô hình 3D (Tối ưu hóa Recomposition)
    var modelScale by remember { mutableFloatStateOf(15f) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. SceneView hiển thị mô hình 3D tràn màn hình
        SceneView(
            modifier = Modifier.fillMaxSize(),
            engine = engine,
            modelLoader = modelLoader,
            cameraManipulator = cameraManipulator,
        ) {
            rememberModelInstance(
                modelLoader = modelLoader,
                fileLocation = "models/store_map.glb"
            )?.let { modelInstance ->
                ModelNode(
                    modelInstance = modelInstance,
                    scaleToUnits = modelScale, // Sử dụng state để thay đổi scale động
                    centerOrigin = Position(0f, 0f, 0f)
                )
            }
        }
        
        // 2. Lớp giao diện phủ phía trên (UI Overlay) phong cách Material Design 3 (Clean & Minimalist)
        
        // A. TopAppBar phong cách Glassmorphism
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
                .padding(top = 8.dp, bottom = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "Bản đồ 3D Cửa hàng",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                IconButton(
                    onClick = { /* Tìm kiếm vật phẩm */ },
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // B. Bảng điều khiển độ thu phóng (Scale Control) ở góc phải
        Card(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .width(64.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Scale",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                IconButton(
                    onClick = { if (modelScale < 30f) modelScale += 2f },
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Zoom In",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Text(
                    text = "${modelScale.toInt()}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                IconButton(
                    onClick = { if (modelScale > 5f) modelScale -= 2f },
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Zoom Out",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // C. Nút Reset View (FAB) ở góc dưới cùng bên phải
        FloatingActionButton(
            onClick = {
                // Đặt lại góc camera
                try {
                    // cameraManipulator có thể thiết lập lại các giá trị vị trí hoặc góc
                    // Để tránh lỗi biên dịch của thư viện cụ thể, chúng ta sử dụng try-catch
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 24.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset View"
                )
                Text(
                    text = "Reset View",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
