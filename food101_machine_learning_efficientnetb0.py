# -*- coding: utf-8 -*-
"""Food101_Machine_Learning_EfficientNetB0.ipynb

Automatically generated by Colaboratory.

Original file is located at
    https://colab.research.google.com/drive/17b3HNvhsu_8Ou-Ea1V9c7_yZrvZmrjzd

#Using EfficientNetB0 for making food 101 ML Model to scan a food

#IMPORT LIBRARIES

This is the libraries which will be needed or not to use in NutriBalance
"""

import tensorflow_datasets as tfds
import tensorflow as tf
import numpy as np
import matplotlib.pyplot as plt
import os
import pandas as pd
from tensorflow.keras import mixed_precision
from tensorflow.keras import layers
from tensorflow.keras.layers.experimental import preprocessing

# This codes are to check the version of libraries
print("The numpy version is " + np.__version__)
print("The tensorflow version is " + tf.__version__)
print("The pandas version is " + pd.__version__)

"""##DATA COLLECTING

We use TensorFlow Dataset Food101
"""

dataset_food101 = tfds.list_builders()
print("food101" in dataset_food101)

#import dataset food101 from Tensorflow Dataset
(train_data, validation_data), ds_info = tfds.load(name="food101",
                                             split=["train","validation"],
                                             shuffle_files=True,
                                             as_supervised=True,
                                             with_info=True)

print('This is the figure of train data')
figure_of_train_data = tfds.show_examples(train_data, ds_info)

print('This is the figure of validation data')
figure_of_validation_data = tfds.show_examples(validation_data, ds_info)

#This is show the features of dictionary
ds_info.features

#This is the example of class names
class_names = ds_info.features["label"].names
ten_first_classes =  class_names[:10]
ten_second_classes = class_names[10:20]
ten_third_classes = class_names[20:30]
ten_fourth_classes = class_names[30:40]
ten_fifth_classes = class_names[40:50]
ten_sixth_classes = class_names[50:60]
ten_seventh_classes = class_names[60:70]
ten_eigth_classes = class_names[70:80]
ten_ninth_classes = class_names[80:90]
ten_tenth_classes = class_names[90:100]
last_one_classes = class_names[100]
print("This is the class names")
print(ten_first_classes)
print(ten_second_classes)
print(ten_third_classes)
print(ten_fourth_classes)
print(ten_fifth_classes)
print(ten_sixth_classes)
print(ten_seventh_classes)
print(ten_eigth_classes)
print(ten_ninth_classes)
print(ten_tenth_classes)
print(last_one_classes)

print('taking one example of train data')
train_sample = train_data.take(1)
train_sample

for image, label in train_sample:
  print(f"""
  Image shape: {image.shape}
  Image dtype: {image.dtype}
  Target class: {label}
  Class name: {class_names[label.numpy()]}
  """)

#This is show the numpy array of image
image
#This is show the maximum and minimum value of image
tf.reduce_min(image), tf.reduce_max(image)

title = class_names[label.numpy()]

plt.imshow(image)
plt.title(title)
plt.axis('off')

"""#DATA PREPROCESSING AND EXPLORATORY"""

def preprocessing_image(image, label):
    image = tf.image.resize(image, size=[224, 224])
    return tf.cast(image, tf.float32), label

preprocessed_image = preprocessing_image(image, label)[0]
image_shape = image.shape
image_dtype = image.dtype
preprocessed_image_shape = preprocessed_image.shape
preprocessed_image_dtype = preprocessed_image.dtype
print(f"Image before preprocessing:\n {image[:2]}...,\nShape: {image_shape},\nDatatype: {image_dtype}\n")
print(f"Image after preprocessing:\n {preprocessed_image[:2]}...,\nShape: {preprocessed_image_shape},\nDatatype: {preprocessed_image_dtype}")

title = class_names[label]

plt.imshow(preprocessed_image/255.)
plt.title(title)
plt.axis('off')

buffer_size = 1000
batch_size = 32
autotune = tf.data.AUTOTUNE

train_data = train_data.map(map_func=preprocessing_image, num_parallel_calls=autotune)
train_data = train_data.shuffle(buffer_size=buffer_size).batch(batch_size=batch_size).prefetch(buffer_size=autotune)
validation_data = validation_data.map(map_func=preprocessing_image, num_parallel_calls=autotune)
validation_data = validation_data.shuffle(buffer_size=buffer_size).batch(batch_size=batch_size).prefetch(buffer_size=autotune)

train_data, validation_data

"""#Using Callbacks"""

class myCallback(tf.keras.callbacks.Callback):
  def on_epoch_end(self, epoch, logs={}):
    if(logs.get('accuracy')>0.75 and logs.get('val_accuracy')>0.75):
      print("\nReached more than 75% accuracy so cancelling training!")
      self.model.stop_training = True

checkpoint_path = "efficientNetB0_model_checkpoints" # save weights in .ckpt
model_checkpoint = tf.keras.callbacks.ModelCheckpoint(checkpoint_path,
                                                      monitor="accuracy",
                                                      save_best_only=True,
                                                      save_weights_only=True,
                                                      verbose=1)

"""#DATA MODELLING

##Creating Base Model
"""

import tensorflow as tf
from tensorflow.keras.applications import EfficientNetB0
from tensorflow.keras.models import Model
from tensorflow.keras.layers import Dense, GlobalAveragePooling2D
from tensorflow.keras.optimizers.legacy import Adam

def create_efficientnetB0_model(input_shape=(224, 224, 3)):
    # Load the ResNet-50 model pre-trained on ImageNet data
    base_model = EfficientNetB0(include_top=False, input_shape=input_shape)
    base_model.trainable = False
    inputs = layers.Input(shape=input_shape, name="input_layer")
    x = base_model(inputs, training=False)
    x = layers.GlobalAveragePooling2D(name="pooling_layer")(x)
    x = layers.Dense(len(class_names))(x)
    outputs = layers.Activation("softmax", dtype=tf.float32, name="softmax_float32")(x)
    model = tf.keras.Model(inputs, outputs)

    # Compile the model
    model.compile(optimizer='adam',
                  loss='sparse_categorical_crossentropy',
                  metrics=['accuracy'])

    return model

# Create the model
model = create_efficientnetB0_model()

# Display the model summary
model.summary()

"""##Feature Extraction"""

callbacks = myCallback()
history_101_food_feature_extraction = model.fit(train_data,
                                                epochs=5,
                                                steps_per_epoch=len(train_data),
                                                validation_data=validation_data,
                                                validation_steps=int(0.15*len(validation_data)),
                                                callbacks=[callbacks,
                                                           model_checkpoint])

evaluation_validation_data_of_feature_extraction_model = model.evaluate(validation_data)

evaluation_train_data_of_feature_extraction_model = model.evaluate(train_data)

evaluation_validation_data_of_feature_extraction_model

evaluation_train_data_of_feature_extraction_model

"""##Fine-Tuning Model"""

for layer in model.layers:
  layer.trainable=True
  print(layer.name, layer.trainable, layer.dtype, layer.dtype_policy)

reduce_learning_rate = tf.keras.callbacks.ReduceLROnPlateau(monitor="val_loss",
                                                 factor=0.2,
                                                 patience=5,
                                                 verbose=1,
                                                 min_lr=1e-7)
model.compile(loss="sparse_categorical_crossentropy",
              optimizer=tf.keras.optimizers.Adam(learning_rate=0.0001),
              metrics=["accuracy"])

history_101_food_fine_tuning = model.fit(train_data,
                                         epochs=5,
                                         steps_per_epoch=len(train_data),
                                         validation_data=validation_data,
                                         validation_steps=int(0.15*len(validation_data)),
                                         callbacks=[model_checkpoint,
                                                    callbacks,
                                                    reduce_learning_rate])

evaluation_validation_data_of_fine_tuning_model = model.evaluate(validation_data)

evaluation_validation_data_of_fine_tuning_model

evaluation_train_data_of_fine_tuning_model = model.evaluate(train_data)

evaluation_train_data_of_fine_tuning_model

"""##Showing the plot of model"""

def plot_metrics(history):
    # Plot training & validation accuracy values
    plt.figure(figsize=(12, 6))
    plt.subplot(1, 2, 1)
    plt.plot(history.history['accuracy'], label='Train Accuracy')
    plt.plot(history.history['val_accuracy'], label='Validation Accuracy')
    plt.title('Model accuracy')
    plt.xlabel('Epoch')
    plt.ylabel('Accuracy')
    plt.legend()

    # Plot training & validation loss values
    plt.subplot(1, 2, 2)
    plt.plot(history.history['loss'], label='Train Loss')
    plt.plot(history.history['val_loss'], label='Validation Loss')
    plt.title('Model loss')
    plt.xlabel('Epoch')
    plt.ylabel('Loss')
    plt.legend()

    plt.tight_layout()
    plt.show()

plot_metrics(history_101_food_feature_extraction)

plot_metrics(history_101_food_fine_tuning)

"""##Save and Download The Model"""

model.save('food101_model.h5')

from tensorflow.keras.models import load_model

# Step 1: Install Required Libraries
# Uncomment the following lines if you haven't installed TensorFlow and h5py
# !pip install tensorflow
# !pip install h5py

# Step 2: Load the H5 Model
food101_model = load_model('food101_model.h5')  # Replace with the path to your H5 model file

# Step 3: Convert to TensorFlow Lite Model
converter = tf.lite.TFLiteConverter.from_keras_model(food101_model)
tflite_model = converter.convert()

# Step 4: Save the TensorFlow Lite Model
with open('converted_model.tflite', 'wb') as f:
    f.write(tflite_model)

from google.colab import drive
drive.mount('/content/drive')

from google.colab import files
files.download('converted_model.tflite')

"""#Citation"""

@inproceedings{bossard14,
  title = {Food-101 -- Mining Discriminative Components with Random Forests},
  author = {Bossard, Lukas and Guillaumin, Matthieu and Van Gool, Luc},
  booktitle = {European Conference on Computer Vision},
  year = {2014}
}