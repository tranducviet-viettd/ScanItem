package com.example.scanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.findNavController
import com.example.scanner.data.db.repository.CloudinaryConfig
import com.example.scanner.ui.theme.ScannerTheme
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
            Store3DMapScreen()
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
@Composable
fun Store3DMapScreen() {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val cameraManipulator = rememberCameraManipulator()
    
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
                scaleToUnits = 15f,           // Chỉnh kích thước model phù hợp
                centerOrigin = Position(0f, 0f, 0f)
            )
        }
    }
}
