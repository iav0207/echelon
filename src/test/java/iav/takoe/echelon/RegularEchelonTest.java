package iav.takoe.echelon;

import com.google.common.collect.Lists;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Logger.getLogger;
import static org.assertj.core.api.Assertions.assertThat;

class RegularEchelonTest {

    private static final Logger log = getLogger(RegularEchelonTest.class);

    private static final Range<Integer> RANGE = Range.fixed(0, 1);
    private static final int CHAIN_LENGTH = 4;

    private List<List<Integer>> result;

    @BeforeEach
    void init() {
        result = EchelonBuilder.<Integer>regular()
                .createBatch(CHAIN_LENGTH, RANGE)
                .getHead()
                .stream()
                .collect(toList());

        result.stream().map(this::asString).forEach(log::info);
    }

    @Test
    void shouldBeOfExpectedLength() {
        assertThat(result).hasSize((int) Math.pow(RANGE.get().size(), CHAIN_LENGTH));
    }

    @Test
    void shouldContainDistinctValueSetsOnly() {
        assertThat(result).hasSameSizeAs(new HashSet<>(result));
    }

    @Test
    void finalResultShouldBeIdenticalToCartesianProductIfScopesAreConstantSequences() {
        List<Integer> scopeAsList = RANGE.get();
        List<List<Integer>> allScopes = Stream.generate(() -> scopeAsList).limit(CHAIN_LENGTH).collect(toList());

        assertThat(result).isEqualTo(Lists.cartesianProduct(allScopes));
    }

    private String asString(Collection<Integer> values) {
        return values.stream().map(String::valueOf).collect(joining());
    }

}
