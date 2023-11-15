package io.rtr.alchemy.db.mongo.util;

import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.joda.time.DateTime;

/** A morphia codec for Joda Time */
public class DateTimeCodec implements Codec<DateTime> {
    @Override
    public DateTime decode(final BsonReader reader, final DecoderContext decoderContext) {
        if (reader.getCurrentBsonType().equals(BsonType.INT64)) {
            return new DateTime(reader.readInt64());
        }
        return new DateTime(reader.readDateTime());
    }

    @Override
    public void encode(
            final BsonWriter writer, final DateTime value, final EncoderContext encoderContext) {
        writer.writeDateTime(value.getMillis());
    }

    @Override
    public Class<DateTime> getEncoderClass() {
        return DateTime.class;
    }
}
