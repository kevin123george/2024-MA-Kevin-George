# Dockerfile for Vite App

# Stage 1: Build the application
FROM node:18-alpine AS build

# Set the working directory inside the container
WORKDIR /app

# Copy package.json and package-lock.json and install dependencies using npm
COPY package*.json ./
RUN npm install

# Copy the rest of the application
COPY . .

# Build the Vite app
RUN npm run build

# Stage 2: Serve the application
FROM nginx:alpine

# Copy the built files from the previous stage
COPY --from=build /app/dist /usr/share/nginx/html

# Expose port 80 to the outside
EXPOSE 80

# Start the nginx server
CMD ["nginx", "-g", "daemon off;"]
