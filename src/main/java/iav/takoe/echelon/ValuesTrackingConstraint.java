package iav.takoe.echelon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public abstract class ValuesTrackingConstraint<V> implements GlobalConstraint<V> {

    private final Map<Echelon<V>, Integer> index = new HashMap<>();
    private final List<V> values = new ArrayList<>();

    @Override
    public void register(Echelon<V> echelon) {
        checkState(!index.containsKey(echelon), "attempt to register a node twice");
        index.putIfAbsent(echelon, index.size());
        values.add(echelon.getValue());
    }

    @Override
    public void reportValueSwitch(Echelon<V> echelon) {
        setNewValue(echelon);
    }

    protected V getStoredValue(Echelon<V> echelon) {
        return values.get(index.get(echelon));
    }

    protected void setNewValue(Echelon<V> echelon) {
        values.set(index.get(echelon), echelon.getValue());
    }
}
