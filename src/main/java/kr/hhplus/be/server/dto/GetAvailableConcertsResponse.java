package kr.hhplus.be.server.dto;

import kr.hhplus.be.server.domain.concert.Concert;

import java.util.List;

public record GetAvailableConcertsResponse(List<Concert> availableConcerts) {
}
