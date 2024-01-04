package com.corenfc

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.corenfc.model.DataVerifyObject

class CustomerDetailFragment : AppCompatActivity() {

//    private lateinit var binding: FragmentCustomerDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        binding = FragmentCustomerDetailBinding.inflate(layoutInflater)
        setContentView(R.layout.fragment_customer_detail)

//        if (intent != null){
//            val rawData = intent.getStringExtra("result")
//            println(rawData)
//            val dataVerifyObject: DataVerifyObject? = DataVerifyObject.fromJson(rawData)
//            bindingNFCData(dataVerifyObject!!)
//
//        }
    }

    private fun bindingNFCData(data: DataVerifyObject) {
//            binding.textCccdValue.text = data.defaultData?.id
//            binding.textNameValue.text = data.defaultData?.fullName
//            binding.textGenderValue.text = data.defaultData?.gender
//            binding.textDobValue.text = data.defaultData?.getDOBToView()
//            binding.textOriginalPlaceValue.text = "---"
//            binding.textNationalityValue.text = if (data.nationality == "VNM") {"Việt Nam"} else {"Nước ngoài"}
//            binding.textIssueDateValue.text = data.defaultData?.getIssueDateToView()
//            binding.textExpireDateValue.text = data.defaultData?.getDOEToView()
//            binding.textOriginalAddressValue.text = data.defaultData?.address
//            binding.textIssuePlaceValue.text = getString(R.string.issue_place_hardcode)
        }

}