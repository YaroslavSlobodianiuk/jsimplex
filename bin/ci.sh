#!/bin/bash
set -ev
if [ "${TRAVIS_OS_NAME}" = "osx" ]; then
	brew install gradle
fi