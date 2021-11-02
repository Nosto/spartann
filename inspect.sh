#!/bin/bash

docker run --env GITHUB_WORKSPACE=/app --volume="$(pwd)"/.:/app docker.pkg.github.com/mridang/action-idea/idealize:2020.3.3 /app /app/.idea/inspectionProfiles/CI.xml /out v2 Inspection noop 1347
