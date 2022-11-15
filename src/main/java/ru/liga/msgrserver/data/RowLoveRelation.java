package ru.liga.msgrserver.data;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Data
public class RowLoveRelation {

    private static Long count = 0L;
    private Map<Long, LoveRelation> rowsLoveRelations = new HashMap<>();


    public void addLoveData(LoveRelation loveRelation) {
        List<LoveRelation> loveRelations = rowsLoveRelations.values().stream().toList();
        for (LoveRelation love : loveRelations) {
            if (love.getChatIdLove().equals(loveRelation.getChatIdLove()) &&
                    love.getChatIdLoved().equals(loveRelation.getChatIdLoved())) {
                return;
            }
        }
        rowsLoveRelations.put(count++, loveRelation);
    }

    public Long getNextLoverId(Long id, Long loverId) {
        rowsLoveRelations.get(id);
        return id;
    }

    public Long getPrevLoverId(Long id, Long loverId) {
        return id;
    }
}
