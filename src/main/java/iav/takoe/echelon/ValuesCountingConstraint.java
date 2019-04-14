package iav.takoe.echelon;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import java.util.function.Predicate;

public class ValuesCountingConstraint<V> extends ValuesTrackingConstraint<V> {
    private final Multiset<V> counter = HashMultiset.create();
    private final Predicate<Multiset<V>> condition;

    public ValuesCountingConstraint(Predicate<Multiset<V>> fulfillmentCondition) {
        this.condition = fulfillmentCondition;
    }

    @Override
    public boolean isFulfilled() {
        return condition.test(counter);
    }

    @Override
    public void register(Echelon<V> echelon) {
        super.register(echelon);
        counter.add(echelon.getValue());
    }

    @Override
    public void reportValueSwitch(Echelon<V> echelon) {
        counter.remove(getStoredValue(echelon));
        counter.add(echelon.getValue());
        super.reportValueSwitch(echelon);
    }
}
