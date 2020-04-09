package dynamia.com.barcodescanner.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import dynamia.com.barcodescanner.R
import dynamia.com.core.util.readJsonAsset
import dynamia.com.core.util.showToast
import dynamia.com.core.data.model.PickingListHeader
import dynamia.com.core.data.model.PickingListLine
import dynamia.com.core.data.model.PickingListScanEntries
import dynamia.com.core.data.model.ReceiptImportHeader
import dynamia.com.core.data.model.ReceiptImportLine
import dynamia.com.core.data.model.ReceiptImportScanEntries
import dynamia.com.core.data.model.ReceiptLocalHeader
import dynamia.com.core.data.model.ReceiptLocalLine
import dynamia.com.core.data.model.ReceiptLocalScanEntries
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (viewModel.checkLoginVariables())
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

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

    private fun setupListener() {
        btn_login.setOnClickListener {
            if (checkNotEmpty()) {
                viewModel.saveLoginVariable(
                    hostname = et_server_host.text.toString(),
                    username = tied_username.text.toString(),
                    password = tied_password.text.toString(),
                    employee = et_employee.text.toString()
                )
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                context?.showToast("Please fill all form")
            }
        }
    }

    private fun checkNotEmpty(): Boolean {
        var result = true
        if (tied_password.text.toString().isEmpty()) {
            result = false
        }
        if (tied_username.text.toString().isEmpty()) {
            result = false
        }
        if (et_server_host.text.toString().isEmpty()) {
            result = false
        }
        if (et_employee.text.toString().isEmpty()) {
            result = false
        }
        return result
    }

    private fun setDataFromJsonLocal() {
        val pickingListHeader = context?.readJsonAsset("PickingListHeader.json")
        val pickingListLine = context?.readJsonAsset("PickingListLine.json")
        val pickingListScanEntries = context?.readJsonAsset("PickingListScanEntries.json")

        val pickingListHeaders = Gson().fromJson(pickingListHeader, PickingListHeader::class.java)
        pickingListHeaders.value?.forEach { pickingListValue ->
            pickingListValue?.let {
                viewModel.pickingListRepository.insertPickingListHeader(it)
            }
        }
        val pickingListLines = Gson().fromJson(pickingListLine, PickingListLine::class.java)
        pickingListLines.value.forEach {
            viewModel.pickingListRepository.insertPickingListLine(it)
        }
        val pickingListScanEntriesList = Gson().fromJson(
            pickingListScanEntries,
            PickingListScanEntries::class.java
        )
        pickingListScanEntriesList.value.forEach {
            viewModel.pickingListRepository.insertPickingListScanEntries(it)
        }

        val receiptImportHeader = context?.readJsonAsset("ReceiptImportHeader.json")
        val receiptImportLine = context?.readJsonAsset("ReceiptImportLine.json")
        val receiptImportScanEntries = context?.readJsonAsset("ReceiptImportScanEntries.json")

        Gson().fromJson(receiptImportHeader, ReceiptImportHeader::class.java).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportHeader(it)
        }
        Gson().fromJson(receiptImportLine, ReceiptImportLine::class.java).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportLine(it)
        }
        Gson().fromJson(
            receiptImportScanEntries,
            ReceiptImportScanEntries::class.java
        ).value.forEach {
            viewModel.receiptImportRepository.insertReceiptImportScanEntries(it)
        }

        val receiptLocalHeader = context?.readJsonAsset("ReceiptLocalHeader.json")
        val receiptLocalLine = context?.readJsonAsset("ReceiptLocalLine.json")
        val receiptLocalScanEntries = context?.readJsonAsset("ReceiptLocalScanEntries.json")

        Gson().fromJson(receiptLocalHeader, ReceiptLocalHeader::class.java).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalHeader(it)
        }
        Gson().fromJson(receiptLocalLine, ReceiptLocalLine::class.java).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalLine(it)
        }
        Gson().fromJson(
            receiptLocalScanEntries,
            ReceiptLocalScanEntries::class.java
        ).value.forEach {
            viewModel.receiptLocalRepository.insertReceiptLocalScanEntries(it)
        }

    }

}
