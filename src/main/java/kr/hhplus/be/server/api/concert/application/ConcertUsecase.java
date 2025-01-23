package kr.hhplus.be.server.api.concert.application;

import kr.hhplus.be.server.api.concert.dto.GetAvailableConcertsResponse;
import kr.hhplus.be.server.domain.concert.Concert;

import java.time.LocalDate;
import java.util.List;

public interface ConcertUsecase {
    public List<Concert> getAvailableConcerts(LocalDate startDate, LocalDate endDate);
}
