package iav.takoe.echelon;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.function.Supplier;

public interface Range<V> extends Supplier<List<V>> {

    @SafeVarargs
    static <V> Range<V> fixed(V... values) {
        List<V> list = ImmutableList.copyOf(values);
        return () -> list;
    }
}
