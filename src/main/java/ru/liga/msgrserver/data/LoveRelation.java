package ru.liga.msgrserver.data;

import lombok.Data;

@Data
public class LoveRelation {

    private Long chatIdLove;
    private Long chatIdLoved;

    public LoveRelation(Long chatIdLove, Long chatIdLoved) {
        this.chatIdLove = chatIdLove;
        this.chatIdLoved = chatIdLoved;
    }
}
