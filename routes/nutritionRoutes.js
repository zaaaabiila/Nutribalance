const Joi = require('joi');
const nutritionController = require('../controllers/nutritionController');

module.exports = [
  {
    method: 'GET',
    path: '/nutrition',
    config: {
      validate: {
        query: Joi.object({
          foodName: Joi.string().required(),
        }),
      },
    },

    handler: async (request, h) => {
      try {
        const { foodName } = request.query;
        const nutritionInfo = await nutritionController.getNutritionInfo(foodName);

        return h.response({ success: true, data: nutritionInfo }).code(200);
      } catch (error) {
        console.error('Error in /nutrition route:', error);

        if (error.statusCode) {
          return h.response({ success: false, error: error.message }).code(error.statusCode);
        }

        return h.response({ success: false, error: 'Internal Server Error' }).code(500);
      }
    },
  },
];
