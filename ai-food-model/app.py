import mysql.connector
from flask import Flask, request, jsonify
from flask_cors import CORS
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input, decode_predictions
from tensorflow.keras.preprocessing import image
import numpy as np
import os

app = Flask(__name__)
CORS(app)

# MobileNetV2 pretrained on ImageNet — works out of the box for fruits & vegetables
model = MobileNetV2(weights='imagenet')

# Map ImageNet class labels → your 36 food CSV names
# ImageNet label : your food_data.csv name
IMAGENET_TO_FOOD = {
    # Fruits
    'Granny_Smith':         'apple',
    'fig':                  'apple',
    'lemon':                'lemon',
    'orange':               'orange',
    'banana':               'banana',
    'pineapple':            'pineapple',
    'strawberry':           'grapes',
    'grape':                'grapes',
    'mango':                'mango',
    'pomegranate':          'pomegranate',
    'watermelon':           'watermelon',
    'cucumber':             'cucumber',
    'zucchini':             'cucumber',
    'acorn':                'pear',
    'pear':                 'pear',
    'kiwi':                 'kiwi',

    # Vegetables
    'head_cabbage':         'cabbage',
    'broccoli':             'cauliflower',
    'cauliflower':          'cauliflower',
    'bell_pepper':          'bell pepper',
    'hot_pot':              'chilli pepper',
    'jalapeño':             'jalepeno',
    'chili_pepper':         'chilli pepper',
    'corn':                 'corn',
    'ear':                  'corn',
    'artichoke':            'beetroot',
    'cardoon':              'beetroot',
    'mushroom':             'potato',
    'earthstar':            'potato',
    'rapeseed':             'spinach',
    'pot':                  'potato',
    'sweet_potato':         'sweetpotato',
    'butternut_squash':     'sweetpotato',
    'acorn_squash':         'sweetpotato',
    'spaghetti_squash':     'sweetpotato',
    'eggplant':             'eggplant',
    'carrot':               'carrot',
    'garlic':               'garlic',
    'ginger':               'ginger',
    'onion':                'onion',
    'leek':                 'onion',
    'green_onion':          'onion',
    'lettuce':              'lettuce',
    'spinach':              'spinach',
    'tomato':               'tomato',
    'radish':               'raddish',
    'turnip':               'turnip',
    'pea':                  'peas',
    'soybean':              'soy beans',
    'paprika':              'paprika',
    'capsicum':             'capsicum',
}

# Fallback keyword scan on ImageNet label
KEYWORD_MAP = [
    ('apple',       'apple'),
    ('lemon',       'lemon'),
    ('orange',      'orange'),
    ('banana',      'banana'),
    ('pineapple',   'pineapple'),
    ('grape',       'grapes'),
    ('mango',       'mango'),
    ('pomegranate', 'pomegranate'),
    ('watermelon',  'watermelon'),
    ('cucumber',    'cucumber'),
    ('pear',        'pear'),
    ('kiwi',        'kiwi'),
    ('cabbage',     'cabbage'),
    ('broccoli',    'cauliflower'),
    ('cauliflower', 'cauliflower'),
    ('pepper',      'bell pepper'),
    ('chili',       'chilli pepper'),
    ('corn',        'corn'),
    ('eggplant',    'eggplant'),
    ('carrot',      'carrot'),
    ('garlic',      'garlic'),
    ('ginger',      'ginger'),
    ('onion',       'onion'),
    ('lettuce',     'lettuce'),
    ('spinach',     'spinach'),
    ('tomato',      'tomato'),
    ('radish',      'raddish'),
    ('turnip',      'turnip'),
    ('pea',         'peas'),
    ('soy',         'soy beans'),
    ('paprika',     'paprika'),
    ('potato',      'potato'),
    ('sweet_potato','sweetpotato'),
    ('beetroot',    'beetroot'),
    ('beet',        'beetroot'),
    ('capsicum',    'capsicum'),
]

def map_to_food(imagenet_label):
    # Direct map
    if imagenet_label in IMAGENET_TO_FOOD:
        return IMAGENET_TO_FOOD[imagenet_label]
    # Keyword scan
    label_lower = imagenet_label.lower().replace('_', ' ')
    for keyword, food in KEYWORD_MAP:
        if keyword in label_lower:
            return food
    return None

def predict_food(img_path):
    img = image.load_img(img_path, target_size=(224, 224))
    arr = image.img_to_array(img)
    arr = np.expand_dims(arr, axis=0)
    arr = preprocess_input(arr)

    preds = model.predict(arr, verbose=0)
    top = decode_predictions(preds, top=10)[0]

    print("Top ImageNet predictions:")
    for _, label, conf in top:
        print(f"  {label}: {conf:.4f}")

    # Try to map top predictions to our food list
    for _, label, conf in top:
        food = map_to_food(label)
        if food:
            # Reject if confidence is too low — unreliable prediction
            if conf < 0.05:
                print(f"Low confidence ({round(conf*100,2)}%) — returning unknown")
                return "unknown", round(float(conf) * 100, 2)
            print(f"Mapped '{label}' -> '{food}' ({round(conf*100,2)}%)")
            return food, round(float(conf) * 100, 2)

    # No food match found
    _, label, conf = top[0]
    print(f"No food match. Top was: {label} ({round(conf*100,2)}%)")
    return "unknown", round(float(conf) * 100, 2)

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({"error": "No image field"}), 400

    file = request.files['image']
    os.makedirs("uploads", exist_ok=True)
    filepath = os.path.join("uploads", file.filename or "upload.jpg")
    file.save(filepath)

    food, confidence = predict_food(filepath)
    print(f"Final: {food} ({confidence}%)")

    return jsonify({"food": food, "confidence": confidence})

@app.route('/get_food', methods=['POST'])
def get_food():
    data = request.get_json()
    food_name = data.get("food", "").strip()
    if not food_name:
        return jsonify({"status": "error", "message": "Food name is required"}), 400

    try:
        connection = mysql.connector.connect(
            host="localhost",
            user="root",
            password="kavyaskk@2005",
            database="food_scanner"
        )
        cursor = connection.cursor(dictionary=True)
        cursor.execute(
            "SELECT * FROM food_report WHERE LOWER(food) = LOWER(%s)",
            (food_name,)
        )
        result = cursor.fetchone()
        cursor.close()
        connection.close()

        if result:
            return jsonify({"status": "found", "data": result})
        else:
            return jsonify({"status": "not_found", "message": "Food not found in database"}), 404

    except mysql.connector.Error as e:
        return jsonify({"status": "error", "message": str(e)}), 500


if __name__ == '__main__':
    app.run(debug=False, port=5000)
