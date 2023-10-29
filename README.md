# MovieMatch

This project implements a movie recommender system using Hadoop, Spark, Mahout, Java (Maven), Angular and Docker. 

It combines user-based collaborative filtering and content-based filtering techniques to provide personalized movie recommendations based on user preferences and movie features.

## Components

#### Front-end
- Angular
- Bootstrap

#### Back-end
- ASP.NET Core

#### Microservice
- Java
- Hadoop
- Spark
- Mahout

## Setup

1. Install Docker: [Download Docker](https://www.docker.com/products/docker-desktop) and follow the installation instructions.

2. Clone the repository:

`git clone https://github.com/RyanRasi/MovieMatch`

3. Open two terminals

4. In one terminal cd to the backend folder. `cd backend` Then run  `mvn clean package`

5. Build then run the backend API using `docker build -t movie-match-backend` and `docker run -d -p 8080:8080 movie-match-backend:latest`

6. In the second terminal, navigate to the main project directory (where this README is located), and run the following command to build and run the Docker image: `docker build -t movie-match-frontend .` and `docker run -d -p 4200:80 movie-match-frontend:latest `

7. Alternatively if you have Docker Desktop you can manually run these dockerfiles from the GUI interface after building.

5. You can now access the frontend application here [localhost:4040](http://localhost:4040/)

6. To stop the containers run, `docker stop movie-match-frontend` and `docker stop movie-match-backend`

## Usage

1. Select a minumum of three movies using the search dropdown.

2. Click recommend: The application will generate movie recommendations based on a hybrid combination of user-based collaborative filtering and content-based filtering models.

3. View the recommendations: The recommended movies will be displayed asynchronously within the search.

## Evaluative Comments

1. The hybrid approach isn't accurate in the recommendations, however this is a limitation of the 'cold start' having no data on the user to start with. This approach revolves around assigning the user to no.999and has three movies recommended despite other users having over fifteen.

2. A significant amount of libraries are outdated with missing dependencies leading to Java 11 being the latest supported project version.

3. Other languages offer better implementations of the Hadoop and Spark libraries allowing for rich results to be obtained. The main reason why I chose this software package of Java, Spark, Hadoop & Mahout was due to this combination being state-of-the-art a few years ago. If I had to do this project again, I would pick Python and Spark with tensorflow libraries to supplement.

## Support and Donations

If you find this project useful and would like to support its development, consider making a donation.

[!["Buy Me A Coffee"](https://www.buymeacoffee.com/assets/img/custom_images/orange_img.png)](https://www.buymeacoffee.com/uiSK0Ex)

## License

This project is licensed under the [MIT License](LICENSE).

## Acknowledgments

- [Docker](https://www.docker.com/)
- [Apache Hadoop](https://hadoop.apache.org/)
- [Apache Spark](https://spark.apache.org/)
- [Apache Mahout](https://mahout.apache.org/)
- [Java](https://www.java.com/)
- [Maven](https://maven.apache.org/)
- [Angular](https://angular.io/)
- [MovieLens 100K Dataset](https://www.kaggle.com/datasets/prajitdatta/movielens-100k-dataset)

Notes

Get rid of all the oldest timestamps.
2.5% of the oldest timestamps
Happens every 3 months
Keeps up with user trends
Real time processing through online learning to fix the cold start problem