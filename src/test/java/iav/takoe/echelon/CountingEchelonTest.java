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

    private static final int CHAIN_LENGTH = 4;
    private static final int NON_ZEROS_COUNT = 2;
    private final Range<Integer> range = Range.fixed(0, 1);

    private List<List<Integer>> result;

    @BeforeEach
    void init() {
        result = EchelonBuilder.<Integer>countingValues(values -> values.size() - values.count(0) == NON_ZEROS_COUNT)
                .createBatch(CHAIN_LENGTH, range)
                .getHead()
                .stream()
                .collect(toList());

        result.stream().map(this::asString).forEach(log::info);
    }

    @Test
    void eachValueSetShouldBeOfFixedLength() {
        result.forEach(each -> assertThat(each).hasSize(CHAIN_LENGTH));
    }

    @Test
    void shouldContainExpectedNumberOfTargetValues() {
        result.forEach(each -> assertThat(each.stream().filter(n -> n != 0)).hasSize(NON_ZEROS_COUNT));
    }

    private String asString(Collection<Integer> values) {
        return values.stream().map(String::valueOf).collect(joining());
    }

}
