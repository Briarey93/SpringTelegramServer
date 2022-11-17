package ru.liga.msgrserver.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
@Slf4j
public class RowProfile {

    /**
     * Класс агрегирующий любовные предпочтения пользователей в мапе.
     */
    private final RowLoveRelation rowLoveRelation;

    private Map<Long, Profile> rowsProfile = new HashMap() {{
        put(0L, new Profile(0L,
                "Anton",
                "Сударь",
                "Первый пользователь",
                "все",
                0L,
                0L));
        put(1L, new Profile(1L,
                "Dima",
                "Сударь",
                "Второй пользователь",
                "Сударыня",
                0L,
                0L));
        put(2L, new Profile(2L,
                "Anna",
                "Сударыня",
                "Третий пользователь",
                "Сударь",
                0L,
                0L));
        put(4L, new Profile(4L,
                "Lena",
                "Сударыня",
                "Четвертый пользователь",
                "Сударь",
                0L,
                0L));
        put(5L, new Profile(5L,
                "Lena",
                "Сударыня",
                "Пятый пользователь",
                "все",
                0L,
                0L));
        put(6L, new Profile(6L,
                "Иполит",
                "Сударь",
                "Шестой пользователь",
                "все",
                0L,
                0L));
    }};

    public RowProfile(@Autowired RowLoveRelation rowLoveRelation) {
        this.rowLoveRelation = rowLoveRelation;
    }

    public Long create(Profile resource) {
        rowsProfile.put(resource.getChatId(), resource.clone());
        return resource.getChatId();
    }

    public Profile getProfileById(Long searchId) {
        return rowsProfile.get(searchId);
    }

    public Long getSearchIdById(Long id) {
        return rowsProfile.get(id).getSearch();
    }

    public Long getLoverIdById(Long id) {
        return rowsProfile.get(id).getLovers();
    }


    public boolean isIdNotRegistered(Long id) {
        return !rowsProfile.containsKey(id);

    }

    private void setLoverIdById(Long id, Long loverId) {
        getProfileById(id).setLovers(loverId);
    }

    public String getLoversProfileDescription(Long loversId) {
        if (loversId == null) {
            return "Вы еще не выбрали любимцев. Вернитесь в Меню->Поиск и выберите любимцев.";
        }
        Profile profile = getProfileById(loversId);
        return MessageFormat.format("{0}, {1}.\n {2}", profile.getName(), profile.getGender(), profile.getDescription());
    }

    public void incrementSearchIdForId(Long id) {
        List<Object> Ids = Arrays.stream(rowsProfile.keySet().toArray()).toList();
        Long currentSearchId = getSearchIdById(id);
        int index = Ids.indexOf(currentSearchId);

        if (++index == rowsProfile.keySet().size()) {
            rowsProfile.get(id).setSearch(0L);
            return;
        }

        Long nextSearchId = (Long) Ids.get(index);
        if (nextSearchId.equals(id)) {
            if (++index == rowsProfile.keySet().size()) {
                rowsProfile.get(id).setSearch(0L);
                return;
            }
            nextSearchId = (Long) Ids.get(index);
        }
        rowsProfile.get(id).setSearch(nextSearchId);
    }

    public Long incrementedLoversIdForId(Long id) {
        if (rowLoveRelation.isIdHasNoRelations(id)) {
            return null;
        }
        Long loverId = rowLoveRelation.getNextLoverId(id, getLoverIdById(id));
        setLoverIdById(id, loverId);

        return loverId;
    }

    public Long decrementedLoversIdForId(Long id) {
        if (rowLoveRelation.isIdHasNoRelations(id)) {
            return null;
        }
        Long loverId = rowLoveRelation.getPrevLoverId(id, getLoverIdById(id));
        setLoverIdById(id, loverId);

        return loverId;
    }
}
