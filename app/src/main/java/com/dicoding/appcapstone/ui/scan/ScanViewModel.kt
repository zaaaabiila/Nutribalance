package com.dicoding.appcapstone.ui.scan

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.appcapstone.remote.CalorieNinjaApiClient
import kotlinx.coroutines.launch

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    val labels: List<String> by lazy {
        application.assets.open("labels.txt").bufferedReader().readLines()
    }

    // Save the query as a String
    private val _query = MutableLiveData<String>()
    val query: LiveData<String>
        get() = _query

    fun setQuery(query: String) {
        _query.value = query
    }

    private val _bitmap = MutableLiveData<Bitmap>()
    val bitmap: LiveData<Bitmap>
        get() = _bitmap

    private val _calorieInfo = MutableLiveData<String>()
    val calorieInfo: LiveData<String>
        get() = _calorieInfo

    private val apiClient = CalorieNinjaApiClient()

    fun setBitmap(bitmap: Bitmap) {
        _bitmap.value = bitmap
    }

    fun getCalorieInfo(query: String) {
        viewModelScope.launch {
            try {
                val response = apiClient.apiService.getNutritionInfo(query)
                if (response.isSuccessful) {
                    val items = response.body()?.items
                    if (!items.isNullOrEmpty()) {
                        val firstItem = items[0]
                        _calorieInfo.value = """
                        Name: ${firstItem?.name}
                        Calories: ${firstItem?.calories}
                        Total Fat: ${firstItem?.fatTotalG}g
                        Saturated Fat: ${firstItem?.fatSaturatedG}g
                        Cholesterol: ${firstItem?.cholesterolMg}mg
                        Sodium: ${firstItem?.sodiumMg}mg
                        Potassium: ${firstItem?.potassiumMg}mg
                        Total Carbohydrates: ${firstItem?.carbohydratesTotalG}g
                        Fiber: ${firstItem?.fiberG}g
                        Sugar: ${firstItem?.sugarG}g
                        Protein: ${firstItem?.proteinG}g
                        Serving Size: ${firstItem?.servingSizeG}g
                    """.trimIndent()
                    } else {
                        _calorieInfo.value = "No nutrition information available."
                    }
                } else {
                    _calorieInfo.value = "Failed to get nutrition info."
                }
            } catch (e: Exception) {
                _calorieInfo.value = "Error: ${e.message}"
            }
        }
    }
}
