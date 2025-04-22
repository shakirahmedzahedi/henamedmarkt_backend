#!/bin/bash

set -e  # Exit on any error

# Ensure the directory exists
mkdir -p /firebase

# Check if FIREBASE_JSON is set
if [ -z "$FIREBASE_JSON" ]; then
  echo "FIREBASE_JSON environment variable is not set!"
  exit 1
fi

# Write the Firebase JSON to a file
echo "$FIREBASE_JSON" | jq '.' > /firebase/firebase.json

# Verify the file exists and is valid JSON
if [ ! -f /firebase/firebase.json ]; then
  echo "Failed to create Firebase credentials file!"
  exit 1
fi

# Set the env var
export GOOGLE_APPLICATION_CREDENTIALS=/firebase/firebase.json

# Launch the app
exec java -jar app.jar

