require('dotenv').config();
const Hapi = require('@hapi/hapi');
const nutritionRoutes = require('./routes/nutritionRoutes');

const init = async () => {
  const server = Hapi.server({
    port: process.env.PORT || 5000,
    host: process.env.HOST || 'localhost',
    routes: {
      cors: true,
    },
  });

  server.route(nutritionRoutes);

  server.ext('onPreResponse', (request, h) => {
    const { response } = request;

    if (response.isBoom) {
      console.error('Error:', response.message);

      if (response.output.statusCode === 404) {
        return h.response({ success: false, error: 'Not Found' }).code(404);
      }
      return h.response({ success: false, error: 'Internal Server Error' }).code(500);
    }

    return h.continue;
  });

  await server.start();
  console.log(`Server is running on ${server.info.uri}`);
};

process.on('unhandledRejection', (err) => {
  console.log(err);
  process.exit(1);
});

init();
