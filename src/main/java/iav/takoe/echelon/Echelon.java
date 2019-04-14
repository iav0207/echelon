package iav.takoe.echelon;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * An element of data structure which acts like a chain of value generators
 * which may be dependent on current state of other generators in the chain.
 * Each values set is obtained by switching of one or more echelons
 * to the next state which does not violate rules, which are injected to it,
 * so it acts as a deterministic state machine.
 * <p>
 * The purpose of using such data structure is traversing all distinct
 * states of a system, where each state fulfills some predefined rules.
 *
 * @param <V> type of values
 */
@ParametersAreNonnullByDefault
interface Echelon<V> extends Iterable<List<V>> {

    /**
     * Create child echelon, which will be a new tail.
     * Each echelon can have only one child, so if this method
     * is called twice on the same instance an exception will be thrown.
     *
     * @param rangeFactory supplier defining a dependency and providing
     *                     the new child with its values range
     *                     each time it is reset.
     * @return The child echelon, which is the new tail.
     */
    Echelon<V> createNext(Range<V> rangeFactory);

    /**
     * @return Current value of this particular element.
     */
    V getValue();

    @Override
    Iterator<List<V>> iterator();

    Stream<List<V>> stream();

}
