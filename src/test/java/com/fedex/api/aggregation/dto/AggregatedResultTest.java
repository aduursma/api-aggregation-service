package com.fedex.api.aggregation.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fedex.api.aggregation.AggregatedResultBuilder;
import com.google.common.testing.EqualsTester;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@JsonTest
class AggregatedResultTest {

    @Autowired
    JacksonTester<AggregatedResult> jacksonTester;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void verifySerialization() throws Exception {
        JsonContent<AggregatedResult> result = this.jacksonTester.write(AggregatedResultBuilder.validResult());

        assertThat(result).hasJsonPath("$.pricing");
        assertThat(result).extractingJsonPathNumberValue("$.pricing.NL").isEqualTo(14.242090605778);
        assertThat(result).extractingJsonPathNumberValue("$.pricing.CN").isEqualTo(20.503467806384);
        assertThat(result).hasJsonPath("$.track");
        assertThat(result).extractingJsonPathStringValue("$.track.109347263").isNull();
        assertThat(result).extractingJsonPathStringValue("$.track.123456891").isEqualTo("COLLECTING");
        assertThat(result).hasJsonPath("$.shipments");
        assertThat(result).extractingJsonPathArrayValue("$.shipments.109347263").containsExactly("box", "box", "pallet");
        assertThat(result).extractingJsonPathArrayValue("$.shipments.123456891").isNull();
    }

    @Test
    void verifyDeserialization() throws Exception {
        String resultJson = objectMapper.writeValueAsString(AggregatedResultBuilder.validResult());

        AggregatedResult result = this.jacksonTester.parseObject(resultJson);

        Assertions.assertThat(result.getPricing())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("NL", new BigDecimal("14.242090605778")), entry("CN", new BigDecimal("20.503467806384")));

        Assertions.assertThat(result.getTrack())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("109347263", null), entry("123456891", "COLLECTING"));

        Assertions.assertThat(result.getShipments())
            .isNotEmpty()
            .hasSize(2)
            .contains(entry("109347263", List.of("box", "box", "pallet")), entry("123456891", null));
    }

    @Test
    void verifyEquality() {
        new EqualsTester()
            .addEqualityGroup(AggregatedResultBuilder.validResult(), AggregatedResultBuilder.validResult())
            .addEqualityGroup(new AggregatedResultBuilder()
                .pricing(Map.of("NL", new BigDecimal("14.242090605778")))
                .track(Map.of("123456891", "COLLECTING"))
                .shipments(Map.of("109347263", List.of("box", "box", "pallet"))))
            .testEquals();
    }

}
