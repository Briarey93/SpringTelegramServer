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
import java.util.Set;

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
    public Set<LoveRelation> getAllLoveRelations() {
        log.info("Get all love relations.");
        return rowLoveRelation.getRowsLoveRelations();
    }

    /**
     * Создать нового пользователя.
     *
     * @param resource - профиль пользователя
     * @return - идентификатор созданного пользователя
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<byte[]> create(@RequestBody Profile resource) {
        log.info(String.format("Create new profile(ChatId: %s).", resource.getChatId()));
        Long id = rowProfile.create(resource);
        return getResponseEntity(id);
    }

    /**
     * Получить пользователя по идентификатору.
     *
     * @param id - идентификатор для поиска профиля
     * @return - возвращает профиль пользователя
     */
    @GetMapping(value = "/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getByChatId(@PathVariable Long id) {
        if (rowProfile.isIdNotRegistered(id)) {
            log.info(String.format("User %s doesn't registered, yet.", id));
            return getResponseEntity("Вы еще не зарегистрированы. Пропишите /start для входа в регистрацию.");
        }

        log.info(String.format("Find profile by chatId(%s).", id));

        return getResponseEntity(id);
    }

    /**
     * Передать следующего пользователя к рассмотрению.
     *
     * @param id - идентификатор соискателя
     * @return - картинка следующего пользователя
     */
    @GetMapping(value = "/search/next/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getSearchById(@PathVariable Long id) {
        if (rowProfile.isIdNotRegistered(id)) {
            log.info(String.format("User %s doesn't registered, yet.", id));
            return getResponseEntity("Вы еще не зарегистрированы. Пропишите /start для входа в регистрацию.");
        }

        rowProfile.incrementSearchIdForId(id);
        Long searchId = rowProfile.getSearchIdById(id);
        log.info(String.format("Get search id for %s.Next searched id is:%s.", id, searchId));

        return getResponseEntity(searchId);
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
        if (rowProfile.isIdNotRegistered(id)) {
            log.info(String.format("User %s doesn't registered, yet.", id));
            return getResponseEntity("Вы еще не зарегистрированы. Пропишите /start для входа в регистрацию.");
        }

        Long searchId = rowProfile.getSearchIdById(id);
        log.info(String.format("Like(%s) by chat id(%s).", searchId, id));

        rowLoveRelation.addLoveData(new LoveRelation(id, searchId));
        return getSearchById(id);
    }

    @GetMapping(value = "/lovers/next/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getNextLoversById(@PathVariable Long id) {
        if (rowProfile.isIdNotRegistered(id)) {
            log.info(String.format("User %s doesn't registered, yet.", id));
            return getResponseEntity("Вы еще не зарегистрированы. Пропишите /start для входа в регистрацию.");
        }

        Long loversId = rowProfile.incrementedLoversIdForId(id);
        log.info(String.format("Get love next(%s) by id(%s).", loversId, id));

        String description = rowProfile.getLoversProfileDescription(loversId);
        return getResponseEntity(description);
    }

    @GetMapping(value = "/lovers/prev/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getPrevLoversById(@PathVariable Long id) {
        if (rowProfile.isIdNotRegistered(id)) {
            log.info(String.format("User %s doesn't registered, yet.", id));
            return getResponseEntity("Вы еще не зарегистрированы. Пропишите /start для входа в регистрацию.");
        }

        Long loversId = rowProfile.decrementedLoversIdForId(id);
        log.info(String.format("Get love prev(%s) by id(%s).", loversId, id));

        String description = rowProfile.getLoversProfileDescription(loversId);
        return getResponseEntity(description);
    }

    private ResponseEntity<byte[]> getResponseEntity(String responseText) {
        String descriptionOnOldRussian = feignTranslateToOldRussian.getTranslatedText(responseText);
        return feignTextToImg.getImg(descriptionOnOldRussian);
    }

    private ResponseEntity<byte[]> getResponseEntity(@PathVariable Long id) {
        Profile profile = rowProfile.getProfileById(id);
        String responseText;

        responseText = MessageFormat.format("{0}, {1}.\n {2}", profile.getName(), profile.getGender(), profile.getDescription());
        String descriptionOnOldRussian = feignTranslateToOldRussian.getTranslatedText(responseText);
        return feignTextToImg.getImg(descriptionOnOldRussian);
    }
}
