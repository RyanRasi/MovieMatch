import pandas as pd
from sklearn.metrics.pairwise import pairwise_distances
from IPython.display import SVG, display

def load_data(item_path, data_path, genre_path):
    items = pd.read_csv(item_path, header=None, sep="|", encoding='latin-1')
    items.columns = ['movie id', 'movie title', 'release date', 'video release date',
                      'IMDb URL', 'unknown', 'Action', 'Adventure', 'Animation',
                      'Childrens', 'Comedy', 'Crime', 'Documentary', 'Drama', 'Fantasy',
                      'Film_Noir', 'Horror', 'Musical', 'Mystery', 'Romance', 'Sci_Fi',
                      'Thriller', 'War', 'Western']

    data = pd.read_csv(data_path, header=None, sep='\t')

    data.columns = ['user id' , 'movie id' , 'rating' , 'timestamp']

    data = clean_data(data, items)
    return data, items

def clean_data(data, items):
    # Merging the columns with data table to better visualise
    data = data.merge(items, on='movie id')

    # Drop rows with missing values
    data.dropna(subset=['release date'], inplace=True)

    # Grouping and aggregating data
    movie_details = data.groupby('movie id').agg({'rating': ['size', 'mean', 'std']}).reset_index()
    movie_details.columns = ['movie id', 'number of movie ratings', 'average of movie ratings', 'std of movie ratings']
    data = data.merge(movie_details, on='movie id')

    return data

def calculate_similarity(data):
    pivot_table_movie = pd.pivot_table(data=data, values='rating', index='user id', columns='movie id')
    pivot_table_movie = pivot_table_movie.fillna(0)

    movie_based_similarity = 1 - pairwise_distances(pivot_table_movie.T.values, metric="cosine")
    movie_based_similarity = pd.DataFrame(movie_based_similarity)
    movie_based_similarity.columns = movie_based_similarity.columns + 1
    movie_based_similarity.index = movie_based_similarity.index + 1

    return movie_based_similarity

def rec_movie(movie_based_similarity, items, movie_id):
    # Get the top 11 similar movies (including itself)
    similar_movies = movie_based_similarity[movie_id].sort_values(ascending=False).index[:11]

    # Extract relevant columns from the items DataFrame
    temp_table = items[items['movie id'].isin(similar_movies)][['movie id', 'movie title']]

    # Add a 'rating' column with the similarity score for each movie in the recommendation list
    temp_table['rating'] = movie_based_similarity.loc[movie_id, similar_movies].values
    return temp_table

def rec_movie3(movie_based_similarity, items, movie_id):
    temp_table = pd.DataFrame(columns = items.columns)
    for movie in movie_id:
        similar_movies = movie_based_similarity[movie].sort_values(ascending = False).index.tolist()[:11]
        for mov in similar_movies:
    #         display(items[items['movie id'] == mov])
            temp_table = temp_table._append(items[items['movie id'] == mov], ignore_index=True)
    # Sorting the movies based on the summed rankings
        temp_table['rating'] = movie_based_similarity.loc[movie, similar_movies].values
    temp_table = temp_table.sort_values(by='rating', ascending=False)
    # Getting the top 10 movies
    top_recommendations = temp_table.head(10)
    return top_recommendations

def rec_movie4(movie_based_similarity, items, movie_id):
    temp_table = pd.DataFrame(columns=items.columns)
    
    for movie in movie_id:
        similar_movies = movie_based_similarity[movie].sort_values(ascending=False).index.tolist()[:11]
        
        # Create a temporary DataFrame for the current movie
        temp_movie_table = pd.DataFrame(columns=items.columns)
        
        for mov in similar_movies:
            temp_movie_table = temp_movie_table._append(items[items['movie id'] == mov], ignore_index=True)
        
        # Assign ratings for the current movie
        temp_movie_table['rating'] = movie_based_similarity.loc[movie, similar_movies].values
        
        # Concatenate the temporary DataFrame to the main DataFrame
        temp_table = pd.concat([temp_table, temp_movie_table], ignore_index=True)
    
    # Exclude the original movies from the results
    temp_table = temp_table[~temp_table['movie id'].isin(movie_id)]

    # Drop duplicates
    temp_table = temp_table.drop_duplicates(subset=['movie id'])
    
    # Sorting the movies based on the summed rankings
    temp_table = temp_table.sort_values(by='rating', ascending=False)
    
    # Getting the top 10 movies
    top_recommendations = temp_table.head(10)
    return top_recommendations


def rec_movie2(movie_based_similarity, items, movie_id):
    temp_table = pd.DataFrame(columns = items.columns)
    movies = movie_based_similarity[movie_id].sort_values(ascending = False).index.tolist()[:11]
    for mov in movies:
#         display(items[items['movie id'] == mov])
        temp_table = temp_table._append(items[items['movie id'] == mov], ignore_index=True)
    return temp_table


def multi_rec(movie_based_similarity, items, seen_movies):
    rec_movies = pd.DataFrame(columns=items.columns)
    
    for mov in seen_movies:
        rec_movies = rec_movies._append(rec_movie(movie_based_similarity, items, mov), ignore_index=True)
    
    # Summing up the rankings for each movie
    rec_movies_sum = rec_movies.groupby('movie title').agg({'rating': 'sum'}).reset_index()

    # Sorting the movies based on the summed rankings
    rec_movies_sum = rec_movies_sum.sort_values(by='rating', ascending=False)

    # Getting the top 10 movies
    top_recommendations = rec_movies_sum.head(11)

    return top_recommendations

def main():
    DATA_PATH = './data/movielens/ml-100k/u.data'
    ITEM_PATH = './data/movielens/ml-100k/u.item'
    GENRE_PATH = './data/movielens/ml-100k/u.genre'

    data, items = load_data(ITEM_PATH, DATA_PATH, GENRE_PATH)
    movie_based_similarity = calculate_similarity(data)

    #display(rec_movie(movie_based_similarity, items, 176))
    print("Changed")
    display(rec_movie(movie_based_similarity, items, 50))
    print("Original")
    display(rec_movie2(movie_based_similarity, items, 50))
    print("New")
    display(rec_movie4(movie_based_similarity, items, [210,174]))
    #display(rec_movie(movie_based_similarity, items, 257))
    #display(rec_movie(movie_based_similarity, items, 273))

    display(multi_rec(movie_based_similarity, items, [210,174]))#[12, 3]))

if __name__ == "__main__":
    main()
