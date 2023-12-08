const databaseService = require('../services/databaseService');

exports.addNutritionData = async (request, h) => {
  try {
    const nutritionData = request.payload;

    const isValidFoodName = (foodName) => /^[a-zA-Z]+$/.test(foodName);

    if (!isValidFoodName(nutritionData.foodName)) {
      return h.response({
        status: 'fail',
        message: 'Masukkan nama makanan dengan huruf.',
      }).code(400);
    }

    const isNumeric = (value) => typeof value === 'number' && !Number.isNaN(value) && Number.isFinite(value);

    if (
      !isNumeric(nutritionData.calories)
  || !isNumeric(nutritionData.protein)
  || !isNumeric(nutritionData.carbohydrates)
  || !isNumeric(nutritionData.fat)
    ) {
      return h.response({
        status: 'fail',
        message: 'Masukkan nilai yang valid pada data nutrisi',
      }).code(400);
    }

    const documentId = await databaseService.addNutritionData(nutritionData);

    const addedNutrition = {
      id: documentId,
    };

    return h.response({
      status: 'success',
      data: {
        nutrition: addedNutrition,
      },
    }).code(201);
  } catch (error) {
    console.error('Error adding nutrition data:', error);
    return h.response({
      statusCode: 500,
      error: 'Internal Server Error',
      message: `Gagal menambahkan data nutrisi makanan: ${error.message}`,
    }).code(500);
  }
};

exports.getAllNutritions = async (request, h) => {
  try {
    const nutritions = await databaseService.getAllNutritions();

    const showNutritions = nutritions.map((nutrition) => ({
      id: nutrition.id,
      foodName: nutrition.foodName,
      calories: nutrition.calories,
      protein: nutrition.protein,
      carbohydrates: nutrition.carbohydrates,
      fat: nutrition.fat,
    }));

    return h.response({
      status: 'success',
      data: {
        nutritions: showNutritions,
      },
    }).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      status: 'error',
      message: `Failed to get nutrition data from Firestore: ${error.message}`,
    }).code(500);
  }
};

exports.searchNutritionsByName = async (request, h) => {
  try {
    const { foodName } = request.query;
    const nutritions = await databaseService.searchNutritionsByName(foodName);

    if (nutritions.length === 0) {
      return h.response({
        status: 'fail',
        message: 'Food nutritional data not available',
      }).code(404);
    }

    const showNutritions = nutritions.map((nutrition) => ({
      id: nutrition.id,
      foodName: nutrition.foodName,
      calories: nutrition.calories,
      protein: nutrition.protein,
      carbohydrates: nutrition.carbohydrates,
      fat: nutrition.fat,
    }));

    return h.response({
      status: 'success',
      data: {
        nutritions: showNutritions,
      },
    }).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      status: 'error',
      message: `Failed to search nutrition data by name: ${error.message}`,
    }).code(500);
  }
};

exports.getNutritionById = async (request, h) => {
  try {
    const { nutritionId } = request.params;
    const nutrition = await databaseService.getNutritionById(nutritionId);

    if (!nutrition) {
      return h.response({
        status: 'fail',
        message: 'Food nutritional data not available',
      }).code(404);
    }

    const showNutrition = {
      id: nutrition.id,
      foodName: nutrition.foodName,
      calories: nutrition.calories,
      protein: nutrition.protein,
      carbohydrates: nutrition.carbohydrates,
      fat: nutrition.fat,
    };

    return h.response({
      status: 'success',
      data: {
        nutrition: showNutrition,
      },
    }).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      status: 'error',
      message: `Failed to get nutrition data by ID: ${error.message}`,
    }).code(500);
  }
};

exports.updateNutritionData = async (request, h) => {
  try {
    const { nutritionId } = request.params;
    const updatedData = request.payload;

    const getNutritionID = await databaseService.getNutritionById(nutritionId);

    if (!getNutritionID) {
      return h.response({
        statusCode: 404,
        error: 'Not Found',
        message: 'Nutrition data not found.',
      }).code(404);
    }
    await databaseService.updateNutritionData(nutritionId, updatedData);
    const updatedNutrition = await databaseService.getNutritionById(nutritionId);
    const showUpdate = {
      status: 'success',
      message: 'Nutrition data successfully updated.',
      data: {
        nutrition: {
          id: updatedNutrition.id,
          foodName: updatedNutrition.foodName,
          calories: updatedNutrition.calories,
          protein: updatedNutrition.protein,
          carbohydrates: updatedNutrition.carbohydrates,
          fat: updatedNutrition.fat,
        },
      },
    };

    return h.response(showUpdate).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      statusCode: 500,
      error: 'Internal Server Error',
      message: error.message,
    }).code(500);
  }
};

exports.deleteNutritionData = async (request, h) => {
  try {
    const { nutritionId } = request.params;

    const existingNutrition = await databaseService.getNutritionById(nutritionId);

    if (!existingNutrition) {
      return h.response({
        statusCode: 404,
        error: 'Not Found',
        message: 'Nutrition data not found.',
      }).code(404);
    }

    await databaseService.deleteNutritionData(nutritionId);

    return h.response({
      status: 'success',
      message: 'Nutrition data successfully deleted.',
    }).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      statusCode: 500,
      error: 'Internal Server Error',
      message: error.message,
    }).code(500);
  }
};
