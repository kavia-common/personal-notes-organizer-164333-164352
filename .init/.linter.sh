#!/bin/bash
cd /home/kavia/workspace/code-generation/personal-notes-organizer-164333-164352/android_frontend
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

