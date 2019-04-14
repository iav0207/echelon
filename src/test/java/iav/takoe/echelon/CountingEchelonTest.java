package iav.takoe.echelon;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;

class CountingEchelonTest {

    private static final Logger log = getLogger(CountingEchelonTest.class);

    private static final Range<Integer> SCOPE = Range.fixed(0, 1);
    private static final int CHAIN_LENGTH = 4;
    private static final int TARGET_VALUES_COUNT = 2;

    private final ValueCountingConstraint<Integer> nonZeroValuesExactCountConstraint =
            new ValueCountingConstraint<>(values -> values.size() - values.count(0) == TARGET_VALUES_COUNT);

    private List<Collection<Integer>> result;

    @BeforeEach
    void init() {
        result = EchelonBuilder.constrained(nonZeroValuesExactCountConstraint)
                .createBatch(CHAIN_LENGTH, SCOPE)
                .getHead()
                .stream()
                .collect(toList());

        result.stream().map(this::asString).forEach(log::info);
    }

    @Test
    void eachValueSetShouldBeOfFixedLength() {
        result.forEach(each -> assertThat(each.size()).isEqualTo(CHAIN_LENGTH));
    }

    @Test
    void shouldContainExpectedNumberOfTargetValues() {
        result.forEach(each -> assertThat(each.size()).isEqualTo(TARGET_VALUES_COUNT));
    }

    private String asString(Collection<Integer> values) {
        return values.stream().map(String::valueOf).collect(joining());
    }

}
