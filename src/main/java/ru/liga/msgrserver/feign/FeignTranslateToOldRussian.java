package ru.liga.msgrserver.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "feignTranslateToOldRussian",
        url = "${feign.translate.url}")
public interface FeignTranslateToOldRussian {

    @GetMapping("translate")
    String getTranslatedText(@RequestParam("resource") String resource);

}
