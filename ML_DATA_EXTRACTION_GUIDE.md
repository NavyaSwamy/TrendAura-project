# 🤖 ML/DL DATA EXTRACTION GUIDE - For Recommendation Engine

This guide shows how to extract and prepare data from TrendAura for your Deep Learning recommendation model.

---

## 1️⃣ Connect to Database

```python
import pandas as pd
import mysql.connector
from datetime import datetime, timedelta
import json

# Database connection
conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="your_mysql_password",
    database="trendaura"
)
cursor = conn.cursor(dictionary=True)
```

---

## 2️⃣ Extract User Behavior Data

### Get All User Activities

```python
# Fetch all activities
query = """
SELECT 
    id,
    user_id,
    page_name,
    action_name,
    event_time,
    session_id,
    additional_data,
    ip_address
FROM user_activity_logs
ORDER BY event_time DESC
"""

cursor.execute(query)
all_activities = cursor.fetchall()

# Convert to DataFrame for easy manipulation
df_activities = pd.DataFrame(all_activities)

# Parse JSON in additional_data column
df_activities['parsed_data'] = df_activities['additional_data'].apply(
    lambda x: json.loads(x) if x else {}
)

print(f"Total activities: {len(df_activities)}")
print(df_activities.head())
```

---

## 3️⃣ Extract Product Views (For Content-Based Filtering)

```python
# Get all product views
query = """
SELECT 
    user_id,
    JSON_EXTRACT(additional_data, '$.productId') as product_id,
    JSON_EXTRACT(additional_data, '$.productName') as product_name,
    JSON_EXTRACT(additional_data, '$.category') as category,
    JSON_EXTRACT(additional_data, '$.price') as price,
    event_time
FROM user_activity_logs
WHERE action_name = 'VIEW_PRODUCT'
ORDER BY user_id, event_time DESC
"""

cursor.execute(query)
product_views = cursor.fetchall()
df_views = pd.DataFrame(product_views)

print("Sample product views:")
print(df_views.head(10))

# Save for feature engineering
df_views.to_csv('product_views.csv', index=False)
```

---

## 4️⃣ Extract Purchase History (For Collaborative Filtering)

```python
# Get user purchase behavior
query = """
SELECT 
    user_id,
    JSON_EXTRACT(additional_data, '$.productId') as product_id,
    JSON_EXTRACT(additional_data, '$.productName') as product_name,
    JSON_EXTRACT(additional_data, '$.orderTotal') as order_value,
    action_name as action,
    event_time
FROM user_activity_logs
WHERE action_name IN ('ORDER_PLACED', 'ADD_TO_CART', 'ADD_TO_WISHLIST')
AND user_id IS NOT NULL
ORDER BY user_id, event_time DESC
"""

cursor.execute(query)
purchases = cursor.fetchall()
df_purchases = pd.DataFrame(purchases)

print("User purchase history sample:")
print(df_purchases.head(10))

df_purchases.to_csv('user_purchases.csv', index=False)
```

---

## 5️⃣ Extract Search Behavior

```python
# Get user search queries
query = """
SELECT 
    user_id,
    JSON_EXTRACT(additional_data, '$.searchQuery') as search_query,
    JSON_EXTRACT(additional_data, '$.resultsCount') as results_count,
    event_time
FROM user_activity_logs
WHERE action_name = 'SEARCH'
ORDER BY event_time DESC
"""

cursor.execute(query)
searches = cursor.fetchall()
df_searches = pd.DataFrame(searches)

print("Popular searches:")
print(df_searches['search_query'].value_counts().head(20))

df_searches.to_csv('user_searches.csv', index=False)
```

---

## 6️⃣ Extract Engagement Metrics

```python
# Get engagement per user
query = """
SELECT 
    user_id,
    COUNT(CASE WHEN action_name = 'PAGE_VIEW' THEN 1 END) as page_views,
    COUNT(CASE WHEN action_name = 'CLICK' THEN 1 END) as clicks,
    COUNT(CASE WHEN action_name = 'SCROLL' THEN 1 END) as scrolls,
    COUNT(CASE WHEN action_name = 'VIEW_PRODUCT' THEN 1 END) as product_views,
    COUNT(CASE WHEN action_name = 'ADD_TO_CART' THEN 1 END) as cart_adds,
    COUNT(CASE WHEN action_name = 'ADD_TO_WISHLIST' THEN 1 END) as wishlist_adds,
    COUNT(CASE WHEN action_name = 'ORDER_PLACED' THEN 1 END) as purchases,
    MAX(event_time) as last_activity
FROM user_activity_logs
WHERE user_id IS NOT NULL
GROUP BY user_id
ORDER BY page_views DESC
"""

cursor.execute(query)
engagement = cursor.fetchall()
df_engagement = pd.DataFrame(engagement)

print("User engagement metrics:")
print(df_engagement.describe())

df_engagement.to_csv('user_engagement.csv', index=False)
```

---

## 7️⃣ Extract User Preferences

```python
# Get category preferences per user
query = """
SELECT 
    user_id,
    JSON_EXTRACT(additional_data, '$.category') as category,
    COUNT(*) as view_count,
    AVG(CAST(JSON_EXTRACT(additional_data, '$.price') AS DECIMAL(10,2))) as avg_price
FROM user_activity_logs
WHERE action_name = 'VIEW_PRODUCT'
AND user_id IS NOT NULL
GROUP BY user_id, category
ORDER BY user_id, view_count DESC
"""

cursor.execute(query)
preferences = cursor.fetchall()
df_preferences = pd.DataFrame(preferences)

print("User category preferences:")
print(df_preferences.head(20))

df_preferences.to_csv('user_preferences.csv', index=False)
```

---

## 8️⃣ Extract Product Popularity

```python
# Get most viewed/purchased products
query = """
SELECT 
    JSON_EXTRACT(additional_data, '$.productId') as product_id,
    JSON_EXTRACT(additional_data, '$.productName') as product_name,
    JSON_EXTRACT(additional_data, '$.category') as category,
    JSON_EXTRACT(additional_data, '$.price') as price,
    COUNT(CASE WHEN action_name = 'VIEW_PRODUCT' THEN 1 END) as view_count,
    COUNT(CASE WHEN action_name = 'ADD_TO_CART' THEN 1 END) as cart_count,
    COUNT(CASE WHEN action_name = 'ADD_TO_WISHLIST' THEN 1 END) as wishlist_count,
    COUNT(CASE WHEN action_name = 'ORDER_PLACED' THEN 1 END) as purchase_count
FROM user_activity_logs
WHERE action_name IN ('VIEW_PRODUCT', 'ADD_TO_CART', 'ADD_TO_WISHLIST', 'ORDER_PLACED')
GROUP BY product_id
ORDER BY view_count DESC
LIMIT 100
"""

cursor.execute(query)
popular = cursor.fetchall()
df_popular = pd.DataFrame(popular)

print("Top 20 most viewed products:")
print(df_popular.head(20))

df_popular.to_csv('popular_products.csv', index=False)
```

---

## 9️⃣ Extract Click-Through Rate (CTR)

```python
# Get conversion funnel
query = """
SELECT 
    user_id,
    JSON_EXTRACT(additional_data, '$.productId') as product_id,
    MAX(CASE WHEN action_name = 'VIEW_PRODUCT' THEN 1 ELSE 0 END) as viewed,
    MAX(CASE WHEN action_name = 'ADD_TO_CART' THEN 1 ELSE 0 END) as carted,
    MAX(CASE WHEN action_name = 'ORDER_PLACED' THEN 1 ELSE 0 END) as purchased,
    COUNT(*) as interactions
FROM user_activity_logs
WHERE user_id IS NOT NULL
AND action_name IN ('VIEW_PRODUCT', 'ADD_TO_CART', 'ORDER_PLACED')
GROUP BY user_id, product_id
"""

cursor.execute(query)
ctr_data = cursor.fetchall()
df_ctr = pd.DataFrame(ctr_data)

# Calculate conversion rates
df_ctr['view_to_cart_rate'] = df_ctr['carted'] / df_ctr['viewed'].clip(lower=1)
df_ctr['cart_to_purchase_rate'] = df_ctr['purchased'] / df_ctr['carted'].clip(lower=1)
df_ctr['overall_conversion_rate'] = df_ctr['purchased'] / df_ctr['viewed'].clip(lower=1)

print("Conversion metrics:")
print(df_ctr.describe())

df_ctr.to_csv('conversion_funnel.csv', index=False)
```

---

## 🔟 Build User-Product Matrix (For Collaborative Filtering)

```python
# Create user-product interaction matrix
user_product_matrix = pd.read_csv('user_purchases.csv')

# Create interaction scores (weighted by action type)
interaction_weights = {
    'VIEW_PRODUCT': 1,
    'ADD_TO_CART': 3,
    'ADD_TO_WISHLIST': 2,
    'ORDER_PLACED': 5
}

user_product_matrix['interaction_score'] = user_product_matrix['action'].map(interaction_weights)

# Pivot table: Users x Products
matrix = user_product_matrix.pivot_table(
    index='user_id',
    columns='product_id',
    values='interaction_score',
    fill_value=0
)

print(f"User-Product Matrix shape: {matrix.shape}")
print("\nUser-Product Interaction Matrix:")
print(matrix.head())

matrix.to_csv('user_product_matrix.csv')

# Save as numpy for DL models
import numpy as np
matrix_array = matrix.values
np.save('user_product_matrix.npy', matrix_array)
```

---

## Build Feature Set for ML Model

```python
# Combined feature engineering
def create_ml_features():
    # Load all data
    df_engagement = pd.read_csv('user_engagement.csv')
    df_preferences = pd.read_csv('user_preferences.csv')
    df_ctr = pd.read_csv('conversion_funnel.csv')
    
    # Merge user features
    features = df_engagement.copy()
    
    # Add category preference features
    for category in df_preferences['category'].unique():
        cat_data = df_preferences[df_preferences['category'] == category]
        features[f'views_{category}'] = features['user_id'].map(
            cat_data.set_index('user_id')['view_count']
        ).fillna(0)
    
    # Add conversion features
    conversion_by_user = df_ctr.groupby('user_id')[['overall_conversion_rate']].mean()
    features['avg_conversion_rate'] = features['user_id'].map(
        conversion_by_user['overall_conversion_rate']
    ).fillna(0)
    
    # Calculate engagement score
    features['engagement_score'] = (
        (features['page_views'] / features['page_views'].max()) * 0.2 +
        (features['product_views'] / features['product_views'].max()) * 0.3 +
        (features['purchases'] / (features['purchases'].max() + 1)) * 0.5
    )
    
    print("ML Features created:")
    print(features.describe())
    
    features.to_csv('ml_features.csv', index=False)
    return features

features = create_ml_features()
```

---

## Sample DL Model Using Features

```python
import tensorflow as tf
from sklearn.preprocessing import StandardScaler

# Load features
features = pd.read_csv('ml_features.csv')
matrix = np.load('user_product_matrix.npy')

# Normalize features
scaler = StandardScaler()
features_scaled = scaler.fit_transform(features.drop('user_id', axis=1))

# Build simple neural network for recommendations
model = tf.keras.Sequential([
    tf.keras.layers.Dense(128, activation='relu', input_shape=(features_scaled.shape[1],)),
    tf.keras.layers.Dropout(0.3),
    tf.keras.layers.Dense(64, activation='relu'),
    tf.keras.layers.Dropout(0.3),
    tf.keras.layers.Dense(32, activation='relu'),
    tf.keras.layers.Dense(matrix.shape[1], activation='sigmoid')  # Predict product interactions
])

model.compile(
    optimizer='adam',
    loss='binary_crossentropy',
    metrics=['accuracy', 'mse']
)

# Train model
model.fit(
    features_scaled,
    matrix,
    epochs=50,
    batch_size=32,
    validation_split=0.2,
    verbose=1
)

# Make predictions
predictions = model.predict(features_scaled)
print("Predictions shape:", predictions.shape)

# Save model
model.save('recommendation_model.h5')
```

---

## Query All Data at Once (Fastest)

```python
# Export entire activity log for external ML processing
query = """
SELECT 
    id,
    user_id,
    page_name,
    action_name,
    event_time,
    session_id,
    additional_data
FROM user_activity_logs
ORDER BY event_time DESC
"""

df = pd.read_sql(query, conn)
df.to_csv('complete_trendaura_activities.csv', index=False)
print(f"Exported {len(df)} activity records")

# Close connection
cursor.close()
conn.close()
```

---

## Database Optimization for ML (Add These Indexes)

```sql
-- Speed up common ML queries
CREATE INDEX idx_user_action ON user_activity_logs(user_id, action_name);
CREATE INDEX idx_product_id ON user_activity_logs(JSON_EXTRACT(additional_data, '$.productId'));
CREATE INDEX idx_event_time ON user_activity_logs(event_time);
CREATE INDEX idx_page_action ON user_activity_logs(page_name, action_name);
```

---

## Import Data in Python for Analysis

```python
# Quick import of all CSVs
import os
import glob

data_dir = './extracted_data/'
os.makedirs(data_dir, exist_ok=True)

# Import all CSVs into a dictionary
dataframes = {}
for csv_file in glob.glob(data_dir + '*.csv'):
    name = os.path.basename(csv_file).replace('.csv', '')
    dataframes[name] = pd.read_csv(csv_file)
    print(f"Loaded {name}: {dataframes[name].shape}")
```

---

## Recommended ML Algorithms

Based on extracted data:

1. **Collaborative Filtering**: User-Product Matrix
2. **Content-Based Filtering**: Product categories & features
3. **Hybrid**: Combine both approaches
4. **Sequential Models (LSTM)**: User action sequences
5. **Graph Neural Networks**: Product relationships
6. **Deep Learning (DNN)**: All features combined

---

## Next Steps

1. ✅ Run tracking on all pages
2. ✅ Let users interact for 1-2 weeks
3. ✅ Export data using queries above
4. ✅ Train your DL model
5. ✅ Deploy recommendation engine in `/api/recommendations`

---

## Important Notes

- All timestamps are in ISO 8601 format
- JSON columns need parsing (see examples)
- Price values need decimal conversion
- Some user_ids will be NULL (anonymous users)
- Session IDs track cross-page user journeys
- Data collected includes: behavior, engagement, conversion, preferences

**Ready to build your AI recommendation engine!** 🚀
