const Joi = require('joi');
const {
  addNutritionData,
  getAllNutritions,
  searchNutritionsByName,
  updateNutritionData,
  deleteNutritionData,
  getNutritionById,
} = require('../controllers/nutritionController');

module.exports = [
  {
    method: 'POST',
    path: '/nutritions',
    handler: addNutritionData,
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
    path: '/nutritions',
    handler: getAllNutritions,
  },

  {
    method: 'GET',
    path: '/nutritions/search',
    handler: searchNutritionsByName,
    options: {
      validate: {
        query: Joi.object({
          foodName: Joi.string().required(),
        }),
      },
    },
  },

  {
    method: 'GET',
    path: '/nutritions/{nutritionId}',
    handler: getNutritionById,
  },

  {
    method: 'PUT',
    path: '/nutritions/{nutritionId}',
    handler: updateNutritionData,
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
    path: '/nutritions/{nutritionId}',
    handler: deleteNutritionData,
  },
];
