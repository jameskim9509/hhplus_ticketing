package kr.hhplus.be.server.api.seat.dto;

import lombok.Getter;

import java.time.LocalDate;

public record GetAvailableSeatRequest(LocalDate date, String uuid) {
    public GetAvailableSeatRequest(LocalDate date, String uuid)
    {
        validate(date, uuid);
        this.date = date;
        this.uuid = uuid;
    }

    private void validate(LocalDate date, String uuid)
    {
        if(date == null || uuid == null) throw new RuntimeException("");
        if(date.isBefore(LocalDate.now())) throw new RuntimeException("");
    }
}
