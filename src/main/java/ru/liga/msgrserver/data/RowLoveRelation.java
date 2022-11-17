package ru.liga.msgrserver.data;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Data
public class RowLoveRelation {

    private Set<LoveRelation> rowsLoveRelations = new HashSet<>();


    public void addLoveData(LoveRelation loveRelation) {
        rowsLoveRelations.add(loveRelation);
    }

    public Long getNextLoverId(Long id, Long loverId) {
        List<LoveRelation> rowsLoveRelationsById = new ArrayList<>();
        int indexLoverId = generateListLoveRelationsByIs_and_getIndexOfLoverId(id, loverId, rowsLoveRelationsById);
        if (rowsLoveRelationsById.size() == indexLoverId + 1) {
            return rowsLoveRelationsById.get(0).getChatIdLoved();
        }

        return rowsLoveRelationsById.get(indexLoverId + 1).getChatIdLoved();
    }

    public Long getPrevLoverId(Long id, Long loverId) {
        List<LoveRelation> rowsLoveRelationsById = new ArrayList<>();
        int indexLoverId = generateListLoveRelationsByIs_and_getIndexOfLoverId(id, loverId, rowsLoveRelationsById);
        if (0 == indexLoverId) {
            return rowsLoveRelationsById.get(rowsLoveRelationsById.size() - 1).getChatIdLoved();
        }

        return rowsLoveRelationsById.get(indexLoverId - 1).getChatIdLoved();
    }

    private int generateListLoveRelationsByIs_and_getIndexOfLoverId(Long id, Long loverId, List<LoveRelation> rowsLoveRelationsById) {
        int indexLoverId = 0;
        for (LoveRelation loveRelation : rowsLoveRelations) {
            if (loveRelation.getChatIdLove().equals(id)) {
                if (loveRelation.getChatIdLoved().equals(loverId)) {
                    indexLoverId = rowsLoveRelationsById.size();
                }
                rowsLoveRelationsById.add(loveRelation);
            }
        }
        return indexLoverId;
    }

    public boolean isIdHasNoRelations(Long id) {
        for (LoveRelation loveRelation : rowsLoveRelations) {
            if (loveRelation.getChatIdLove().equals(id))
                return false;
        }
        return true;
    }
}
