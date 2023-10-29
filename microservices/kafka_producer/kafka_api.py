from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import kafka_producer

app = FastAPI()

# CORS configuration
origins = [
    "http://localhost",
    "http://localhost:8000",
    "http://127.0.0.1:8000"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get("/produce")
def read_item(user_id: str = None, movie_id: str = None, rating: str = None,):
    if user_id != None and movie_id != None and rating != None:
        print("user_id", user_id)
        print("movie_id", movie_id)
        print("rating", rating)
        return kafka_producer.producer(user_id, movie_id, rating)
    else:
        return "One of more params are empty"
