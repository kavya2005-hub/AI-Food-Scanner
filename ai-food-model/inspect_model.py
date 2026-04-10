from tensorflow.keras.models import load_model
import os

MODEL_PATH = os.path.join('..', 'springboot-app', 'my_model.h5')
model = load_model(MODEL_PATH)

print("=== Model Summary ===")
model.summary()
print("\n=== Input Shape ===", model.input_shape)
print("=== Output Shape ===", model.output_shape)
