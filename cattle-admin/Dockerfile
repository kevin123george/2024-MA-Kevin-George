# Dockerfile for Vite with Bun
# Use the official Bun image
FROM jarredsumner/bun:edge

# Set the working directory inside the container
WORKDIR /app

# Copy the package files and source code
COPY . .

# Install dependencies
RUN bun install

# Expose port 5173
EXPOSE 5173

# Run the Vite development server
CMD ["bun", "run", "dev", "--host"]
