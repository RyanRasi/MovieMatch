from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

import contentbased, userbased, spark_train_model

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
    return spark_train_model.train()

@app.get("/recommendContent")
def read_root():
    return contentbased.recommendContent()

@app.get("/recommendUser")
def read_root():
    return userbased.recommendUser()