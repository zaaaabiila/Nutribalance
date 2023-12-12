const admin = require('firebase-admin');

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
