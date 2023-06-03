# MovieMatch

This project implements a movie recommender system using Spark, C#, React and Docker. 

It combines user-based collaborative filtering and content-based filtering techniques to provide personalized movie recommendations based on user preferences and movie features.

## Prerequisites

- Docker

- Apache Spark (version 3.4.0)
- C# (compatible version with Spark)

## Setup

1. Install Docker: [Download Docker](https://www.docker.com/products/docker-desktop) and follow the installation instructions.

2. Clone the repository:

`git clone https://github.com/RyanRasi/MovieMatch`

2. Download the dataset [The Movies Dataset](https://www.kaggle.com/datasets/rounakbanik/the-movies-dataset) and place the two required files within the `/assets` folder. The two files are `movies_metadata.csv` and `ratings.csv`, both totalling 744 MB.

3. Build the Docker image: Open a terminal, navigate to your project directory (where the Dockerfile is located), and run the following command to build the Docker image:

`docker build -t movie-matcher .`

This command builds the Docker image using the Dockerfile in the current directory and tags it with the name `movie-matcher`.

4. Run the Docker container: After the image is built, you can run a container based on that image using the following command:

`docker run -it movie-matcher`

This command starts a container using the `movie-matcher` image and gives you an interactive terminal within the container.

5. Interact with the Spark application: Once the container is running, you can interact with the Spark application just like you would in a regular Spark environment. The code will execute within the container, utilizing the Spark installation inside.

6. Clean up: When you're finished using the container, you can stop and remove it using the following commands:

`docker stop <container_id>`

`docker rm <container_id>`

Replace `<container_id>` with the actual ID or name of the container that was created.

## Usage

1. User selects three movies: Update the `selectedMovies` array in the code with the movie IDs of the three movies selected by the user.

2. Generate movie recommendations: The application will generate movie recommendations based on the user-based collaborative filtering and content-based filtering models.

3. View the recommendations: The recommended movies will be displayed in the console.

4. Customize the recommendations: You can modify the number of recommendations generated, the parameters for the collaborative filtering model, or the content-based filtering model as per your requirements.

5. Stop the application: Terminate the Spark application when you no longer need the recommendations.

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- [Docker](https://www.docker.com/)
- [Apache Spark](https://spark.apache.org/)
- [React](https://react.dev/)
- [.Net7.0](https://dotnet.microsoft.com/)
- [Kaggle The Movies Dataset](https://www.kaggle.com/datasets/rounakbanik/the-movies-dataset)