package io.rtr.alchemy.db.mongo.util;

import io.rtr.alchemy.db.mongo.models.ExperimentEntity;
import io.rtr.alchemy.models.Experiment;

import java.util.Iterator;

/**
 * An iterator that iterates over ExperimentEntity results and maps them to actual Experiment instances
 */
public class ExperimentIterable implements Iterable<Experiment> {
    private final Iterator<ExperimentEntity> iterator;
    private final Experiment.BuilderFactory factory;

    public ExperimentIterable(Iterator<ExperimentEntity> iterator, Experiment.BuilderFactory factory) {
        this.iterator = iterator;
        this.factory = factory;
    }

    @Override
    public Iterator<Experiment> iterator() {
        return ExceptionSafeIterator.wrap(
            new Iterator<Experiment>() {
                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Experiment next() {
                    final ExperimentEntity entity = iterator.next();
                    return entity.toExperiment(factory.createBuilder(entity.name));
                }

                @Override
                public void remove() {
                    iterator.remove();
                }
            }
        );
    }
}