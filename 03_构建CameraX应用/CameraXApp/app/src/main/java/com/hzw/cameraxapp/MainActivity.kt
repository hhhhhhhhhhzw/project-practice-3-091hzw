package com.hzw.cameraxapp

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.hzw.cameraxapp.databinding.ActivityMainBinding
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

typealias LumaListener = (luma: Double) -> Unit
class MainActivity : AppCompatActivity() {
    // 声明一个ViewBinding类型的变量，使用lateinit关键字标记该属性将在之后初始化，而不是现在
    private lateinit var viewBinding: ActivityMainBinding
    // 声明图像捕获对象，用于拍照功能
    private var imageCapture: ImageCapture? = null
    // 声明视频捕获对象，用于录制视频功能
    private var videoCapture: VideoCapture<Recorder>? = null
    // 声明一个Recording对象，用于表示当前录制的视频
    private var recording: Recording? = null
    // 声明线程执行器，用于执行相机相关操作的后台任务，以避免阻塞UI线程。
    private lateinit var cameraExecutor: ExecutorService

    // 重写onCreate方法，当Activity被创建时会被调用
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取并使用viewBinding初始化布局
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (allPermissionsGranted()) {  // 检查是否获得了必要的权限
            startCamera()  // 如果已经获得权限则开启相机
        } else {
            // 否则执行请求获取必要的权限
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
        // 给拍照按钮设置点击事件
        viewBinding.imageCaptureButton.setOnClickListener {
            takePhoto()
        }
        // 给录制按钮设置点击事件
        viewBinding.videoCaptureButton.setOnClickListener {
            captureVideo()
        }
    }
    // 重写onRequestPermissionsResult，处理请求结果
    override fun onRequestPermissionsResult(requestCode: Int,
              permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {  // 校验请求码
            // 检查用户是否同意权限请求，同意继续执行相机相关操作
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                // 用户拒绝了权限请求，则提示用户
                Toast.makeText(this, "用户拒绝了权限请求", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // 关闭相机
        cameraExecutor.shutdown()
    }

    // 开启相机的方法
    private fun startCamera() {
        // cameraProviderFuture用于将相机的生命周期绑定到生命周期所有者，消除打开和关闭相机的任务
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        // 给cameraProviderFuture添加监听
        cameraProviderFuture.addListener({
            // cameraProvider，用于将相机的生命周期绑定到应用进程中的LifecycleOwner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            // 使用Preview对象构造器构建Preview对象
            val preview = Preview.Builder().build()
                .also {// 扩展函数，设置渲染相机预览画面
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }
            // 构建ImageCapture对象
            imageCapture = ImageCapture.Builder().build()
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, LuminosityAnalyzer { luma ->
                        Log.d(TAG, "Average luminosity: $luma")
                    })
                }
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)
            // 创建CameraSelector对象，选择DEFAULT_BACK_CAMERA，表示选择默认的后置摄像头
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            // 确保当前没有任何内容绑定到cameraProvider后再进行绑定
            try {
                // 在重新绑定之前取消绑定用例
                cameraProvider.unbindAll()
                // 绑定用例到cameraProvider
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, videoCapture, imageCapture, imageAnalyzer)
            } catch(exc: Exception) {
                Log.e(TAG, "用例绑定失败", exc)
            }
        }, ContextCompat.getMainExecutor(this))  // 第二个参数返回一个在主线程上运行的Executor
    }
    // 拍照方法
    private fun takePhoto() {
        // 获取对ImageCapture用例的引用
        val imageCapture = imageCapture ?: return
        // 使用时间戳确保显示名唯一，创建用于保存图片的MediaStore内容值
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        // 创建OutputFileOptions对象,指定输出内容
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()
        // 进行拍照
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                // 失败
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "拍照失败: ${exc.message}", exc)
                }
                // 成功，则提示拍照成功，并显示图片路径
                override fun onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "拍照成功: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }
    // 录像的方法
    private fun captureVideo() {
        // 检查是否已创建VideoCapture用例
        val videoCapture = this.videoCapture ?: return
        // 在CameraX完成请求操作之前，停用界面按钮
        viewBinding.videoCaptureButton.isEnabled = false
        // 如果有正在进行的录制操作，将其停止并释放当前的recording
        val curRecording = recording
        if (curRecording != null) {
            curRecording.stop()
            recording = null
            return
        }
        // 创建一个新的录像会话
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
            }
        }
        // 外部内容选项创建MediaStoreOutputOptions.Builder,构建MediaStoreOutputOptions实例
        val mediaStoreOutputOptions = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(contentValues)
            .build()
        // 将输出选项配置为VideoCapture的Recorder并启用录音
        recording = videoCapture.output
            .prepareRecording(this, mediaStoreOutputOptions)
            .apply {// 启用音频
                if (PermissionChecker.checkSelfPermission(this@MainActivity,
                        Manifest.permission.RECORD_AUDIO) ==
                    PermissionChecker.PERMISSION_GRANTED)
                {
                    withAudioEnabled()
                }
            }
            .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
                when(recordEvent) {
                    // 录像开始
                    is VideoRecordEvent.Start -> {
                        // 修改按钮文本和启用状态
                        viewBinding.videoCaptureButton.apply {
                            text = getString(R.string.stop_capture)
                            isEnabled = true
                        }
                    }
                    // 录像结束
                    is VideoRecordEvent.Finalize -> {
                        if (!recordEvent.hasError()) {
                            // 录像成功
                            val msg = "录像成功: " +
                                    "${recordEvent.outputResults.outputUri}"
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT)
                                .show()
                            Log.d(TAG, msg)
                        } else {
                            // 录像出错，关闭录像并打印日志
                            recording?.close()
                            recording = null
                            Log.e(TAG, "Video capture ends with error: " +
                                    "${recordEvent.error}")
                        }
                        // 恢复按钮状态
                        viewBinding.videoCaptureButton.apply {
                            text = getString(R.string.start_capture)
                            isEnabled = true
                        }
                    }
                }
            }
    }


    // 检查是否有必要权限的方法，all函数会遍历REQUIRED_PERMISSIONS集合中的每一项执行检查
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it)== PackageManager.PERMISSION_GRANTED
    }


    // 定义伴生对象，用来存储一些与该类相关的常量和配置信息
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        // 请求权限的请求码
        private const val REQUEST_CODE_PERMISSIONS = 10
        // 需要的权限列表
        private val REQUIRED_PERMISSIONS =
            mutableListOf (  // 创建可变列表存储需要的权限
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {  // 使用apply函数来初始化或修改MutableList
                // 如果设备运行在Android 9.0或更低版本，还需要WRITE_EXTERNAL_STORAGE写外部存储权限
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
    }

    // 定义一个ImageAnalysis.Analyzer的子类，用来分析图像
    private class LuminosityAnalyzer(private val listener: LumaListener) : ImageAnalysis.Analyzer {
        // 重写分析方法
        override fun analyze(image: ImageProxy) {
            // 获取图像的像素数据
            val buffer = image.planes[0].buffer
            // 将像素数据转换为字节数组
            val data = buffer.toByteArray()
            // 将字节数组转换为灰度像素值
            val pixels = data.map { it.toInt() and 0xFF }
            // 计算灰度像素值的平均值
            val luma = pixels.average()
            // 回调给调用者
            listener(luma)
            // 释放ImageProxy
            image.close()
        }

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // 将缓冲区的位置重置为0
            val data = ByteArray(remaining())
            get(data)   // 将缓冲区的数据复制到字节数组中
            return data // 返回字节数组
        }
    }


}