package kr.hhplus.be.server.api.concert.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AvailableConcertDtoList
{
    private List<AvailableConcertDto> availableConcertDtoList;

    public static AvailableConcertDtoList from(
            List<AvailableConcertDto> availableConcertDtoList
    )
    {
        return new AvailableConcertDtoList(availableConcertDtoList);
    }
}
