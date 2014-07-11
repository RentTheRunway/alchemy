package com.rtr.alchemy.identities;

import org.apache.commons.math3.stat.inference.ChiSquareTest;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;

public class IdentityBuilderTest {

    @Test
    public void testHashAll() {
        final Identity identity = mock(Identity.class);
        doReturn(1L).when(identity).computeHash(anyInt(), Mockito.<Set<String>>any(), any(AttributesMap.class));

        final long hash =
            IdentityBuilder
                .seed(0)
                .putBoolean(true)
                .putByte((byte) 1)
                .putBytes(new byte[]{1, 2, 3})
                .putChar('a')
                .putDouble(1.0)
                .putFloat(1.0f)
                .putInt(1)
                .putLong(1L)
                .putShort((short) 1)
                .putString("foo")
                .putNull()
                .hash();

        assertEquals(-3880933330945736271L, hash);
    }

    @Test
    public void testHashNulls() {
        final long hash =
            IdentityBuilder
                .seed(0)
                .putBoolean(null)
                .putByte(null)
                .putBytes(null)
                .putChar(null)
                .putDouble(null)
                .putFloat(null)
                .putInt(null)
                .putLong(null)
                .putShort(null)
                .putString(null)
                .putNull()
                .hash();

        assertEquals(7075199957211664123L, hash);

        final long hash1 = IdentityBuilder.seed(0).putBoolean(null).hash();
        final long hash2 = IdentityBuilder.seed(0).putNull().hash();
        assertEquals("hashing null value should be same as hashing null", hash1, hash2);
    }

    @Test
    public void testHashDifferentSeeds() {
        final long hash1 = IdentityBuilder.seed(1).putBoolean(true).hash();
        final long hash2 = IdentityBuilder.seed(2).putBoolean(true).hash();
        assertNotEquals("hashes generated with different seeds should be different most of the time", hash1, hash2);
    }

    @Test
    public void testHashSameSeed() {
        final long hash1 = IdentityBuilder.seed(0).putBoolean(true).hash();
        final long hash2 = IdentityBuilder.seed(0).putBoolean(true).hash();
        assertEquals("hashes generated with same seeds should be same", hash1, hash2);
    }

    private static final int SAMPLE_SIZE = 1000000;
    private static final int NUMBER_SEEDS = 3;
    private static final double EXPECTED_SIGNIFICANCE = 0.05;
    private static final int MIN_NUM_BINS = 2;
    private static final int MAX_NUM_BINS = 10;

    @Test
    public void testDistribution() {
        final ChiSquareTest chiSquareTest = new ChiSquareTest();

        for (int numBins=MIN_NUM_BINS; numBins <= MAX_NUM_BINS; numBins++) {
            final double[] expectedFrequencies = new double[numBins];
            final long[] actualFrequencies = new long[numBins];

            for (int i=0; i<numBins; i++) {
                expectedFrequencies[i] = SAMPLE_SIZE / numBins;
            }

            for (int seed = 0; seed < NUMBER_SEEDS; seed++) {
                for (int sample = 0; sample < SAMPLE_SIZE; sample++) {
                    final int bin = (int) (Math.abs(IdentityBuilder.seed(seed).putInt(sample).hash()) % numBins);
                    actualFrequencies[bin]++;
                }

                final double significance = chiSquareTest.chiSquareTest(expectedFrequencies, actualFrequencies);

                assertTrue(
                    String.format(
                        "distribution was not uniform, significance was %.4f for %d bins",
                        significance,
                        numBins
                    ),
                    significance > EXPECTED_SIGNIFICANCE
                );
            }
        }
    }
}
