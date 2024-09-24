package com.dti.multiwarehouse.order.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ShippingCostResponseDto {

    @JsonProperty("rajaongkir")
    private RajaOngkir rajaOngkir;

    @Data
    public static class RajaOngkir {
        private Query query;
        private Status status;
        private CityDetails originDetails;
        private CityDetails destinationDetails;
        private List<Result> results;

        @Data
        public static class Query {
            private String origin;
            private String destination;
            private int weight;
            private String courier;
        }

        @Data
        public static class Status {
            private int code;
            private String description;
        }

        @Data
        public static class CityDetails {
            @JsonProperty("city_id")
            private String cityId;
            private String province;
            @JsonProperty("province_id")
            private String provinceId;
            private String type;
            @JsonProperty("city_name")
            private String cityName;
            @JsonProperty("postal_code")
            private String postalCode;
        }

        @Data
        public static class Result {
            private String code;
            private String name;
            private List<Cost> costs;

            @Data
            public static class Cost {
                private String service;
                private String description;
                private List<CostDetail> cost;

                @Data
                public static class CostDetail {
                    private int value;
                    private String etd;
                    private String note;
                }
            }
        }
    }
}