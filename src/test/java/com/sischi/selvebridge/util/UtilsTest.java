package com.sischi.selvebridge.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class UtilsTest implements HasLogger {

    @Test
    public void readIdsFromBinaryMaskTest() {
        String mask = "0000000000000000000000000000000000000000000000000000000000000000";
        List<Integer> ids = Utils.readIdsFromBinaryMask(mask);
        assertEquals(0, ids.size());

        mask = "0000000100000000000000000000000000000000000000000000000000000000";
        ids = Utils.readIdsFromBinaryMask(mask);
        assertEquals(1, ids.size());
        assertEquals(0, ids.get(0));

        mask = "1000000100100000000000000000000000000000000000000000000010000000";
        ids = Utils.readIdsFromBinaryMask(mask);
        assertEquals(4, ids.size());
        assertTrue(ids.contains(0));
        assertTrue(ids.contains(7));
        assertTrue(ids.contains(13));
        assertTrue(ids.contains(63));
    }

    @Test
    public void writeIdsAsBinaryMaskTest() {
        List<Integer> ids = new ArrayList<>();
        String mask = Utils.writeIdsAsBinaryMask(ids);
        String expected = "0000000000000000000000000000000000000000000000000000000000000000";
        assertEquals(expected, mask);

        ids = Arrays.asList(0);
        mask = Utils.writeIdsAsBinaryMask(ids);
        expected = "0000000100000000000000000000000000000000000000000000000000000000";
        assertEquals(expected, mask);

        ids = Arrays.asList(0, 7, 13, 63);
        mask = Utils.writeIdsAsBinaryMask(ids);
        expected = "1000000100100000000000000000000000000000000000000000000010000000";
        assertEquals(expected, mask);
    }
    
}
