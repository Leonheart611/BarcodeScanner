package dynamia.com.barcodescanner.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.data.model.*
import dynamia.com.barcodescanner.util.readJsonAsset
import dynamia.com.barcodescanner.util.showToast
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupListener()
    }

    private fun setupListener(){
        btn_login.setOnClickListener {
            setDataFromJsonLocal()
            context?.showToast("Login Success")
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    private fun setDataFromJsonLocal(){
        val pickingListHeader = context?.readJsonAsset("PickingListHeader.json")
        val pickingListLine = context?.readJsonAsset("PickingListLine.json")
        val pickingListScanEntries = context?.readJsonAsset("PickingListScanEntries.json")

        val pickingListHeaders = Gson().fromJson(pickingListHeader,PickingListHeader::class.java)
        pickingListHeaders.value?.forEach {pickingListValue->
            pickingListValue?.let {
                viewModel.pickingListRepository.insertPickingListHeader(it)
            }
        }
        val pickingListLines = Gson().fromJson(pickingListLine,PickingListLine::class.java)
        pickingListLines.value.forEach {
            viewModel.pickingListRepository.insertPickingListLine(it)
        }
        val pickingListScanEntriesList = Gson().fromJson(pickingListScanEntries,PickingListScanEntries::class.java)
        pickingListScanEntriesList.value.forEach {
            viewModel.pickingListRepository.insertPickingListScanEntries(it)
        }

        val receiptImportHeader = context?.readJsonAsset("ReceiptImportHeader.json")
        val receiptImportLine = context?.readJsonAsset("ReceiptImportLine.json")
        val receiptImportScanEntries = context?.readJsonAsset("ReceiptImportScanEntries.json")

        Gson().fromJson(receiptImportHeader,ReceiptImportHeader::class.java).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportHeader(it)
        }
        Gson().fromJson(receiptImportLine,ReceiptImportLine::class.java).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportLine(it)
        }
        Gson().fromJson(receiptImportScanEntries,ReceiptImportScanEntries::class.java).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportScanEntries(it)
        }

        val receiptLocalHeader = context?.readJsonAsset("ReceiptLocalHeader.json")
        val receiptLocalLine = context?.readJsonAsset("ReceiptLocalLine.json")
        val receiptLocalScanEntries = context?.readJsonAsset("ReceiptLocalScanEntries.json")

        Gson().fromJson(receiptLocalHeader,ReceiptLocalHeader::class.java).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalHeader(it)
        }
        Gson().fromJson(receiptLocalLine,ReceiptLocalLine::class.java).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalLine(it)
        }
        Gson().fromJson(receiptLocalScanEntries,ReceiptLocalScanEntries::class.java).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalScanEntries(it)
        }

    }

}
