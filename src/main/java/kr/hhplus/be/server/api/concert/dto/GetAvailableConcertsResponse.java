package kr.hhplus.be.server.api.concert.dto;

import kr.hhplus.be.server.domain.concert.Concert;

import java.util.List;

public record GetAvailableConcertsResponse(AvailableConcertDtoList availableConcerts) {
    public static GetAvailableConcertsResponse from(AvailableConcertDtoList concertList)
    {
        return new GetAvailableConcertsResponse(concertList);
    }
}
