package com.example.cardreader

import android.content.Intent
import android.content.ContentValues.TAG
import android.util.Log
import com.king.camera.scan.AnalyzeResult
import com.king.camera.scan.CameraScan
import com.king.camera.scan.analyze.Analyzer
import com.king.wechat.qrcode.scanning.WeChatCameraScanActivity
import com.king.wechat.qrcode.scanning.analyze.WeChatScanningAnalyzer


class ScanQRCodeWeChat : WeChatCameraScanActivity() {
    override fun createAnalyzer(): Analyzer<MutableList<String>> {
        return WeChatScanningAnalyzer(true)
    }


    override fun onScanResultCallback(result: AnalyzeResult<List<String>>) {
        if (result.result.isNotEmpty()) {
            cameraScan.setAnalyzeImage(false)
            Log.d(TAG, result.result.toString())
            val text = result.result[0]
            val intent = Intent()
            intent.putExtra(CameraScan.SCAN_RESULT, text)
            setResult(RESULT_OK, intent)
            finish()

        }
    }

    override fun getLayoutId(): Int {
        return com.king.wechat.qrcode.scanning.R.layout.wechat_camera_scan
    }

}