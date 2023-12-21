# Nutribalance

# Cloud Computing
## Project Structure
- Controller: Contains files that handle the business logic for each entity or feature in the application.
- Routes: Contains files that define API routes and link them to controller functions.
- Services: Contains files that serve as additional services, such as integration with Cloud Storage, Cloud Vision API, or database services.
- app.js: Contains initialization of the Hapi server, registration of API routes, and other configurations
- package.json: Node.js configuration file and project dependency list.
- tools: Node.js, Hapi framework, Eslint Style, Module Joi, Paket dotenv
- Database & Data Storage: Utilizes Firebase, Firestore, and Cloud Storage for data storage.

## Server Information
The server is running on http://localhost:3000

## API Documentation

### POST Requests:
Add Nutrition Information with valid data:
URL: http://localhost:3000/nutritions
Body:
`{
  "foodName": "Bakwan Goreng",
  "calories": 137,
  "protein": 1.99,
  "carbohydrates": 6.74,
  "fat": 11.5
}`

Add Nutrition Information with Invalid food name"
URL: http://localhost:3000/nutritions
Body:
`{
  "foodName": "12345",
  "calories": 200,
  "protein": 30,
  "carbohydrates": 50,
  "fat": 30
}`

Add Nutrition Information without name:
URL: http://localhost:3000/nutritions
Body:
`{
  "foodName": "",
  "calories": 200,
  "protein": 30,
  "carbohydrates": 10,
  "fat": 20
}`

Add Nutrition Information without Data Nutrition:
URL: http://localhost:3000/nutritions
Body:
`{
  "foodName": "Soto",
  "protein": 30,
  "carbohydrates": 50,
  "fat": 30
}`


### GET Requests:
Get All Nutrition: 
URL: http://localhost:3000/nutritions

Get Nutrition Information with valid Name: 
URL: http://localhost:3000/nutritions/search?foodName=Nasi%20Goreng

Get Nutrition Information with Invalid Name:
URL: http://localhost:3000/nutritions/search?foodName=xxxxx

Get Nutrition Information with ID:
URL: http://localhost:3000/nutritions/TUbrlynbGEQeaRjPFvx2

Get Nutrition Information with invalid ID:
URL: http://localhost:3000/nutritions/xxxxx

### PUT Requests:
Update Nutrition Information with Valid ID:
URL: http://localhost:3000/nutritions/TUbrlynbGEQeaRjPFvx2

Update Nutrition Information with Invalid ID:
URL: http://localhost:3000/nutritions/xxxxx

### DELETE Requests:
Delete Nutrition with Valid ID:
URL: http://localhost:3000/nutritions/XkmTQIMQRmKveJubAGrH

Delete Nutrition with Invalid ID:
URL: http://localhost:3000/nutritions/xxxxx


## Image Storage on Cloud Storage
A Cloud Storage bucket has been created for uploading food image files. The goal is to access images publicly. The image URL from Cloud Storage will be added to the API to display food images when responding to user requests.