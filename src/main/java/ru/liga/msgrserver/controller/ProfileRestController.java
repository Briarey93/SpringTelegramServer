package ru.liga.msgrserver.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.liga.msgrserver.data.LoveRelation;
import ru.liga.msgrserver.data.Profile;
import ru.liga.msgrserver.data.RowLoveRelation;
import ru.liga.msgrserver.data.RowProfile;
import ru.liga.msgrserver.feign.FeignTextToImg;
import ru.liga.msgrserver.feign.FeignTranslateToOldRussian;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/profiles")
public class ProfileRestController {
    /**
     * Ручка обращения к сервису перевод текста на древнерусский.
     */
    private final FeignTranslateToOldRussian feignTranslateToOldRussian;
    /**
     * Ручка обращения к сервису перевода текста на картинку.
     */
    private final FeignTextToImg feignTextToImg;

    /**
     * Класс агрегирующий пользователей в мапе.
     */
    private final RowProfile rowProfile;
    /**
     * Класс агрегирующий любовные предпочтения пользователей в мапе.
     */
    private final RowLoveRelation rowLoveRelation;

    @Autowired
    public ProfileRestController(FeignTranslateToOldRussian feignTranslateToOldRussian,
                                 FeignTextToImg feignTextToImg,
                                 RowProfile rowProfile,
                                 RowLoveRelation rowLoveRelation) {
        this.feignTranslateToOldRussian = feignTranslateToOldRussian;
        this.feignTextToImg = feignTextToImg;
        this.rowProfile = rowProfile;
        this.rowLoveRelation = rowLoveRelation;
        log.info("ProfileRestController created.");
    }

    /**
     * Получить всех пользователей.
     *
     * @return - список профилей всех пользователей
     */
    @GetMapping
    public List<Profile> getAllProfiles() {
        log.info("Get all profiles.");
        return new ArrayList<>(rowProfile.getRowsProfile().values());
    }

    /**
     * Получить все любовные предпочтения.
     *
     * @return - список всех любовных отношений в формате первому понравился второй
     */
    @GetMapping(value = "/relations")
    public List<LoveRelation> getAllLoveRelations() {
        log.info("Get all love relations.");
        return new ArrayList<>(rowLoveRelation.getRowsLoveRelations().values());
    }

    /**
     * Получить пользователя по идентификатору.
     *
     * @param id - идентификатор для поиска профиля
     * @return - возвращает профиль пользователя
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<byte[]> getByChatId(@PathVariable Long id) {
        log.info(String.format("Find by chatId(%s) method.", id));
        Profile profile = rowProfile.getProfileById(id);
        String responseText = MessageFormat.format("{0}, {1}.\n {2}", profile.getName(), profile.getGender(), profile.getDescription());
        String descriptionOnOldRussian = feignTranslateToOldRussian.getTranslatedText(responseText);
        return feignTextToImg.getImg(descriptionOnOldRussian);
    }

    /**
     * Создать нового пользователя.
     *
     * @param resource - профиль пользователя
     * @return - идентификатор созданного пользователя
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody Profile resource) {
        log.info(String.format("Create new profile method(ChatId: %s).", resource.getChatId()));
        return rowProfile.create(resource);
    }

    /**
     * Передать следующего пользователя к рассмотрению.
     *
     * @param id - идентификатор соискателя
     * @return - картинка следующего пользователя
     */
    @GetMapping(value = "/search/next/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getSearchById(@PathVariable Long id) {
        Profile profile = rowProfile.getSearchById(id);
        log.info(String.format("Get search id for %s.Next searched id is:%s", id, profile.getChatId()));
        String descriptionOnOldRussian = feignTranslateToOldRussian.getTranslatedText(profile.getDescription());
        return feignTextToImg.getImg(descriptionOnOldRussian);
    }

    /**
     * Пользователь по идентификационному номеру полюбит другого пользователя, на которого сейчас обращено поле "search"
     * Запрос следующего пользователя к рассмотрению(@see getSearchById).
     *
     * @param id - идентификатор соискателя
     * @return - картинка следующего пользователя
     */
    @GetMapping(value = "/search/like/{id}")
    public ResponseEntity<byte[]> likeByChatId(@PathVariable Long id) {
        Long searchId = rowProfile.getSearchId(id);
        log.info(String.format("Like(%s) by chat id(%s)", searchId, id));
        LoveRelation loveRelation = new LoveRelation(id, searchId);
        rowLoveRelation.addLoveData(loveRelation);
        return getSearchById(id);
    }


//    @GetMapping(value = "/lovers/next/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
//    public ResponseEntity<byte[]> getNextLoversById(@PathVariable Long id) {
//        log.info(String.format("Get love by id(%s) method", id));
//        Long loversId = rowProfile.getLoveId(id);
////        rowProfile.incrementLoverId(id);
//
//        Profile profile = rowProfile.getProfileById(loversId);
//        String descriptionOnOldRussian = feignTranslateToOldRussian.getTranslatedText(profile.getDescription());
//        return feignTextToImg.getImg(descriptionOnOldRussian);
//    }
//
//    @GetMapping(value = "/lovers/prev/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
//    public ResponseEntity<byte[]> getPrevLoversById(@PathVariable Long id) {
//        log.info(String.format("Get loved(%s) by id(%s) method", rowProfile.getProfileById(id).getLovers(), id));
//        Long loversId = rowProfile.getLoveId(id);
////        rowProfile.decrementLoverId(id);
//
//        Profile profile = rowProfile.getProfileById(loversId);
//        String descriptionOnOldRussian = feignTranslateToOldRussian.getTranslatedText(profile.getDescription());
//        return feignTextToImg.getImg(descriptionOnOldRussian);
//    }
}
