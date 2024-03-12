package dynamia.com.barcodescanner.ui.login

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dynamia.com.barcodescanner.R
import dynamia.com.barcodescanner.databinding.BottomsheetUserListBinding
import dynamia.com.barcodescanner.databinding.RefreshWarningDialogBinding
import dynamia.com.barcodescanner.ui.history.HistoryType
import dynamia.com.barcodescanner.ui.transferstore.transferinput.TransferHistoryBottomSheet
import dynamia.com.core.data.entinty.UserData
import dynamia.com.core.util.EventObserver
import dynamia.com.core.util.io
import dynamia.com.core.util.showLongToast
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class UserListBottomSheet : BottomSheetDialogFragment(), UserListAdapter.UserListListener {
    private val viewModel: UserListViewModel by viewModels()

    private lateinit var _viewBinding: BottomsheetUserListBinding
    val viewBinding by lazy { _viewBinding }
    val userAdapter = UserListAdapter(this)
    var clickListener: ((UserData) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _viewBinding = BottomsheetUserListBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as BottomSheetDialog
            setupFullHeight(bottomSheetDialog)
            dialogInterface.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheetDialog: BottomSheetDialog) {
        val bottomSheet =
            bottomSheetDialog.findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
        val layoutParams = bottomSheet.layoutParams
        val windowHeight = getWindowHeight()
        if (layoutParams != null) {
            layoutParams.height = windowHeight
        }
        bottomSheet.layoutParams = layoutParams
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private fun getWindowHeight(): Int {
        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.rvUserList.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        }
        viewModel.userRepository.getAllUserData().observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) showEmptyState()
            else userAdapter.submitList(it)
        }
        viewModel.deleteStatus.observe(viewLifecycleOwner, EventObserver {
            if (it) context?.showLongToast("Success Delete Data")
        })
    }

    private fun showEmptyState() {
        viewBinding.rvUserList.isVisible = false
        viewBinding.includeEmptyList.root.isVisible = true
    }

    override fun onclicklistener(data: UserData) {
        showDialog("Data yang di hapus tidak dapat dikembalikan, apakah anda yakin?") {
            viewModel.deleteUser(data)
        }
    }

    override fun onSetValue(data: UserData) {
        clickListener?.let {
            it(data)
            dismiss()
        }
    }

    private fun showDialog(
        warningMessage: String? = null,
        call: () -> Unit,
    ) {
        context?.let { context ->
            val dialog = Dialog(context)
            with(dialog) {
                val bind = RefreshWarningDialogBinding.inflate(layoutInflater)
                setContentView(bind.root)
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window
                    ?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                with(bind) {
                    warningMessage?.let { message ->
                        tvWarningLogoutRefresh.text = message
                    }
                    btnRefreshYes.setOnClickListener {
                        call()
                        dismiss()
                    }
                    btnRefreshNo.setOnClickListener {
                        dismiss()
                    }
                }
                show()
            }
        }
    }

    companion object {
        fun newInstance(click: (UserData) -> Unit): UserListBottomSheet {
            return UserListBottomSheet().apply { clickListener = click }
        }
    }
}