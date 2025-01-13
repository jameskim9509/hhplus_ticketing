package kr.hhplus.be.server.api.concert.dto;

import kr.hhplus.be.server.domain.concert.Concert;

import java.util.List;

public record GetAvailableConcertsResponse(List<Concert> availableConcerts) {
    public static GetAvailableConcertsResponse from(List<Concert> concertList)
    {
        return new GetAvailableConcertsResponse(concertList);
    }
}
