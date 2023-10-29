import pandas as pd
from surprise import Reader, Dataset, KNNBasic, accuracy
from surprise.model_selection import train_test_split
# User Based Recommender
# Finds Similar Users

from sklearn.preprocessing import LabelEncoder
from IPython.display import SVG, display

# Load MovieLens data
items = pd.read_csv('./data/movielens/ml-100k/u.item', header=None, sep="|", encoding='latin-1')
items.columns = ['movie id', 'movie title', 'release date', 'video release date',
                  'IMDb URL', 'unknown', 'Action', 'Adventure', 'Animation',
                  'Childrens', 'Comedy', 'Crime', 'Documentary', 'Drama', 'Fantasy',
                  'Film_Noir', 'Horror', 'Musical', 'Mystery', 'Romance', 'Sci_Fi',
                  'Thriller', 'War', 'Western']

data = pd.read_csv('./data/movielens/ml-100k/u.data', header=None, sep='\t')
data.columns = ['user id', 'movie id', 'rating', 'timestamp']

users = pd.read_csv('./data/movielens/ml-100k/u.user', header=None, sep='|')
users.columns = ['user id', 'age', 'gender', 'occupation', 'zip code']

# Encode categorical variables
label_encoder = LabelEncoder()
users['gender'] = label_encoder.fit_transform(users['gender'])
users['occupation'] = label_encoder.fit_transform(users['occupation'])

# Merge user demographic information
data = data.merge(users, on='user id')

# Create Surprise dataset
reader = Reader(rating_scale=(1, 5))
surprise_data = Dataset.load_from_df(data[['user id', 'movie id', 'rating']], reader)

# Split the data for training and testing
trainset, testset = train_test_split(surprise_data, test_size=0.2, random_state=42)

# Use user-based collaborative filtering with kNN
sim_options = {
    'name': 'cosine',  # Use cosine similarity
    'user_based': True  # Use user-based collaborative filtering
}

model = KNNBasic(sim_options=sim_options)
model.fit(trainset)

# Testing user-based Recommendation with demographic information
def rec_user_demographic(user_id):
    user_ratings = data[data['user id'] == user_id][['movie id', 'rating']]
    user_unrated_movies = set(items['movie id']) - set(user_ratings['movie id'])
    
    predictions = []
    for movie_id in user_unrated_movies:
        prediction = model.predict(user_id, movie_id)
        predictions.append({'movie id': movie_id, 'predicted rating': prediction.est})
    
    recommendations = pd.DataFrame(predictions).sort_values(by='predicted rating', ascending=False).head(10)
    recommendations = recommendations.merge(items, on='movie id')
    
    return recommendations[['movie id', 'movie title', 'predicted rating']]

# Display user-based recommendations for a user (e.g., user 1)
display(rec_user_demographic(771))
