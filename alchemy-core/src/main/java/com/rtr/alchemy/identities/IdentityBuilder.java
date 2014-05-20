package com.rtr.alchemy.identities;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import java.nio.charset.Charset;

/**
 * Used for building a unique identity
 */
public class IdentityBuilder {
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private final Hasher hasher;
    private final int seed;

    private IdentityBuilder(int seed) {
        this.hasher = Hashing.murmur3_128(seed).newHasher();
        this.seed = seed;
    }

    public static IdentityBuilder seed(int seed) {
        return new IdentityBuilder(seed);
    }

    public IdentityBuilder putByte(Byte value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putByte(value);
        }
        return this;
    }

    public IdentityBuilder putBytes(byte[] value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putBytes(value);
        }
        return this;
    }

    public IdentityBuilder putBytes(byte[] value, int start, int length) {
        if (value == null) {
            putNull();
        } else {
            hasher.putBytes(value, start, length);
        }
        return this;
    }

    public IdentityBuilder putShort(Short value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putShort(value);
        }
        return this;
    }

    public IdentityBuilder putInt(Integer value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putInt(value);
        }
        return this;
    }

    public IdentityBuilder putLong(Long value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putLong(value);
        }
        return this;
    }

    public IdentityBuilder putFloat(Float value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putFloat(value);
        }
        return this;
    }

    public IdentityBuilder putDouble(Double value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putDouble(value);
        }
        return this;
    }

    public IdentityBuilder putBoolean(Boolean value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putBoolean(value);
        }
        return this;
    }

    public IdentityBuilder putChar(Character value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putChar(value);
        }
        return this;
    }

    public IdentityBuilder putString(CharSequence value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putString(value, CHARSET);
        }
        return this;
    }

    public IdentityBuilder putNull() {
        hasher.putLong(0);
        return this;
    }

    public IdentityBuilder putIdentity(Identity value) {
        if (value == null) {
            putNull();
        } else {
            hasher.putLong(value.computeHash(seed));
        }
        return this;
    }

    public long hash() {
        return hasher.hash().asLong();
    }
}
