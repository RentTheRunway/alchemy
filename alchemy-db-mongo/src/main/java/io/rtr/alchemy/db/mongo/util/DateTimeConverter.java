package io.rtr.alchemy.db.mongo.util;

import org.joda.time.DateTime;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

/** A morphia converter for Joda Time */
public class DateTimeConverter extends TypeConverter implements SimpleValueConverter {
    protected DateTimeConverter() {
        super(DateTime.class);
    }

    @Override
    public Object decode(Class clazz, Object o, MappedField mappedField) {
        final Long instant = (Long) o;
        if (instant == null) {
            return null;
        }
        return new DateTime(instant);
    }

    @Override
    public Object encode(Object value, MappedField optionalExtraInfo) {
        final DateTime dateTime = (DateTime) value;
        if (dateTime == null) {
            return null;
        }
        return dateTime.getMillis();
    }
}
