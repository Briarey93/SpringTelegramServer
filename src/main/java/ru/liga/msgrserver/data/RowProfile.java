package ru.liga.msgrserver.data;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Data
public class RowProfile {

    private Map<Long, Profile> rowsProfile = new HashMap() {{
        put(0L, new Profile(0L,
                "первый",
                "-",
                "тупо текст",
                "-",
                0L,
                0L));
        put(1L, new Profile(1L,
                "Dima",
                "Сударь",
                "описание анкеты, лол",
                "Сударыня",
                0L,
                0L));
        put(3L, new Profile(3L,
                "Lena",
                "Сударыня",
                "описание анкеты второго пользователя, лол",
                "Сударь",
                0L,
                0L));
    }};

    public Long create(Profile resource) {
        rowsProfile.put(resource.getChatId(), resource.clone());
        return resource.getChatId();
    }

    public Profile getProfileById(Long searchId) {
        return rowsProfile.get(searchId);
    }

    public Long getSearchId(Long id) {
        return rowsProfile.get(id).getSearch();
    }

    public void incrementSearchId(Long id) {
        List<Object> Ids = Arrays.stream(rowsProfile.keySet().toArray()).toList();
        Long currentSearchId = getSearchId(id);
        int index = Ids.indexOf(currentSearchId);

        if (++index == rowsProfile.keySet().size()){
            rowsProfile.get(id).setSearch(0L);
            return;
        }

        Long nextSearchId = (Long) Ids.get(index);
        if(nextSearchId.equals(id)){
            if (++index == rowsProfile.keySet().size()){
                rowsProfile.get(id).setSearch(0L);
                return;
            }
            nextSearchId = (Long) Ids.get(index);
        }
        rowsProfile.get(id).setSearch(nextSearchId);
    }

    public Long getLoveId(Long id) {
        return rowsProfile.get(id).getLovers();
    }
}
