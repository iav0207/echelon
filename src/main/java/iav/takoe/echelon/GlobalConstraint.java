package iav.takoe.echelon;

public interface GlobalConstraint<V> {

    boolean isFulfilled();

    void register(Echelon<V> echelon);

    void reportValueSwitch(Echelon<V> echelon);
}
