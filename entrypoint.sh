#!/bin/bash

# Ensure the directory exists
mkdir -p /firebase

# Write the Firebase JSON to the container from the GitHub secret or environment variable
echo "$FIREBASE_JSON" > /firebase/firebase.json

# Set the GOOGLE_APPLICATION_CREDENTIALS environment variable
export GOOGLE_APPLICATION_CREDENTIALS=/firebase/firebase.json

# Run the Spring Boot app
exec java -jar app.jar
