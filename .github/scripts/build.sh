#!/usr/bin/env bash
./gradlew --no-daemon -Dmaven.repo.local=cnf/generated/m2 --version
./gradlew --no-daemon -Dmaven.repo.local=cnf/generated/m2 --continue :install :osgi.specs:specifications "$@"
