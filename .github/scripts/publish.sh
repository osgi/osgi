#!/usr/bin/env bash
./gradlew --no-daemon -Dmaven.repo.local=cnf/generated/m2 :deploy "$@"
