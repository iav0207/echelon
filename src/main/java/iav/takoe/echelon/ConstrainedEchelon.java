package iav.takoe.echelon;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.List;

@ParametersAreNonnullByDefault
class ConstrainedEchelon<V> extends RegularEchelon<V> {

    private final GlobalConstraint<V> constraint;

    ConstrainedEchelon(GlobalConstraint<V> constraint, Range<V> range) {
        super(range);
        this.constraint = constraint;
        constraint.register(this);
    }

    @Override
    public RegularEchelon<V> createNext(Range<V> rangeFactory) {
        return setNext(new ConstrainedEchelon<>(constraint, rangeFactory));
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
    public Iterator<List<V>> iterator() {
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
