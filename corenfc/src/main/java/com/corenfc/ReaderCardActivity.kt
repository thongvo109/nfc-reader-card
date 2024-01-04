package com.corenfc

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.corenfc.model.DataQrCode


class ReaderCardActivity : AppCompatActivity() {
    private lateinit var nfcAdapter: NfcAdapter
    private var dataQrCode: DataQrCode? = null
    private lateinit var viewModel: ReaderCardViewModel
    private lateinit var resultJsonTxt: TextView
    private lateinit var btnCallNfc: Button

    private fun onUpdateUI() {
        viewModel.getUiState().observe(this) {
            when (it) {
                is UiState.Error -> {
                    resultJsonTxt.text = it.message
                }

                is UiState.Scanning -> {
                    resultJsonTxt.text = it.message
                }

                UiState.Loading -> {
                    resultJsonTxt.text = "Vui lòng áp thẻ vào vị trí"
                }

                is UiState.Success -> {
                    val resultIntent = Intent(this, CustomerDetailFragment::class.java)
                    resultIntent.putExtra("result", it.data.toJson())
                    startActivity(resultIntent)
                }

                else -> {}
            }
        }
        btnCallNfc.text = "Set Wrong MRZ ${viewModel.getWrongMRD()}"
        btnCallNfc.setOnClickListener {
            onUpdateWrongResult()
        }
    }

    private fun onInitViewModel(qrCode: DataQrCode) {
        viewModel =
            ViewModelProvider(this, ViewModelFactory(qrCode))[ReaderCardViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader_card)
        resultJsonTxt = findViewById(R.id.result_json_txt)
        btnCallNfc = findViewById(R.id.btn_call_nfc)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (intent != null) {
            dataQrCode = DataQrCode.fromJson(intent.getStringExtra("data_from_qr_code"))
            println("Dang tim cai nay ${dataQrCode?.toString()}")
            onInitViewModel(dataQrCode!!)
        }
        onUpdateUI()
        onCallNFC()


    }


    private fun onUpdateWrongResult() {
        viewModel.onUpdateEnableWrong()
        btnCallNfc.text = "Set Wrong MRZ ${viewModel.getWrongMRD()}"
    }


    private fun onCallNFC() {

        nfcAdapter.enableReaderMode(this, { tag ->
            if (tag?.techList?.contains("android.nfc.tech.IsoDep") == true) {
                if (dataQrCode != null) {
//                    viewModel.ReadTask(IsoDep.get(tag)).execute()
                    viewModel.onReadCard(IsoDep.get(tag))
                }

            }

        }, NfcAdapter.FLAG_READER_NFC_A, null)


    }


    override fun onResume() {
        super.onResume()
        print("onResume running")
        val adapter = NfcAdapter.getDefaultAdapter(this)
        if (adapter != null) {
            val intent = Intent(applicationContext, this.javaClass)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
            val filter = arrayOf(arrayOf("android.nfc.tech.IsoDep"))
            adapter.enableForegroundDispatch(this, pendingIntent, null, filter)
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        print("onResume running new intennt")
        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent?.action) {
            val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            if (tag?.techList?.contains("android.nfc.tech.IsoDep") == true) {
            }
        }

    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableReaderMode(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
