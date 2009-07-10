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

package org.osgi.test.cases.blueprint.java5.components.injection;

import java.util.List;
import java.util.LinkedList;
import java.util.TreeMap;

import org.osgi.service.blueprint.container.Converter;
import org.osgi.service.blueprint.container.ReifiedType;
import org.osgi.test.cases.blueprint.services.BaseTestComponent;

@SuppressWarnings("unchecked")
public class GenericTypeConverter extends BaseTestComponent {
    // the injected converter type
    protected Converter blueprintConverter;

    public GenericTypeConverter(String componentId, Converter blueprintConverter) {
        super(componentId);
        this.blueprintConverter = blueprintConverter;
    }


    public Object convert(Object s, ReifiedType type) throws Exception {
        switch (type.size()) {
            // we should only be getting called for things we've already vetted,
            // so we can take some short cuts.
            case 1:
            {
                Class<?>targetType = type.getRawClass();
                // we only handle LinkedList<Point>, and only from an array of ints.
                if (targetType == LinkedList.class) {
                    LinkedList result = new LinkedList();
                    int[] source = (int[])s;

                    int counter = 0;
                    while (counter < source.length) {
                        Point p = new Point(source[counter], source[counter + 1]);
                        result.add(p);
                        counter += 2;
                    }

                    return result;
                }
                else if (targetType == GenericHolder.class) {
                    Class<?>listType = type.getActualTypeArgument(0).getRawClass();
                    GenericHolder source = (GenericHolder)s;
                    // this just validates the type
                    if (listType.isInstance(source.getValue())) {
                        return source;
                    }
                }
            }
            case 2:
            {
                TreeMap result = new TreeMap();
                List source = (List)s;

                int counter = 0;
                while (counter < source.size()) {
                    String index = (String)source.get(counter);
                    int x = Integer.parseInt((String)source.get(counter + 1));
                    int y = Integer.parseInt((String)source.get(counter + 2));

                    Point p = new Point(x, y);
                    result.put(index, p);
                    counter += 3;
                }

                return result;
            }
        }
        // we're supposed to throw an exception if we can't convert
        throw new Exception("Unconvertable object type");
    }


    public boolean canConvert(Object source, ReifiedType type ) {
        switch (type.size()) {
            case 0:
                return false;
            // probably a collection type with a target.  We only handle some
            // very specific conversions here.
            case 1:
            {
                Class<?>targetType = type.getRawClass();
                // we only handle LinkedList<Point>, and only from an array of ints.
                if (targetType == LinkedList.class) {
                    Class<?>listType = type.getActualTypeArgument(0).getRawClass();
                    if (!(listType == Point.class)) {
                        return false;
                    }

                    // the only source we handle is an array of ints with even size
                    if (source instanceof int[] && (((int[])source).length % 2) == 0) {
                        return true;
                    }
                    return false;
                }
                // this is our generic type class.  Check for convertability also
                else if (targetType == GenericHolder.class) {
                    // all we do is handle the held value
                    return source instanceof GenericHolder;
                }

                return false;
            }
            case 2:
            {
                Class<?>targetType = type.getRawClass();
                // we only handle TreeMap<String, Point>, and only from a List of triplet values
                if (!(targetType != TreeMap.class)) {
                    return false;
                }
                Class<?>indexType = type.getActualTypeArgument(0).getRawClass();
                if (indexType != String.class) {
                    return false;
                }

                Class<?>itemType = type.getActualTypeArgument(1).getRawClass();
                if (itemType != Point.class) {
                    return false;
                }

                // the only source we handle is a list of values with a size that's
                // a multiple of 3.
                if (source instanceof List && (((List)source).size() % 3) == 0) {
                    return true;
                }

                return false;
            }
            default:
                // don't understand this
                return false;
        }
    }
}

