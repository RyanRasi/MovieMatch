# User Based Recommender
# Finds Similar Users

import numpy as np # linear algebra
import pandas as pd # data processing, CSV file I/O (e.g. pd.read_csv)
import os
import ast
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import linear_kernel
from sklearn.metrics import pairwise_distances
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.model_selection import train_test_split

from scipy.spatial.distance import cosine, correlation
from surprise import Reader, Dataset, SVD, NormalPredictor, BaselineOnly, KNNBasic, NMF
from surprise.model_selection import cross_validate, KFold ,GridSearchCV , RandomizedSearchCV

from keras.models import Sequential
from keras.callbacks import ReduceLROnPlateau, EarlyStopping
from keras.layers import  Input, dot, concatenate
from keras.models import Model
from IPython.display import SVG, display
from keras.utils.vis_utils import model_to_dot
from keras.layers import Activation, Dense, Dropout, Embedding, Flatten, Conv1D, MaxPooling1D, LSTM

import gc
import matplotlib.pyplot as plt
import seaborn as sns
from datetime import datetime

meta_data = pd.read_csv('../data/moviesdataset/movies_metadata.csv')
keywords = pd.read_csv('../data/moviesdataset/keywords.csv')
credits = pd.read_csv('../data/moviesdataset/credits.csv')

meta_data = meta_data[meta_data.id!='1997-08-20']
meta_data = meta_data[meta_data.id!='2012-09-29']
meta_data = meta_data[meta_data.id!='2014-01-01']
meta_data = meta_data.astype({'id':'int64'})

meta_data = meta_data.merge(keywords , on = 'id')
meta_data = meta_data.merge(credits , on = 'id')

meta_data[meta_data['production_companies'].isnull()]
meta_data.dropna(subset=['production_companies'] , inplace = True)

def btc_function(data):
    if type(data) == str:
        return ast.literal_eval(data)['name'].replace(" ","")
    return data
# https://www.kaggle.com/hadasik/movies-analysis-visualization-newbie
def get_values(data_str):
    if isinstance(data_str, float):
        pass
    else:
        values = []
        data_str = ast.literal_eval(data_str)
        if isinstance(data_str, list):
            for k_v in data_str:
                values.append(k_v['name'].replace(" ",""))
            return str(values)[1:-1]
        else:
            return None

meta_data['btc_name'] = meta_data.belongs_to_collection.apply(btc_function)
meta_data[['genres', 'production_companies', 'production_countries', 'spoken_languages' ,'keywords','cast', 'crew']] = meta_data[['genres', 'production_companies', 'production_countries', 'spoken_languages' ,'keywords' ,'cast' , 'crew']].applymap(get_values)
meta_data['is_homepage'] = meta_data['homepage'].isnull()

meta_data['status'] = meta_data['status'].fillna('')
meta_data['original_language'] = meta_data['original_language'].fillna('')
meta_data['btc_name'] = meta_data['btc_name'].fillna('')

def vector_values(df , columns , min_df_value):
    c_vector = CountVectorizer(min_df = min_df_value)
    df_1 = pd.DataFrame(index = df.index)
    for col in columns:
        #print(col)
        df_1 = df_1.join(pd.DataFrame(c_vector.fit_transform(df[col]).toarray(),columns =c_vector.get_feature_names_out(),index= df.index).add_prefix(col+'_'))
    return df_1
meta_data_addon_1 = vector_values(meta_data , columns = ['status','original_language','genres', 'production_companies' ,'production_countries' , 'spoken_languages' , 'keywords' , 'cast' ,'crew'] ,min_df_value = 20)
meta_data_addon_2 = vector_values(meta_data , columns = ['btc_name'] , min_df_value = 2)

col = ['belongs_to_collection', 'genres' , 'homepage' , 'id' , 'imdb_id' , 'overview' ,'poster_path' , 'status' , 'original_language' , 
'production_companies', 'production_countries', 'spoken_languages', 'keywords',  'cast',  'crew', 'tagline','adult'  ]
meta_data.drop(columns = col , inplace = True)
col = [ 'video', 'is_homepage']
for c in col:
    meta_data[c] = meta_data[c].astype(bool)
    meta_data[c] = meta_data[c].astype(int)

def get_year(date):
    return str(date).split('-')[0]
meta_data['popularity'] = meta_data['popularity'].astype(float)
meta_data['budget'] = meta_data['budget'].astype(float)
meta_data['vote_average_group'] = pd.qcut(meta_data['vote_average'], q=10, precision=2,duplicates = 'drop')
meta_data['popularity_group'] = pd.qcut(meta_data['popularity'], q=10, precision=2,duplicates = 'drop')
meta_data['vote_average_group'] =pd.qcut(meta_data['vote_average'], q=10, precision=2,duplicates = 'drop')
meta_data['runtime_group'] = pd.qcut(meta_data['runtime'], q=10, precision=2,duplicates = 'drop')
meta_data['budget_group'] = pd.qcut(meta_data['budget'], q=10, precision=2,duplicates = 'drop')
meta_data['revenue_group'] = pd.qcut(meta_data['revenue'], q=10, precision=2,duplicates = 'drop')
meta_data['vote_count_group'] = pd.qcut(meta_data['vote_count'], q=10, precision=2,duplicates = 'drop')
meta_data['release_year'] = meta_data['release_date'].apply(get_year)
meta_data['release_year'] = meta_data['release_year'].fillna('')
meta_data['release_year'] = meta_data['release_year'].astype(float)
meta_data['release_year_group'] = pd.qcut(meta_data['release_year'], q=10, precision=2,duplicates = 'drop')
meta_data['title_new'] = meta_data.apply(lambda x: str(x['title'])+' ('+str(x['release_date'])+')' , axis =1)

meta_data_addon_3 = pd.get_dummies(meta_data[['vote_average_group' , 'popularity_group' , 'runtime_group' , 'budget_group' , 'revenue_group' , 'vote_count_group' , 'release_year_group']])
meta_data_train = pd.concat([meta_data_addon_1,meta_data_addon_2,meta_data_addon_3 , meta_data[['video' , 'is_homepage']]] , axis = 1)

meta_data_train.index = meta_data['title_new']

del meta_data_addon_1,meta_data_addon_2,meta_data_addon_3
gc.collect()

def get_similar_movies(movie_title , num_rec = 10):
    try:
        sample_1 = 1 - pairwise_distances([meta_data_train.loc[movie_title].values] , meta_data_train.values , metric = 'cosine')
        sample_1 = pd.DataFrame(sample_1.T , index = meta_data_train.index )
        return sample_1.sort_values(by = 0 , ascending  = False).head(num_rec).index
    except ValueError as e:
        print(e)

#print(get_similar_movies('Toy Story (1995-10-30)'))
#print(get_similar_movies('Heat (1995-12-15)'))
#print(get_similar_movies('Kong: Skull Island (2017-03-08)'))

def multi_rec(seen_movies):
    rec_movies = []
    for mov in seen_movies:
        rec_movies.append(get_similar_movies(mov , 5).values)
    return rec_movies
    
print(multi_rec(['Star Wars: The Clone Wars (2008-08-05)' , 'Marvel One-Shot: Item 47 (2012-09-13)']))