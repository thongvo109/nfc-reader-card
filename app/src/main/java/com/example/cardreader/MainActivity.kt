package com.example.cardreader

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.corenfc.ReaderCardActivity
import com.corenfc.model.DataQrCode
import com.king.camera.scan.CameraScan
import com.king.wechat.qrcode.WeChatQRCodeDetector
import org.opencv.OpenCV

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        OpenCV.initAsync(this)
        WeChatQRCodeDetector.init(this)
        showCamera()
    }

    private fun startActivityForResult(clazz: Class<*>) {
        startActivityForResult(Intent(this, clazz), REQUEST_CODE_QRCODE)
    }

    private fun showCamera() {
        startActivityForResult(ScanQRCodeWeChat::class.java)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_CODE_QRCODE){
            if(resultCode == RESULT_OK){
                val result = data?.getStringExtra(CameraScan.SCAN_RESULT)
                val qrCode: DataQrCode? = DataQrCode.parseFromQrCode(result.toString())
                if(result != null){
                    val intent = Intent(this, ReaderCardActivity::class.java)
                    intent.putExtra("data_from_qr_code", qrCode?.toJson())
                    startActivity(intent)
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE_QRCODE = 0x10
    }
}

