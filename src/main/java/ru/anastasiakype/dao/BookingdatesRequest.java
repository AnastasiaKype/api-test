package ru.anastasiakype.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@With

public class BookingdatesRequest {

        @Setter
        @Getter

        @JsonProperty("checkin")
        public String checkin;
        @JsonProperty("checkout")
        public String checkout;

    }



