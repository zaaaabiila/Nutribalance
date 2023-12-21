package com.dicoding.appcapstone.ui.home

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.appcapstone.remote.CalorieNinjaApiClient
import com.dicoding.appcapstone.remote.TastyApiClient
import com.dicoding.appcapstone.response.ArticleResponse
import com.dicoding.appcapstone.response.ResultsItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val apiClient = TastyApiClient()
    val recipes: MutableLiveData<List<ResultsItem>> = MutableLiveData()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val calApiClient = CalorieNinjaApiClient()
    val nutritionInfo: MutableLiveData<String> = MutableLiveData()

    private val sharedPreferences =
        application.getSharedPreferences("ArticleData", Application.MODE_PRIVATE)

    fun getSharedPreferences(): SharedPreferences {
        return sharedPreferences
    }
    fun fetchRecipes() {
        // Check if recipes are already saved in SharedPreferences
        val savedRecipes = sharedPreferences.getString("recipes", null)
        if (savedRecipes != null) {
            val listType = object : TypeToken<List<ResultsItem>>() {}.type
            recipes.value = Gson().fromJson(savedRecipes, listType)
            return
        }

        // Check for internet connection
        val cm = getApplication<Application>().getSystemService(Application.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork: android.net.NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            // Show ProgressBar
            isLoading.value = true

            // Fetch data from the API
            apiClient.apiService.getRecipes().enqueue(object : Callback<ArticleResponse> {
                override fun onResponse(
                    call: Call<ArticleResponse>,
                    response: Response<ArticleResponse>
                ) {
                    if (response.isSuccessful) {
                        val resultsItemList = response.body()?.results?.filterNotNull()
                        // Filter out items with null values for the mentioned fields
                        val filteredResultsItemList = resultsItemList?.filter {
                            it.nutrition != null && it.name != null && it.description != null && it.thumbnailUrl != null && it.originalVideoUrl != null
                        }
                        if (!filteredResultsItemList.isNullOrEmpty()) {
                            recipes.value = filteredResultsItemList!!
                            sharedPreferences.edit()
                                .putString("recipes", Gson().toJson(filteredResultsItemList)).apply()
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

    fun fetchNutritionInfo(query: String) {
        // Check for internet connection
        val cm = getApplication<Application>().getSystemService(Application.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork: android.net.NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            viewModelScope.launch {
                try {
                    val response = calApiClient.apiService.getNutritionInfo(query)
                    if (response.isSuccessful) {
                        val items = response.body()?.items
                        if (!items.isNullOrEmpty()) {
                            val item = items[0]
                            val info =
                                "Calories: ${item?.calories}\n" +
                                        "Protein: ${item?.proteinG}g\n" +
                                        "Fat: ${item?.fatTotalG}g\n" +
                                        "Carbs: ${item?.carbohydratesTotalG}g"
                            nutritionInfo.postValue(info)
                        } else {
                            // No nutrition value
                            nutritionInfo.postValue("No Nutrition Value")
                        }
                    } else {
                        nutritionInfo.postValue("Error: ${response.errorBody()}")
                    }
                } catch (e: Exception) {
                    // Handle the error
                    errorMessage.postValue(e.message)
                }
            }
        } else {
            // Show no internet connection message
            errorMessage.postValue("No internet connection")
        }
    }
}
