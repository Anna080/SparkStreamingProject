import collections
from flask import Flask, render_template, jsonify
import pandas as pd
import sqlite3
import time
import pickle
import numpy as np
from collections import deque

app = Flask(__name__)

# Load the model and scaler from the files
with open('model.pkl', 'rb') as f:
    model = pickle.load(f)

with open('scaler.pkl', 'rb') as f:
    scaler = pickle.load(f)

# Buffer to store the last 'n' data points
n = 14
buffer = deque(maxlen=n)

# List to store the last 'm' errors for anomaly detection
m = 100
errors = deque(maxlen=m)

@app.route('/daily_total', methods=['GET'])
def get_daily_total():
    conn = sqlite3.connect('/home/nadim/data.db')
    cursor = conn.cursor()

    # Query to get the total power consumption for each day
    cursor.execute("SELECT * FROM power_data ORDER BY Date DESC, Time DESC LIMIT 1")

    rows = cursor.fetchall()
    conn.close()

    # Convert the rows to a list of dictionaries
    data = [{'date': row[0], 'total_power': row[2]} for row in rows]

    return jsonify(data)


@app.route('/')
def index():
    return render_template('index.html')

@app.route('/data', methods=['GET'])
def get_data():
    conn = sqlite3.connect('/Users/annadiaw/Desktop/ProjetSparkStreaming/data.db')
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM power_data ORDER BY Date DESC, Time DESC LIMIT 1")
    row = cursor.fetchone()
    conn.close()

    if row is None:
        print("No data fetched from the database.")
        return jsonify({})

    # Convert query to an object of key-value pairs
    d = collections.OrderedDict()
    d['date'] = pd.to_datetime(row[0] + ' ' + row[1], format='%d/%m/%Y %H:%M:%S').isoformat()
    d['global_active_power'] = row[2]

    print(f"Fetched data: {d}")

    # Add the new data point to the buffer
    buffer.append(d['global_active_power'])

    # If the buffer is not full, simply return the current data
    if len(buffer) < n:
        return jsonify(d)

    # If the buffer is full, make a prediction
    x = np.array(list(buffer)).reshape(-1, n)
    x_scaled = scaler.transform(x)
    x_scaled = np.reshape(x_scaled, (1, 1, n))  # Changed the shape to match what the model expects

    # Make a prediction
    prediction = model.predict(x_scaled)
    prediction_rescaled = scaler.inverse_transform(prediction)

    # Add the prediction to the dictionary
    d['predicted_power'] = prediction_rescaled[0, 0].item()

    # Calculate the error and add it to the list of errors
    error = abs(d['global_active_power'] - d['predicted_power'])
    errors.append(error)

    # If we have enough errors, calculate the mean and standard deviation
    if len(errors) == m:
        mean_error = np.mean(errors)
        std_error = np.std(errors)

        # Define an anomaly as any point where the error is more than 3 standard deviations from the mean
        anomaly_threshold = mean_error + 3*std_error
        d['is_anomaly'] = bool(error > anomaly_threshold)

    return jsonify(d)


if __name__ == '__main__':
    app.run(debug=True)