package com.dicoding.appcapstone.ui.article

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.dicoding.appcapstone.remote.TastyApiClient
import com.dicoding.appcapstone.response.ArticleResponse
import com.dicoding.appcapstone.response.ResultsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArticleViewModel(private val application: Application) : AndroidViewModel(application) {
    private val apiClient = TastyApiClient()
    val recipes: MutableLiveData<List<ResultsItem>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()


    private val sharedPreferences = application.getSharedPreferences("ArticleData", Context.MODE_PRIVATE)

    fun fetchRecipes() {
        val savedRecipes = sharedPreferences.getString("recipes", null)
        if (savedRecipes != null) {
            val listType = object : TypeToken<List<ResultsItem>>() {}.type
            recipes.value = Gson().fromJson(savedRecipes, listType)
            return
        }

        // Check for internet connection
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            // Show ProgressBar
            isLoading.value = true

            // Fetch data from the API
            apiClient.apiService.getRecipes().enqueue(object : Callback<ArticleResponse> {

                override fun onResponse(call: Call<ArticleResponse>, response: Response<ArticleResponse>) {
                    if (response.isSuccessful) {
                        val resultsItemList = response.body()?.results?.filterNotNull()
                        // Filter out items with null values for the mentioned fields
                        val filteredResultsItemList = resultsItemList?.filter {
                            it.nutrition != null && it.name != null && it.description != null && it.thumbnailUrl != null && it.originalVideoUrl != null
                        }
                        if (!filteredResultsItemList.isNullOrEmpty()) {
                            recipes.value = filteredResultsItemList!!
                            sharedPreferences.edit().putString("recipes", Gson().toJson(filteredResultsItemList)).apply()
                        }
                    } else {
                        // Handle error
                        errorMessage.value = "Error: ${response.errorBody()}"
                    }
                    isLoading.value = false
                }

                override fun onFailure(call: Call<ArticleResponse>, t: Throwable) {
                    // Handle failure
                    errorMessage.value = t.localizedMessage
                    isLoading.value = false
                }
            })
        } else {
            // Show no internet connection message
            errorMessage.value = "No internet connection"
            isLoading.value = false
        }
    }
}

