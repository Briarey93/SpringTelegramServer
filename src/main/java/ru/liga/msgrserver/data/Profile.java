package ru.liga.msgrserver.data;

import lombok.Data;

@Data
public class Profile implements Cloneable {
    private Long chatId;

    private String name;

    private String gender;

    private String description;

    private String genderSearch;

    private Long search;

    private Long lovers;

    public Profile(Long chatId, String name, String gender, String description, String genderSearch, Long search, Long lovers) {
        this.chatId = chatId; //ПК
        this.name = name;
        this.gender = gender;
        this.description = description;
        this.genderSearch = genderSearch;
        this.search = search; //0 chatId->next
        this.lovers = lovers; //0
    }

    public Profile() {
    }

    public Profile clone() {
        Profile profile = new Profile(this.getChatId(),
                this.getName(),
                this.getGender(),
                this.getDescription(),
                this.getGenderSearch(),
                this.getSearch(),
                this.getLovers());
        return profile;
    }
}