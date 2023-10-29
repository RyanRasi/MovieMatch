from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import trainCustomModel, getUserRecommendation, randomWine

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

@app.get("/train")
def read_root():
    return trainCustomModel.model()

@app.get("/recommendContent")
def read_root():
    return trainCustomModel.model()

@app.get("/recommendUser")
def read_root():
    return trainCustomModel.model()