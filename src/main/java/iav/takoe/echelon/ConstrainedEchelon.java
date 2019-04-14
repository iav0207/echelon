package iav.takoe.echelon;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.Iterator;

@ParametersAreNonnullByDefault
class ConstrainedEchelon<V> extends RegularEchelon<V> {

    private final GlobalConstraint<V> constraint;

    ConstrainedEchelon(GlobalConstraint<V> constraint, ScopeFunction<V> scopeFunction) {
        super(scopeFunction);
        this.constraint = constraint;
        constraint.register(this);
    }

    @Override
    public RegularEchelon<V> createNext(ScopeFunction<V> scopeGenerator) {
        return setNext(new ConstrainedEchelon<>(constraint, scopeGenerator));
    }

    @Override
    void switchValue() {
        do {
            super.switchValue();
        } while (!constraint.isFulfilled() && canSwitch());
    }

    @Override
    void switchLocalValue() {
        super.switchLocalValue();
        if (constraint != null) {
            constraint.reportValueSwitch(this);
        }
    }

    @Override
    public Iterator<Collection<V>> iterator() {
        return new ConstrainedIterator();
    }

    class ConstrainedIterator extends RegularEchelonIterator {

        @Override
        void tryToFindNext() {

            if (constraint.isFulfilled()) {
                super.tryToFindNext();

            } else if (canSwitch()) {
                switchValue();
                theNext = constraint.isFulfilled() ? getValues() : null;
            }
        }
    }

}
