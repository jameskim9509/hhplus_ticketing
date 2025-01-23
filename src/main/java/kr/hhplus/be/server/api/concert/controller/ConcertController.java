package kr.hhplus.be.server.api.concert.controller;

import io.swagger.v3.oas.annotations.Operation;
import kr.hhplus.be.server.api.concert.application.ConcertUsecase;
import kr.hhplus.be.server.api.concert.dto.GetAvailableConcertRequest;
import kr.hhplus.be.server.api.concert.dto.GetAvailableConcertsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {
    private final ConcertUsecase concertUsecase;

    @Operation(description = "기간 내 존재하는 콘서트를 조회합니다.")
    @GetMapping
    public GetAvailableConcertsResponse getAvailableConcert(
            @ModelAttribute GetAvailableConcertRequest getAvailableConcertRequest
    )
    {
        return GetAvailableConcertsResponse.from(
                concertUsecase.getAvailableConcerts(
                        getAvailableConcertRequest.startDate(),
                        getAvailableConcertRequest.endDate()
                )
        );
    }
}
