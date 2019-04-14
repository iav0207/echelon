package iav.takoe.echelon;

import iav.takoe.echelon.Echelon.ScopeFunction;

public class EchelonBuilder<V> {

    private final Echelon<V> head;
    private Echelon<V> tail;

    private EchelonBuilder(Echelon<V> head) {
        tail = this.head = head;
    }

    public static <V> InitialStep<V> constrained(GlobalConstraint<V> constraint) {
        return scopeFunction -> new EchelonBuilder<>(new ConstrainedEchelon<>(constraint, scopeFunction));
    }

    public static <V> InitialStep<V> regular() {
        return scopeFunction -> new EchelonBuilder<>(new RegularEchelon<>(scopeFunction));
    }

    public interface InitialStep<V> {
        EchelonBuilder<V> createFirst(ScopeFunction<V> scopeFunction);

        default EchelonBuilder<V> createBatch(int count, ScopeFunction<V> scopeFunction) {
            return createFirst(scopeFunction).createSuccessors(count - 1, scopeFunction);
        }
    }

    public EchelonBuilder<V> createSuccessors(int count, ScopeFunction<V> scopeFunction) {
        for (int i = 0; i < count; i++) {
            createNext(scopeFunction);
        }
        return this;
    }

    public EchelonBuilder<V> createNext(V... values) {
        return createNext(ScopeFunction.sequence(values));
    }

    public EchelonBuilder<V> createNext(ScopeFunction<V> scopeGenerator) {
        tail = tail.createNext(scopeGenerator);
        return this;
    }

    public Echelon<V> getHead() {
        return head;
    }
}
