@rem ***************************************************************************
@rem Copyright (c) Contributors to the Eclipse Foundation
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem     http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@rem SPDX-License-Identifier: Apache-2.0 
@rem ***************************************************************************
keytool -genkey -alias test -keyalg RSA -keysize 1024 -sigalg MD5WithRSA -validity 30000 -keystore ../keystore -keypass testtest -storepass testtest -dname "CN=John Smith,O=ACME Inc,OU=ACME Cert Authority,L=Austin,ST=Texas,C=US"
keytool -export -alias test -file cert.crt -storepass testtest -keystore ../keystore
