/*
 * Copyright (C) 2017 The Android Open Source Project
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

package android.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.graphics.Typeface;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class MeasuredTextTest {
    private static final TextDirectionHeuristic LTR = TextDirectionHeuristics.LTR;
    private static final TextDirectionHeuristic RTL = TextDirectionHeuristics.RTL;

    private static final TextPaint PAINT = new TextPaint();
    static {
        // The test font has following coverage and width.
        // U+0020: 10em
        // U+002E (.): 10em
        // U+0043 (C): 100em
        // U+0049 (I): 1em
        // U+004C (L): 50em
        // U+0056 (V): 5em
        // U+0058 (X): 10em
        // U+005F (_): 0em
        // U+FFFD (invalid surrogate will be replaced to this): 7em
        // U+10331 (\uD800\uDF31): 10em
        Context context = InstrumentationRegistry.getTargetContext();
        PAINT.setTypeface(Typeface.createFromAsset(context.getAssets(),
                  "fonts/StaticLayoutLineBreakingTestFont.ttf"));
        PAINT.setTextSize(1.0f);  // Make 1em == 1px.
    }

    private String charsToString(char[] chars) {
        return (new StringBuilder()).append(chars).toString();
    }

    @Test
    public void buildForBidi() {
        MeasuredText mt = null;

        mt = MeasuredText.buildForBidi("XXX", 0, 3, LTR, null);
        assertNotNull(mt);
        assertNotNull(mt.getChars());
        assertEquals("XXX", charsToString(mt.getChars()));
        assertEquals(Layout.DIR_LEFT_TO_RIGHT, mt.getParagraphDir());
        assertNotNull(mt.getDirections(0, 3));
        assertEquals(0, mt.getWholeWidth(), 0);
        assertEquals(0, mt.getWidths().size());
        assertEquals(0, mt.getSpanEndCache().size());
        assertEquals(0, mt.getFontMetrics().size());

        // Recycle it
        MeasuredText mt2 = MeasuredText.buildForBidi("_VVV_", 1, 4, RTL, mt);
        assertEquals(mt2, mt);
        assertNotNull(mt2.getChars());
        assertEquals("VVV", charsToString(mt.getChars()));
        assertNotNull(mt2.getDirections(0, 3));
        assertEquals(0, mt2.getWholeWidth(), 0);
        assertEquals(0, mt2.getWidths().size());
        assertEquals(0, mt2.getSpanEndCache().size());
        assertEquals(0, mt2.getFontMetrics().size());

        mt2.recycle();
    }

    @Test
    public void buildForMeasurement() {
        MeasuredText mt = null;

        mt = MeasuredText.buildForMeasurement(PAINT, "XXX", 0, 3, LTR, null);
        assertNotNull(mt);
        assertNotNull(mt.getChars());
        assertEquals("XXX", charsToString(mt.getChars()));
        assertEquals(Layout.DIR_LEFT_TO_RIGHT, mt.getParagraphDir());
        assertNotNull(mt.getDirections(0, 3));
        assertEquals(30, mt.getWholeWidth(), 0);
        assertEquals(3, mt.getWidths().size());
        assertEquals(10, mt.getWidths().get(0), 0);
        assertEquals(10, mt.getWidths().get(1), 0);
        assertEquals(10, mt.getWidths().get(2), 0);
        assertEquals(0, mt.getSpanEndCache().size());
        assertEquals(0, mt.getFontMetrics().size());

        // Recycle it
        MeasuredText mt2 = MeasuredText.buildForMeasurement(PAINT, "_VVV_", 1, 4, RTL, mt);
        assertEquals(mt2, mt);
        assertNotNull(mt2.getChars());
        assertEquals("VVV", charsToString(mt.getChars()));
        assertEquals(Layout.DIR_RIGHT_TO_LEFT, mt2.getParagraphDir());
        assertNotNull(mt2.getDirections(0, 3));
        assertEquals(15, mt2.getWholeWidth(), 0);
        assertEquals(3, mt2.getWidths().size());
        assertEquals(5, mt2.getWidths().get(0), 0);
        assertEquals(5, mt2.getWidths().get(1), 0);
        assertEquals(5, mt2.getWidths().get(2), 0);
        assertEquals(0, mt2.getSpanEndCache().size());
        assertEquals(0, mt2.getFontMetrics().size());

        mt2.recycle();
    }

    @Test
    public void testFor70146381() {
        MeasuredText.buildForMeasurement(PAINT, "X…", 0, 2, RTL, null);
    }
}
