package com.stevesoltys.backup.ui

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stevesoltys.backup.R
import kotlinx.android.synthetic.main.fragment_recovery_code_output.*

class RecoveryCodeOutputFragment : Fragment() {

    private lateinit var viewModel: RecoveryCodeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_recovery_code_output, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(RecoveryCodeViewModel::class.java)

        setGridParameters(wordList)
        wordList.adapter = RecoveryCodeAdapter(viewModel.wordList)

        confirmCodeButton.setOnClickListener { viewModel.onConfirmButtonClicked() }
    }

    private fun setGridParameters(list: RecyclerView) {
        val layoutManager = list.layoutManager as GridLayoutManager
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager.orientation = RecyclerView.VERTICAL
            layoutManager.spanCount = 4
        } else {
            layoutManager.orientation = RecyclerView.HORIZONTAL
            layoutManager.spanCount = 6
        }
    }

}