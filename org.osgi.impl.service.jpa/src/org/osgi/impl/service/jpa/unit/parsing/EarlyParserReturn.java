/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.osgi.impl.service.jpa.unit.parsing;

import javax.xml.validation.Schema;

import org.xml.sax.SAXException;

/**
 * A convenience mechanism for finding the schema to validate with
 */
public class EarlyParserReturn extends SAXException
{
  /** This class is serializable */
  private static final long serialVersionUID = 6173561765417524327L;
  /** The schema to use */
  private final Schema schema;
  /** The value of the version attribute in the xml */
  private final String jpaVersion;

  /**
   * @return The schema that was used in the xml document
   */
  public Schema getSchema()
  {
    return schema;
  }
  
  /**
   * @return The version of the JPA schema used
   */
  public String getVersion()
  {
    return jpaVersion;
  }

  /**
   * @param s  The schema used
   * @param version The version of the schema used
   */
  public EarlyParserReturn(Schema s, String version)
  {
    schema = s;
    jpaVersion = version;
  }
  
  
}
