package iav.takoe.echelon;

import com.google.common.collect.Multiset;

import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;

public class EchelonBuilder<V> {

    private final Echelon<V> head;
    private Echelon<V> tail;

    private EchelonBuilder(Echelon<V> head) {
        tail = this.head = head;
    }

    public static <V> InitialStep<V> countingValues(Predicate<Multiset<V>> fulfillmentCondition) {
        return constrained(new ValuesCountingConstraint<>(fulfillmentCondition));
    }

    public static <V> InitialStep<V> constrained(GlobalConstraint<V> constraint) {
        return range -> new EchelonBuilder<>(new ConstrainedEchelon<>(constraint, range));
    }

    public static <V> InitialStep<V> regular() {
        return range -> new EchelonBuilder<>(new RegularEchelon<>(range));
    }

    public interface InitialStep<V> {
        EchelonBuilder<V> createFirst(Range<V> range);

        default EchelonBuilder<V> createBatch(int count, Range<V> range) {
            return createFirst(range).createSuccessors(count - 1, range);
        }
    }

    public EchelonBuilder<V> createSuccessors(int count, Range<V> range) {
        checkArgument(count > 0);
        for (int i = 0; i < count; i++) {
            createNext(range);
        }
        return this;
    }

    public EchelonBuilder<V> createNext(Range<V> range) {
        tail = tail.createNext(range);
        return this;
    }

    public Echelon<V> getHead() {
        return head;
    }
}
