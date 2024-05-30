# Use an official lightweight Scala and SBT image as a parent image
FROM hseeberger/scala-sbt:11.0.13_1.6.1_2.13.7 as build

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Compile and package the application
RUN sbt clean compile stage

# Use the OpenJDK image for running the application
FROM openjdk:11-jre-slim

# Copy the binary files from the previous stage
COPY --from=build /app/target/universal/stage /app
# Set the working directory in the container
WORKDIR /app

# Make port 9000 available to the world outside this container
EXPOSE 9000

# Define environment variable
ENV PLAY_HTTP_SECRET=a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6A7B8C9D0E1F2G3H4I5J6K7L8M9N0O1P2Q3R4S5T6U7V8W9X0Y1Z2


# Run the binary script when the container launches
CMD ./bin/play-service-n-app -Dplay.http.secret.key=$PLAY_HTTP_SECRET