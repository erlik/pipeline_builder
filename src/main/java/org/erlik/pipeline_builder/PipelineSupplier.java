package org.erlik.pipeline_builder;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@FunctionalInterface
public interface PipelineSupplier<TObject> {

    Stream<TObject> get();

    default <TSeed> TSeed spread(TSeed seed,
                                 BiFunction<? super TObject, TSeed, TSeed> accumulator) {
        var pipeline = new SpreadPipeline<>(this.get());
        return pipeline.spread(seed, accumulator);
    }

    @FunctionalInterface
    interface Supply<TObject> {

        Stream<TObject> supply();
    }

    record SpreadPipeline<TObject>(Stream<TObject> list) {

        private static <T> Collector<T, ?, List<T>> reverse() {
            return Collectors.collectingAndThen(Collectors.toList(), list -> {
                Collections.reverse(list);
                return list;
            });
        }

        private <TSeed> TSeed spread(TSeed seed,
                                     BiFunction<? super TObject, TSeed, TSeed> accumulator) {
            TSeed result = seed;
            for (TObject i : list.collect(reverse())) {
                result = accumulator.apply(i, result);
            }
            return result;
        }
    }
}
