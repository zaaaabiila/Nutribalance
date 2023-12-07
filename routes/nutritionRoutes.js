const Joi = require('joi');
const nutritionController = require('../controllers/nutritionController');

module.exports = [
  {
    method: 'POST',
    path: '/api/nutritions',
    handler: nutritionController.addNutritionData,
    options: {
      validate: {
        payload: Joi.object({
          foodName: Joi.string().required(),
          calories: Joi.number().required(),
          protein: Joi.number().required(),
          carbohydrates: Joi.number().required(),
          fat: Joi.number().required(),
        }),
      },
    },
  },
  {
    method: 'GET',
    path: '/api/nutritions/search',
    handler: nutritionController.getNutritionsByName,
  },

  {
    method: 'PUT',
    path: '/api/nutritions/{nutritionId}',
    handler: nutritionController.updateNutritionData,
    options: {
      validate: {
        payload: Joi.object({
          foodName: Joi.string(),
          calories: Joi.number(),
          protein: Joi.number(),
          carbohydrates: Joi.number(),
          fat: Joi.number(),
        }),
      },
    },
  },

  {
    method: 'DELETE',
    path: '/api/nutritions/{nutritionId}',
    handler: nutritionController.deleteNutritionData,
  },
];
