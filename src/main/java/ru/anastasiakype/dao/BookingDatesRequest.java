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
@Setter
@Getter
public class BookingDatesRequest {



        @JsonProperty("checkin")
        public String checkin;
        @JsonProperty("checkout")
        public String checkout;

    }



