package com.chordsense.chordapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.chordsense.chordapp.MainActivity
import com.chordsense.chordapp.R
import com.chordsense.chordapp.databinding.FragmentCsplusBinding

class CSPlusFragment : Fragment() {

private var _binding: FragmentCsplusBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_csplus, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val purchase: Button = view.findViewById(R.id.purchase)
        purchase.setOnClickListener {
            (activity as MainActivity).purchase()
        }
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}