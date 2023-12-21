package com.dicoding.appcapstone.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.appcapstone.R

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var articleAdapter: HomeArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        articleAdapter = HomeArticleAdapter(requireContext())

        val sharedPreferences = requireActivity().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        val name = sharedPreferences.getString("name", "")

        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        nameTextView.text = name

        val emailTextView: TextView = view.findViewById(R.id.emailTextView)
        emailTextView.text = email

        val horizontalRecyclerView: RecyclerView = view.findViewById(R.id.horizontalRecyclerView)
        horizontalRecyclerView.adapter = articleAdapter

        val searchEditText: EditText = view.findViewById(R.id.search_edit_text)

        val searchButton: Button = view.findViewById(R.id.search_button)
        searchButton.setOnClickListener {
            val query = searchEditText.text.toString()
            viewModel.fetchNutritionInfo(query)
            // Save the query to SharedPreferences
            viewModel.getSharedPreferences().edit().putString("query", query).apply()
        }

        // Observe the LiveData in the ViewModel and update the adapter when needed
        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            articleAdapter.setItems(recipes)
        }

        viewModel.nutritionInfo.observe(viewLifecycleOwner) { info ->
            val resultsBox: TextView = view.findViewById(R.id.results_box)
            resultsBox.text = info
            // Save the result to SharedPreferences
            viewModel.getSharedPreferences().edit().putString("result", info).apply()
        }

        // Retrieve the query and result from SharedPreferences
        val savedQuery = viewModel.getSharedPreferences().getString("query", "")
        val savedResult = viewModel.getSharedPreferences().getString("result", "")
        if (!savedQuery.isNullOrEmpty()) {
            searchEditText.setText(savedQuery)
        }
        if (!savedResult.isNullOrEmpty()) {
            val resultsBox: TextView = view.findViewById(R.id.results_box)
            resultsBox.text = savedResult
        }

        // Trigger the fetchRecipes function in the ViewModel
        viewModel.fetchRecipes()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
    }
}
