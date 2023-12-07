const admin = require('firebase-admin');
const Hapi = require('@hapi/hapi');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

require('dotenv').config();
const nutritionRoutes = require('./routes/nutritionRoutes');

const init = async () => {
  const server = Hapi.server({
    port: 3000,
    host: 'localhost',
    routes: {
      cors: {
        origin: ['*'],
      },
    },
  });

  server.route(nutritionRoutes);

  await server.start();
  console.log(`Server is running on ${server.info.uri}`);
};

process.on('unhandledRejection', (err) => {
  console.log(err);
  process.exit(1);
});

init();
