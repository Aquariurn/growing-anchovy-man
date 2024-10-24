package com.example.growinganchovyman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class PageFragment : Fragment() {

    companion object {
        fun newInstance(content: String): PageFragment {
            val args = Bundle()
            args.putString("content", content)
            val fragment = PageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_page, container, false)
        val contentTextView = view.findViewById<TextView>(R.id.content)
        val content = arguments?.getString("content")
        contentTextView.text = content
        return view
    }
}