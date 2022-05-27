# ***************************************************************************
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
# ***************************************************************************
keytool -genkeypair -alias test -keyalg RSA -keysize 3072 -sigalg SHA256withRSA -validity 30000 -keystore ../keystore -keypass testtest -storepass testtest -dname "CN=John Smith,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US"
keytool -exportcert -alias test -file cert.crt -storepass testtest -keystore ../keystore
