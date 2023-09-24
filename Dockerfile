# Stage 1: Build the Angular app
FROM node:16 as build-stage
WORKDIR /app
COPY movie-match/package*.json ./
RUN npm install
COPY movie-match .
RUN npm run build

# Stage 2: Serve the app with NGINX
FROM nginx:latest as production-stage
COPY --from=build-stage /app/dist/movie-match /usr/share/nginx/html
EXPOSE 4200
CMD ["nginx", "-g", "daemon off;"]
