package com.corenfc.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.corenfc.utils.Utils

class DataQrCode {
    //060097006655|261524061|Võ Minh Thông|10091997|Nam|Tổ 2, Khu Phố 7, Phú Thủy, Phan Thiết, Bình Thuận|14022022
    var id: String

    @SerializedName("old_id")
    var oldId: String

    @SerializedName("full_name")
    var fullName: String

    @SerializedName("date_of_birth")
    var dateOfBirth: String
    var gender: String
    var address: String

    @SerializedName("date_of_issue")
    var dateOfIssue: String

    @SerializedName("date_of_expired")
    var dateOfExpired: String

    constructor(
        id: String,
        oldId: String,
        fullName: String,
        dateOfBirth: String,
        gender: String,
        address: String,
        dateOfIssue: String,
        dateOfExpired: String,
    ) {
        this.id = id
        this.oldId = oldId
        this.fullName = fullName
        this.dateOfBirth = dateOfBirth
        this.gender = gender
        this.address = address
        this.dateOfIssue = dateOfIssue
        this.dateOfExpired = dateOfExpired
    }

    companion object {

        fun parseFromQrCode(rawData: String): DataQrCode? {
            val list = rawData.split('|')
            if (rawData.isEmpty() || list.isEmpty()) {
                return null
            }
            val id = list[0]
            val oldId = list[1]
            val fullName = list[2]
            val dateOfBirth = list[3]
            val gender = list[4]
            val address = list[5]
            val dateOfIssue = list[6]
            val dateOfExpired = list[3]
            return DataQrCode(
                id,
                oldId,
                fullName,
                dateOfBirth,
                gender,
                address,
                dateOfIssue,
                dateOfExpired
            );
        }

        fun fromJson(json: String?): DataQrCode? {
            if (json == null) {
                return null
            }
            return try {
                Gson().fromJson(json, DataQrCode::class.java)
            } catch (e: Exception) {
                null;
            }
        }
    }

    fun toJson(): String {
        return GsonBuilder().serializeNulls().create().toJson(this)
    }

    override fun toString(): String {
        return "[${this.javaClass}] \n $id \n $oldId \n $fullName \n $dateOfBirth \n $gender \n $address \n $dateOfIssue \n ${getIdForNFC()} \n ${getDOBForNFC()} \n ${getDOEForNFC()} \n"
    }


    fun getIdForNFC(): String {
        return id.takeLast(9)
    }

    fun getDOBForNFC(): String {
        return Utils.convertDate(dateOfBirth)
    }

    fun getDOEForNFC(): String {
        return Utils.calculateExpiredDate(this.dateOfBirth);
    }

    fun getDOBToView(): String {
        return Utils.convertDateToView(this.dateOfBirth , "ddMMyyyy")
    }

    fun getDOEToView(): String {
        return  Utils.convertDateToView(getDOEForNFC())
    }

    fun getIssueDateToView(): String{
        return  Utils.convertDateToView(this.dateOfIssue, "ddMMyyyy" )
    }

}