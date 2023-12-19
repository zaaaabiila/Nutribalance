const { Storage } = require('@google-cloud/storage');

const storage = new Storage();

async function uploadImageToGCS(imageBuffer, filename) {
  const bucketName = 'images-foodnutrition';
  const bucket = storage.bucket(bucketName);
  const file = bucket.file(filename);

  await file.save(imageBuffer, {
    metadata: {
      contentType: 'image/png',
    },
  });

  const imageUrl = `https://storage.googleapis.com/${bucketName}/${filename}`;
  return imageUrl;
}

module.exports = {
  uploadImageToGCS,
};
