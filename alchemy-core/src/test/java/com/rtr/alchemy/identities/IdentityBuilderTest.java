package com.rtr.alchemy.identities;

import org.junit.Test;

import static org.junit.Assert.assertNotEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.junit.Assert.assertEquals;

public class IdentityBuilderTest {

    @Test
    public void testHashAll() {
        Identity identity = mock(Identity.class);
        doReturn(1L).when(identity).getHash(anyInt());

        long hash =
            IdentityBuilder
                .seed(0)
                .putBoolean(true)
                .putByte((byte) 1)
                .putBytes(new byte[] {1, 2, 3})
                .putChar('a')
                .putDouble(1.0)
                .putFloat(1.0f)
                .putInt(1)
                .putIdentity(identity)
                .putLong(1L)
                .putShort((short) 1)
                .putString("foo")
                .putNull()
                .hash();

        assertEquals(-4907526905224643059L, hash);
    }

    @Test
    public void testHashNulls() {
        long hash =
            IdentityBuilder
                .seed(0)
                .putBoolean(null)
                .putByte(null)
                .putBytes(null)
                .putChar(null)
                .putDouble(null)
                .putFloat(null)
                .putInt(null)
                .putIdentity(null)
                .putLong(null)
                .putShort(null)
                .putString(null)
                .putNull()
                .hash();

        assertEquals(2758870851737752684L, hash);

        long hash1 = IdentityBuilder.seed(0).putBoolean(null).hash();
        long hash2 = IdentityBuilder.seed(0).putNull().hash();
        assertEquals("hashing null value should be same as hashing null", hash1, hash2);
    }

    @Test
    public void testHashDifferentSeeds() {
        long hash1 = IdentityBuilder.seed(1).putBoolean(true).hash();
        long hash2 = IdentityBuilder.seed(2).putBoolean(true).hash();
        assertNotEquals("hashes generated with different seeds should be different most of the time", hash1, hash2);
    }

    @Test
    public void testHashSameSeed() {
        long hash1 = IdentityBuilder.seed(0).putBoolean(true).hash();
        long hash2 = IdentityBuilder.seed(0).putBoolean(true).hash();
        assertEquals("hashes generated with same seeds should be same", hash1, hash2);
    }
}
