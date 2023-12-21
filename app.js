const admin = require('firebase-admin');
const Hapi = require('@hapi/hapi');
const { SecretManagerServiceClient } = require('@google-cloud/secret-manager');

const projectId = 'nutribalance-project';

const getSecret = async (secretName) => {
  const client = new SecretManagerServiceClient();
  const [version] = await client.accessSecretVersion({
    name: `projects/${projectId}/secrets/${secretName}/versions/latest`,
  });
  return version.payload.data.toString();
};

const serviceAccountString = await getSecret('service-account-key');
const serviceAccount = JSON.parse(serviceAccountString);

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

require('dotenv').config();
const nutritionRoutes = require('./routes/nutritionRoute');

const init = async () => {
  const server = Hapi.server({
    port: 3000,
    host: process.env.NODE_ENV !== 'production' ? 'localhost' : '0.0.0.0',
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
