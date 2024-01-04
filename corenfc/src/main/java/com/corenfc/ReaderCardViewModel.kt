package com.corenfc

import android.content.ContentValues.TAG
import android.nfc.tech.IsoDep
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.corenfc.model.DataQrCode
import com.corenfc.model.DataVerifyObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sf.scuba.smartcards.CardService
import net.sf.scuba.smartcards.CardServiceException
import org.jmrtd.AccessDeniedException
import org.jmrtd.BACKey
import org.jmrtd.BACKeySpec
import org.jmrtd.PassportService
import org.jmrtd.lds.CardAccessFile
import org.jmrtd.lds.PACEInfo
import org.jmrtd.lds.SODFile
import org.jmrtd.lds.SecurityInfo
import org.jmrtd.lds.icao.COMFile
import org.jmrtd.lds.icao.DG14File
import org.jmrtd.lds.icao.DG15File
import org.jmrtd.lds.icao.DG1File
import org.jmrtd.lds.icao.DG2File

class ReaderCardViewModel(dataQrCode: DataQrCode) : ViewModel() {
    private val uiState = MutableLiveData<UiState<DataVerifyObject>>()
    private var isEnableWrong = false
    private var valueDoeForNFc = ""
    private var currentDataQrCode = dataQrCode

    private var bacKey: BACKeySpec = BACKey(
        currentDataQrCode.getIdForNFC(),
        currentDataQrCode.getDOBForNFC(), currentDataQrCode.getDOEForNFC()
    )


    init {
        valueDoeForNFc = currentDataQrCode.getDOEForNFC()
        uiState.postValue(UiState.Loading)
    }

    fun onUpdateEnableWrong() {
        isEnableWrong = !isEnableWrong
        bacKey = if (isEnableWrong) {
            BACKey(
                currentDataQrCode.getIdForNFC(),
                currentDataQrCode.getDOBForNFC(), "999999"
            )

        } else {
            BACKey(
                currentDataQrCode.getIdForNFC(),
                currentDataQrCode.getDOBForNFC(), currentDataQrCode.getDOEForNFC()
            )
        }
    }


    fun onReadCard(isoDep: IsoDep) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                isoDep.timeout = 10000
                val cardService = CardService.getInstance(isoDep)
                cardService.open()

                val service = PassportService(
                    cardService,
                    PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                    PassportService.DEFAULT_MAX_BLOCKSIZE,
                    false,
                    false,
                )
                withContext(Dispatchers.Main) {
                    cardService.addAPDUListener {
                        uiState.postValue(UiState.Scanning("Đã nhận được tín hiệu thẻ. Vui lòng giữ nguyên vị trí"))
                    }
                }
                var paceSucceeded = false
                try {
                    val cardAccessFile =
                        CardAccessFile(service.getInputStream(PassportService.EF_CARD_ACCESS))
                    val securityInfoCollection = cardAccessFile.securityInfos
                    for (securityInfo: SecurityInfo in securityInfoCollection) {
                        if (securityInfo is PACEInfo) {
                            service.doPACE(
                                bacKey,
                                securityInfo.objectIdentifier,
                                PACEInfo.toParameterSpec(securityInfo.parameterId),
                                null,
                            )
                            paceSucceeded = true
                        }
                    }
                } catch (e: CardServiceException) {
                    Log.w(TAG, "[Reader Card View Model] ${e.stackTrace} $e")
                    uiState.postValue(UiState.Error("Đã mất kết nối vui lòng thử lại"))
                } catch (e: Exception) {
                    Log.w(TAG, e)
                    uiState.postValue(UiState.Error("Error when return doPACE  ${e.stackTrace} $e"))
                }
                service.sendSelectApplet(paceSucceeded)
                if (!paceSucceeded) {
                    try {
                        service.getInputStream(PassportService.EF_COM).read()
                    } catch (e: Exception) {
                        service.doBAC(bacKey)
                        uiState.postValue(UiState.Error("Error when return EF_COM  ${e.stackTrace} $e"))
                    }
                }
                //com, sod, dg1, dg2, dg13, dg14, dg15
                val com = service.getInputStream(PassportService.EF_COM)
                val sod = service.getInputStream(PassportService.EF_SOD)
                val dg1 = service.getInputStream(PassportService.EF_DG1)
                val dg2 = service.getInputStream(PassportService.EF_DG2)
                val dg13 = service.getInputStream(PassportService.EF_DG13)
                val dg14 = service.getInputStream(PassportService.EF_DG14)
                val dg15 = service.getInputStream(PassportService.EF_DG15)


                val comFile = COMFile(com)
                val sodFile = SODFile(sod)
                val dg1File = DG1File(dg1)
                val dg2File = DG2File(dg2)
                val dg14File = DG14File(dg14)
                val dg15File = DG15File(dg15)
                val resultMrz = dg1File.mrzInfo

                val comValue = Base64.encodeToString(comFile.encoded, Base64.DEFAULT)
                    .replace("\n".toRegex(), "")
                val sodValue = Base64.encodeToString(sodFile.encoded, Base64.DEFAULT)
                    .replace("\n".toRegex(), "")
                val dg1Value = Base64.encodeToString(dg1File.encoded, Base64.DEFAULT)
                    .replace("\n".toRegex(), "")
                val dg2Value = Base64.encodeToString(dg2File.encoded, Base64.DEFAULT)
                    .replace("\n".toRegex(), "")
                val dg13Value = Base64.encodeToString(dg13.readBytes(), Base64.DEFAULT)
                    .replace("\n".toRegex(), "")
                val dg14Value = Base64.encodeToString(dg14File.encoded, Base64.DEFAULT)
                    .replace("\n".toRegex(), "")
                val dg15Value = Base64.encodeToString(dg15File.encoded, Base64.DEFAULT)
                    .replace("\n".toRegex(), "")
                uiState.postValue(UiState.Scanning("Đọc thẻ thành công"))
                uiState.postValue(
                    UiState.Success(
                        DataVerifyObject(
                            comValue, sodValue, dg1Value,
                            dg2Value,
                            dg13Value,
                            dg14Value,
                            dg15Value,
                            currentDataQrCode,
                            resultMrz.nationality
                        )
                    )
                )
                cardService.close()

            } catch (e: AccessDeniedException) {
                Log.w(TAG, "[Reader Card View Model] ${e.stackTrace} $e")
                uiState.postValue(UiState.Error("Sai mã MRZ vui lòng kiểm tra lại thông tin"))
            } catch (e: CardServiceException) {
                Log.w(TAG, "[Reader Card View Model] ${e.stackTrace} $e")
                uiState.postValue(UiState.Error("Đã mất kết nối vui lòng thử lại"))
            } catch (e: Exception) {
                Log.w(TAG, "[Reader Card View Model] ${e.stackTrace} $e")
                uiState.postValue(UiState.Error("Có lỗi xảy ra"))
            }
        }
    }


    fun getUiState(): LiveData<UiState<DataVerifyObject>> {
        return uiState
    }

    fun getWrongMRD(): Boolean {
        return isEnableWrong
    }


//    @SuppressLint("StaticFieldLeak")
//    inner class ReadTask(private val isoDep: IsoDep) :
//        AsyncTask<Void?, Void?, Exception?>() {
//        override fun doInBackground(vararg params: Void?): Exception? {
//            try {
//                isoDep.timeout = 10000
//                val cardService = CardService.getInstance(isoDep)
//                cardService.open()
//                val service = PassportService(
//                    cardService,
//                    PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
//                    PassportService.DEFAULT_MAX_BLOCKSIZE,
//                    false,
//                    false,
//                )
//                service.open()
//
//                service.addAPDUListener {
//                    uiState.postValue(UiState.Scan)
//                }
//                var paceSucceeded = false
//                try {
//                    val cardAccessFile =
//                        CardAccessFile(service.getInputStream(PassportService.EF_CARD_ACCESS))
//                    val securityInfoCollection = cardAccessFile.securityInfos
//                    for (securityInfo: SecurityInfo in securityInfoCollection) {
//                        if (securityInfo is PACEInfo) {
//                            service.doPACE(
//                                bacKey,
//                                securityInfo.objectIdentifier,
//                                PACEInfo.toParameterSpec(securityInfo.parameterId),
//                                null,
//                            )
//                            paceSucceeded = true
//                        }
//                    }
//                } catch (e: Exception) {
//                    Log.w(TAG, e)
//                }
//                service.sendSelectApplet(paceSucceeded)
//                if (!paceSucceeded) {
//                    try {
//                        service.getInputStream(PassportService.EF_COM).read()
//                    } catch (e: Exception) {
//                        service.doBAC(bacKey)
//                    }
//                }
//                val com = service.getInputStream(PassportService.EF_COM)
//                val sod = service.getInputStream(PassportService.EF_SOD)
//                val dg1 = service.getInputStream(PassportService.EF_DG1)
//                val dg2 = service.getInputStream(PassportService.EF_DG2)
//                val dg13 = service.getInputStream(PassportService.EF_DG13)
//                val dg14 = service.getInputStream(PassportService.EF_DG14)
//                val dg15 = service.getInputStream(PassportService.EF_DG15)
//
//                val comFile = COMFile(com)
//                val sodFile = SODFile(sod)
//                val dg1File = DG1File(dg1)
//                val dg2File = DG2File(dg2)
//                val dg13File = Base64.encodeToString(dg13.readBytes(), Base64.DEFAULT)
//                val dg14File = DG14File(dg14)
//                val dg15File = DG15File(dg15)
//
//                uiState.postValue(UiState.Scanning("Đọc thẻ thành công"))
//
//                uiState.postValue(
//                    UiState.Success(
//                        DataVerifyObject(
//                            comFile, sodFile, dg1File,
//                            dg2File,
//                            dg13File,
//                            dg14File,
//                            dg15File,
//                            currentDataQrCode,
//                        )
//                    )
//                )
//
//            } catch (e: AccessDeniedException) {
//                Log.w(TAG, "[Reader Card View Model] ${e.stackTrace} $e")
//                uiState.postValue(UiState.Error("Sai mã MRZ vui lòng kiểm tra lại thông tin"))
//            } catch (e: CardServiceException) {
//                Log.w(TAG, "[Reader Card View Model] ${e.stackTrace} $e")
//                uiState.postValue(UiState.Error("Đã mất kết nối vui lòng thử lại"))
//            } catch (e: Exception) {
//                Log.w(TAG, "[Reader Card View Model] ${e.stackTrace} $e")
//                uiState.postValue(UiState.Error("Có lỗi xảy ra"))
//            }
//            return null
//        }


}



