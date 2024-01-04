package com.corenfc.model

import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import org.jmrtd.lds.SODFile
import org.jmrtd.lds.icao.COMFile
import org.jmrtd.lds.icao.DG11File
import org.jmrtd.lds.icao.DG12File
import org.jmrtd.lds.icao.DG14File
import org.jmrtd.lds.icao.DG15File
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File
import org.jmrtd.lds.icao.DG3File
import org.jmrtd.lds.icao.DG4File
import org.jmrtd.lds.icao.DG5File
import org.jmrtd.lds.icao.DG6File
import org.jmrtd.lds.icao.DG7File
import org.jmrtd.lds.iso19794.FaceImageInfo
import java.io.DataInputStream


class DataVerifyObject {
    var com: String
    var sod: String
    var dg1: String
    var dg2: String
    var dg11: String? = null
    var dg13: String? = null
    var dg14: String
    var dg15: String
    @SerializedName("qr_code")
    var defaultData: DataQrCode? = null
    var nationality: String? = null

    companion object {
        private const val TAG = "[DataVerifyObject]"

        fun fromJson(json: String?): DataVerifyObject? {
            if (json == null) {
                return null
            }
            return try {
                Gson().fromJson(json, DataVerifyObject::class.java)
            } catch (e: Exception) {
                Log.w(TAG, e.toString())
                null
            }

        }

    }

    fun toJson(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }

    constructor(
        com: String,
        sod: String,
        dg1: String,
        dg2: String,
        dg13: String,
        dg14: String,
        dg15: String,
        defaultDate: DataQrCode?,
        nationality: String?
    ) {
        this.com = com
        this.sod = sod
        this.dg1 = dg1
        this.dg2 = dg2
        this.dg13 = dg13
        this.dg14 = dg14
        this.dg15 = dg15
        this.dg11 = dg11
        this.defaultData = defaultDate
        this.nationality = nationality
    }

//    fun getImage(): String? {
//        val allFaceImageInfo: MutableList<FaceImageInfo> = ArrayList()
//        dg2.faceInfos.forEach {
//            allFaceImageInfo.addAll(it.faceImageInfos)
//        }
//        if (allFaceImageInfo.isNotEmpty()) {
//
//            val faceImageInfo = allFaceImageInfo.first()
//            val imageLength = faceImageInfo.imageLength
//            val dataInputStream = DataInputStream(faceImageInfo.imageInputStream)
//            val buffer = ByteArray(imageLength)
//            dataInputStream.readFully(buffer, 0, imageLength)
//            return Base64.encodeToString(buffer, Base64.DEFAULT)
//        }
//        return null;
//    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}