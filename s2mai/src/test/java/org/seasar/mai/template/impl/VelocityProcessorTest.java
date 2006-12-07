/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.mai.template.impl;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Satsohi Kimura
 */
public class VelocityProcessorTest extends TestCase {
    private VelocityProcessor processor = new VelocityProcessor();

    private VelocityContextHelperImpl helper = new VelocityContextHelperImpl();

    protected void setUp() throws Exception {
        processor.setConfigFile("velocity.properties");
        processor.init();
    }

    public void testProcess() {
        Map context = new HashMap();
        context.put("name", "foo");
        String result = processor.process("$name", helper.createContext(context));
        assertEquals("foo", result);
    }

    public void testProcessResource() {
        fail("Not yet implemented");
    }

}