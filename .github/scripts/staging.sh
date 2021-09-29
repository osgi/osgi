#!/usr/bin/env bash
#*******************************************************************************
# Copyright (c) Contributors to the Eclipse Foundation
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
# SPDX-License-Identifier: Apache-2.0 
#*******************************************************************************
set -ev

OSSRH_STAGINGPROFILEURI=$(curl --silent --request GET -u "$OSSRH_USERNAME:$OSSRH_PASSWORD" --header 'Accept: application/json' --header 'Content-Type: application/json' --url https://oss.sonatype.org/service/local/staging/profiles | jq --raw-output '.data[] | select(.name == "org.osgi") | .resourceURI')
OSSRH_STAGINGREPOSITORYID=$(curl --silent --request POST -u "$OSSRH_USERNAME:$OSSRH_PASSWORD" --header 'Accept: application/json' --header 'Content-Type: application/json' --url $OSSRH_STAGINGPROFILEURI/start --data '{ "data": {"description": "OSGi Working Group"}}' | jq --raw-output '.data.stagedRepositoryId')
echo "OSSRH_STAGINGREPOSITORYID=$OSSRH_STAGINGREPOSITORYID" >> $GITHUB_ENV
echo "OSSRH_RELEASE=https://oss.sonatype.org/service/local/staging/deployByRepositoryId/$OSSRH_STAGINGREPOSITORYID" >> $GITHUB_ENV
