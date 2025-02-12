/**
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.transformation;

import com.gs.dmn.feel.analysis.scanner.LexicalContext;
import com.gs.dmn.runtime.Pair;
import org.junit.Test;
import org.omg.dmn.tck.marshaller._20160719.TestCases;

import static com.gs.dmn.runtime.Assert.assertEquals;

public class ToQuotedNameTransformerTest extends NameTransformerTest {
    @Test
    public void testTransform() throws Exception {
        doTest("0004-lending.dmn", new Pair<>("http://www.trisotech.com/definitions/_4e0f0b70-d31c-471c-bd52-5ca709ed362b", "tns"),
                "0004-lending-test-01.xml", new Pair<>("http://www.w3.org/2001/XMLSchema-instance", "xsi"));
        doTest("0007-date-time.dmn", new Pair<>("http://www.trisotech.com/definitions/_69430b3e-17b8-430d-b760-c505bf6469f9", "tns"),
                "0007-date-time-test-01.xml", new Pair<>("http://www.w3.org/2001/XMLSchema-instance", "xsi"));
        doTest("0034-drg-scopes.dmn", new Pair<>("http://www.actico.com/spec/DMN/0.1.0/0034-drg-scopes", "tns"),
                "0034-drg-scopes-test-01.xml", new Pair<>("http://www.w3.org/2001/XMLSchema-instance", "xsi"));
    }

    @Test
    public void testTransformName() {
        ToQuotedNameTransformer transformer = (ToQuotedNameTransformer) getTransformer();

        // Transform first name
        String firstName = transformer.transformName("abc ? x");
        assertEquals("'abc ? x'", firstName);

        // Transform second name
        String secondName = transformer.transformName("abc?x");
        assertEquals("'abc?x'", secondName);

        // Transform names with unicode
        String result = transformer.transformName("a \uD83D\uDC0E bc");
        assertEquals("'a \uD83D\uDC0E bc'", result);
    }

    @Test
    public void testContextKeys() {
        NameTransformer transformer = (NameTransformer) getTransformer();

        String result = transformer.replaceNamesInText("{foo bar: \"foo\"}", new LexicalContext());
        assertEquals("{'foo bar': \"foo\"}", result);

        result = transformer.replaceNamesInText("{foo+bar: \"foo\"}", new LexicalContext());
        assertEquals("{'foo+bar': \"foo\"}", result);

        result = transformer.replaceNamesInText("{\"foo+bar((!!],foo\": \"foo\"}", new LexicalContext());
        assertEquals("{\"'foo+bar((!!],foo'\": \"foo\"}", result);

        result = transformer.replaceNamesInText("{\"\": \"foo\"}", new LexicalContext());
        assertEquals("{\"\": \"foo\"}", result);

        result = transformer.replaceNamesInText("{a: 1 + 2, b: a + 3}", new LexicalContext());
        assertEquals("{a: 1 + 2, b: a + 3}", result);

        result = transformer.replaceNamesInText("{a: 1 + 2, b: 3, c: {d e: a + b}}", new LexicalContext());
        assertEquals("{a: 1 + 2, b: 3, c: {'d e': a + b}}", result);

        result = transformer.replaceNamesInText("{\uD83D\uDC0E: \"bar\"}", new LexicalContext());
        assertEquals("{'\uD83D\uDC0E': \"bar\"}", result);

        String text = "function(s1, s2) external {java:{class:\"java.lang.Math\",method signature:\"max(java.lang.String, java.lang.String)\"}}";
        LexicalContext context = new LexicalContext("mathMaxString");
        result = transformer.replaceNamesInText(text, context);
        assertEquals("function(s1, s2) external {java:{class:\"java.lang.Math\", 'method signature':\"max(java.lang.String, java.lang.String)\"}}", result);
    }

    @Test
    public void testBuiltinFunction() {
        NameTransformer transformer = (NameTransformer) getTransformer();

        String result = transformer.replaceNamesInText("number(from: \"1.000.000,01\", decimal separator:\",\", grouping separator:\".\")", new LexicalContext("decimal separator", "grouping separator"));
        assertEquals("number(from: \"1.000.000,01\", 'decimal separator':\",\", 'grouping separator':\".\")", result);

        result = transformer.replaceNamesInText("substring(string:\"abc\", starting position:2)", new LexicalContext("starting position"));
        assertEquals("substring(string:\"abc\", 'starting position':2)", result);
    }

    @Override
    protected DMNTransformer<TestCases> getTransformer() {
        return new ToQuotedNameTransformer(LOGGER);
    }

    @Override
    protected String getInputPath() {
        return "dmn/input/";
    }

    @Override
    protected  String getTargetPath() {
        return "target/quoted/";
    }

    @Override
    protected  String getExpectedPath() {
        return "dmn/expected/quoted/";
    }
}