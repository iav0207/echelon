package iav.takoe.echelon;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Spliterators.spliteratorUnknownSize;

@ParametersAreNonnullByDefault
class RegularEchelon<V> implements Echelon<V> {

    final Range<V> rangeFactory;

    private V value;
    private RegularEchelon<V> next;
    private Queue<V> range;

    RegularEchelon(Range<V> rangeFactory) {
        this.rangeFactory = rangeFactory;
        renew();
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public RegularEchelon<V> createNext(Range<V> rangeFactory) {
        return setNext(new RegularEchelon<>(rangeFactory));
    }

    RegularEchelon<V> setNext(RegularEchelon<V> newEchelon) {
        checkState(next == null, "once created, the successor slot cannot be replaced");
        next = newEchelon;
        return next;
    }

    boolean canSwitch() {
        if (next == null) {
            return canSwitchLocal();
        }
        return next.canSwitch() || canSwitchLocal();
    }

    private boolean canSwitchLocal() {
        return !range.isEmpty();
    }

    void switchValue() {
        if (next != null && next.canSwitch()) {
            next.switchValue();
            return;
        }
        switchLocalValue();
        if (next != null) {
            next.renew();
        }
    }

    private void renew() {
        range = new ArrayDeque<>(rangeFactory.get());
        switchLocalValue();
        if (next != null) {
            next.renew();
        }
    }

    void switchLocalValue() {
        checkState(canSwitchLocal(), "attempt to switch value when none left in the range");
        value = range.remove();
    }

    @Override
    public Iterator<List<V>> iterator() {
        return new RegularEchelonIterator();
    }

    @Override
    public Stream<List<V>> stream() {
        int characteristics = Spliterator.ORDERED | Spliterator.NONNULL;
        boolean parallel = false;
        return StreamSupport.stream(spliteratorUnknownSize(iterator(), characteristics), parallel);
    }

    List<V> getValues() {
        return addValues(new ArrayList<>());
    }

    private List<V> addValues(List<V> values) {
        values.add(value);
        return next == null ? values : next.addValues(values);
    }

    class RegularEchelonIterator implements Iterator<List<V>> {

        List<V> lastReturned = null;
        List<V> theNext = null;

        @Override
        public boolean hasNext() {
            if (theNext != null) {
                return true;
            }
            tryToFindNext();
            return theNext != null;
        }

        void tryToFindNext() {
            if (canSwitch()) {
                theNext = getValues();
                switchValue();
            } else {
                List<V> values = getValues();
                if (lastReturned != null && !values.equals(lastReturned)) {
                    theNext = values;
                }
            }
        }

        @Override
        @SuppressWarnings("squid:S2272")
        public List<V> next() {
            if (theNext == null && !hasNext()) {
                throw new NoSuchElementException();
            }
            List<V> result = lastReturned = theNext;
            theNext = null;
            return result;
        }
    }

}
