package com.dicoding.appcapstone.ui.article

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.appcapstone.R

class ArticleFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var noInternetTextView: TextView
    private lateinit var refreshButton: Button
    private lateinit var viewModel: ArticleViewModel
    private var articleAdapter: ArticleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_article, container, false)

        // Find the RecyclerView in the layout
        recyclerView = view.findViewById(R.id.recyclerView)

        progressBar = view.findViewById(R.id.progressBar)

        // Find the TextView in the layout
        noInternetTextView = view.findViewById(R.id.noInternetTextView)

        refreshButton = view.findViewById(R.id.refreshButton)

        // Set a LinearLayoutManager for the RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this, ArticleViewModelFactory(requireActivity().application)).get(
            ArticleViewModel::class.java)

        viewModel.recipes.observe(viewLifecycleOwner, Observer { recipes ->
            // Update UI with recipes
            if (recyclerView.adapter == null) {
                val adapter = ArticleAdapter(requireActivity(), recipes)
                recyclerView.adapter = adapter
            }
        })

        // Observe isLoading
        viewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            noInternetTextView.visibility = View.GONE
            refreshButton.visibility = View.GONE
        })

        // Observe errorMessage
        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            noInternetTextView.visibility = if (errorMessage == "No internet connection") View.VISIBLE else View.GONE
            refreshButton.visibility = if (errorMessage == "No internet connection") View.VISIBLE else View.GONE
        })

        viewModel.recipes.observe(viewLifecycleOwner, Observer { recipes ->
            // Update UI with recipes
            if (recyclerView.adapter == null) {
                articleAdapter = ArticleAdapter(requireActivity(), recipes)
                recyclerView.adapter = articleAdapter
            }
        })

        refreshButton.setOnClickListener {
            // Check for internet connection and fetch data
            viewModel.fetchRecipes()
        }

        if (savedInstanceState == null) {
            viewModel.fetchRecipes()
        }
    }
}
