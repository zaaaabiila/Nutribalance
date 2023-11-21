const dietagramApiService = require('../services/dietagramAPIService');

async function getNutritionInfo(foodName) {
  try {
    if (!foodName) {
      throw new Error('Nama makanan harus disertakan dalam pencarian');
    }
    const dietagramData = await dietagramApiService.getFoodInfo(foodName);

    if (!dietagramData || dietagramData.length === 0) {
      throw new Error('Informasi nutrisi untuk makanan ini tidak tersedia.');
    }

    const nutritionInfo = dietagramData.map((item) => ({
      calories: item.calories,
      protein: item.protein,
      fat: item.fat,
      carbohydrates: item.carbohydrates,
    }));

    return nutritionInfo;
  } catch (error) {
    console.error('Error in getNutritionInfo:', error.message);

    if (error.message.includes('Nama makanan harus disertakan dalam pencarian')) {
      const errorObject = new Error('Nama makanan harus disertakan dalam pencarian.');
      errorObject.statusCode = 400;
      throw errorObject;
    }

    if (error.message.includes('Informasi nutrisi untuk makanan ini tidak tersedia.')) {
      const errorObject = new Error('Informasi nutrisi untuk makanan ini tidak tersedia.');
      errorObject.statusCode = 404;
      throw errorObject;
    }

    const errorObject = new Error('Terjadi kesalahan saat memuat informasi nutrisi.');
    errorObject.statusCode = 500;
    throw errorObject;
  }
}

module.exports = { getNutritionInfo };
