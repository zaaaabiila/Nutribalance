async function getFoodInfo() {
  try {
    const simulatedResponse = [
      {
        calories: 150,
        protein: 10,
        fat: 5,
        carbohydrates: 20,
      },
      {
        calories: 200,
        protein: 15,
        fat: 10,
        carbohydrates: 13,
      },
    ];
    return simulatedResponse;
  } catch (error) {
    console.error('Error fetching Dietagram API:', error.message);
    throw error;
  }
}

module.exports = { getFoodInfo };
