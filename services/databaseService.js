const admin = require('firebase-admin');
const { SecretManagerServiceClient } = require('@google-cloud/secret-manager');

const projectId = 'nutribalance-project';
const getSecret = async (secretName) => {
  const client = new SecretManagerServiceClient();
  const [version] = await client.accessSecretVersion({
    name: `projects/${projectId}/secrets/${secretName}/versions/latest`,
  });
  return version.payload.data.toString();
};

// Exporting a function to initialize Firebase
exports.initializeFirebase = async () => {
  try {
    const serviceAccountString = await getSecret('service-account-key');
    const serviceAccount = JSON.parse(serviceAccountString);

    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount),
    });

    console.log('Firebase initialized successfully');
  } catch (error) {
    console.error('Error initializing Firebase:', error);
    throw new Error('Failed to initialize Firebase');
  }
};

const firestore = admin.firestore();

exports.addNutritionData = async (nutritionData) => {
  try {
    const result = await firestore.collection('nutritions').add(nutritionData);
    return result.id;
  } catch (error) {
    console.error('Error adding nutrition data:', error);
    throw new Error('Gagal menambahkan data nutrisi makanan ke Firestore');
  }
};

exports.getAllNutritions = async () => {
  try {
    const snapshot = await firestore.collection('nutritions').get();
    const nutritions = [];
    snapshot.forEach((doc) => {
      nutritions.push(doc.data());
    });
    return nutritions;
  } catch (error) {
    console.error(error);
    throw new Error('Gagal mendapatkan data nutrisi makanan dari Firestore');
  }
};

exports.getNutritionsByName = async (foodName) => {
  try {
    const snapshot = await firestore
      .collection('nutritions')
      .where('foodName', '>=', foodName)
      .where('foodName', '<=', `${foodName}\uf8ff`)
      .get();

    const nutritions = [];
    snapshot.forEach((doc) => {
      const nutritionData = doc.data();
      nutritionData.id = doc.id;
      nutritions.push(nutritionData);
    });

    return nutritions;
  } catch (error) {
    console.error('Error searching nutritions by name:', error);
    throw new Error('Failed to search nutrition data from Firestore');
  }
};

exports.getNutritionById = async (nutritionId) => {
  try {
    const snapshot = await firestore.collection('nutritions').doc(nutritionId).get();
    if (snapshot.exists) {
      return snapshot.data();
    }
    return null;
  } catch (error) {
    console.error('Error getting nutrition data by ID:', error);
    throw new Error('Failed to get nutrition data by ID from Firestore');
  }
};

exports.updateNutritionData = async (nutritionId, updatedData) => {
  try {
    await firestore.collection('nutritions').doc(nutritionId).update(updatedData);
  } catch (error) {
    console.error('Error updating nutrition data:', error);
    throw new Error('Gagal memperbarui data nutrisi makanan di Firestore');
  }
};

exports.deleteNutritionData = async (nutritionId) => {
  try {
    await firestore.collection('nutritions').doc(nutritionId).delete();
  } catch (error) {
    console.error('Error deleting nutrition data:', error);
    throw new Error('Gagal menghapus data nutrisi makanan di Firestore');
  }
};
