package com.stevesoltys.backup.ui.storage

import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.stevesoltys.backup.R
import com.stevesoltys.backup.ui.INTENT_EXTRA_IS_RESTORE
import com.stevesoltys.backup.ui.REQUEST_CODE_OPEN_DOCUMENT_TREE
import kotlinx.android.synthetic.main.fragment_storage_root.*

private val TAG = StorageRootsFragment::class.java.simpleName

internal class StorageRootsFragment : Fragment(), StorageRootClickedListener {

    companion object {
        fun newInstance(isRestore: Boolean): StorageRootsFragment {
            val f = StorageRootsFragment()
            f.arguments = Bundle().apply {
                putBoolean(INTENT_EXTRA_IS_RESTORE, isRestore)
            }
            return f
        }
    }

    private lateinit var viewModel: StorageViewModel

    private val adapter by lazy { StorageRootAdapter(viewModel.isRestoreOperation, this) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_storage_root, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = if (arguments!!.getBoolean(INTENT_EXTRA_IS_RESTORE)) {
            ViewModelProviders.of(requireActivity()).get(RestoreStorageViewModel::class.java)
        } else {
            ViewModelProviders.of(requireActivity()).get(BackupStorageViewModel::class.java)
        }

        if (viewModel.isRestoreOperation) {
            titleView.text = getString(R.string.storage_fragment_restore_title)
            backView.visibility = VISIBLE
            backView.setOnClickListener { requireActivity().finishAfterTransition() }
        }

        listView.adapter = adapter

        viewModel.storageRoots.observe(this, Observer { roots -> onRootsLoaded(roots) })
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadStorageRoots()
    }

    private fun onRootsLoaded(roots: List<StorageRoot>) {
        progressBar.visibility = INVISIBLE
        adapter.setItems(roots)
    }

    override fun onClick(root: StorageRoot) {
        val intent = Intent(requireContext(), PermissionGrantActivity::class.java)
        intent.data = DocumentsContract.buildTreeDocumentUri(root.authority, root.documentId)
        intent.addFlags(FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION)
        startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT_TREE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_OPEN_DOCUMENT_TREE) {
            viewModel.onUriPermissionGranted(result)
        } else {
            super.onActivityResult(requestCode, resultCode, result)
        }
    }

}

internal interface StorageRootClickedListener {
    fun onClick(root: StorageRoot)
}