package iav.takoe.echelon;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Spliterators.spliteratorUnknownSize;

@ParametersAreNonnullByDefault
class RegularEchelon<V> implements Echelon<V> {

    final ScopeFunction<V> scopeFunction;

    V value;
    RegularEchelon<V> next;
    Queue<V> scope;

    RegularEchelon(ScopeFunction<V> scopeFunction) {
        this.scopeFunction = scopeFunction;
        renew();
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public RegularEchelon<V> createNext(ScopeFunction<V> scopeGenerator) {
        return setNext(new RegularEchelon<>(scopeGenerator));
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

    @SuppressWarnings("SimplifiableIfStatement")
    boolean canSwitchLocal() {
        return !scope.isEmpty();
    }

    void switchValue() {
        if (next != null && next.canSwitch()) {
            next.switchValue();
            return;
        }
        checkState(canSwitchLocal(), "switchValue() called while canSwitch() == false");
        pollValue();
        if (next != null) {
            next.renew();
        }
    }

    void renew() {
        scope = new ArrayDeque<>(scopeFunction.get());
        pollValue();
        if (next != null) {
            next.renew();
        }
    }

    void pollValue() {
        checkState(canSwitchLocal(), "attempt to poll value when none left");
        value = scope.remove();
    }

    @Override
    public Iterator<Collection<V>> iterator() {
        return new RegularEchelonIterator();
    }

    @Override
    public Stream<Collection<V>> stream() {
        int characteristics = Spliterator.ORDERED | Spliterator.NONNULL;
        boolean parallel = false;
        return StreamSupport.stream(spliteratorUnknownSize(iterator(), characteristics), parallel);
    }

    @SuppressWarnings("squid:S3398")
    Collection<V> getValues() {
        return addValues(new ArrayList<>());
    }

    Collection<V> addValues(Collection<V> values) {
        values.add(value);
        return next == null ? values : next.addValues(values);
    }

    class RegularEchelonIterator implements Iterator<Collection<V>> {

        Collection<V> lastReturned = null;
        Collection<V> theNext = null;

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
                Collection<V> values = getValues();
                if (lastReturned != null && !values.equals(lastReturned)) {
                    theNext = values;
                }
            }
        }

        @Override
        @SuppressWarnings("squid:S2272")
        public Collection<V> next() {
            if (theNext == null && !hasNext()) {
                throw new NoSuchElementException();
            }
            Collection<V> result = lastReturned = theNext;
            theNext = null;
            return result;
        }
    }

}
