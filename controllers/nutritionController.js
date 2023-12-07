const databaseService = require('../services/databaseService');

exports.addNutritionData = async (request, h) => {
  try {
    const nutritionData = request.payload;
    const documentId = await databaseService.addNutritionData(nutritionData);

    return h.response({ documentId, message: 'Data nutrisi makanan berhasil ditambahkan.' }).code(201);
  } catch (error) {
    console.error('Error adding nutrition data:', error);
    return h.response({
      statusCode: 500,
      error: 'Internal Server Error',
      message: `Gagal menambahkan data nutrisi makanan ke Firestore: ${error.message}`,
    }).code(500);
  }
};

exports.getNutritionsByName = async (request, h) => {
  try {
    const { foodName } = request.query;
    const nutritions = await databaseService.getNutritionsByName(foodName);
    return h.response(nutritions).code(200);
  } catch (error) {
    console.error(error);
    return h.response({
      statusCode: 500,
      error: 'Internal Server Error',
      message: error.message,
    }).code(500);
  }
};

exports.updateNutritionData = async (request, h) => {
  try {
    const { nutritionId } = request.params;
    const updatedData = request.payload;
    await databaseService.updateNutritionData(nutritionId, updatedData);
    return h.response().code(204);
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
    await databaseService.deleteNutritionData(nutritionId);
    return h.response().code(204);
  } catch (error) {
    console.error(error);
    return h.response({
      statusCode: 500,
      error: 'Internal Server Error',
      message: error.message,
    }).code(500);
  }
};
