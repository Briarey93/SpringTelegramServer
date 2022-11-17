package ru.liga.msgrserver.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "text-to-image-service",
        url = "${feign.img.url}",
        decode404 = true)
public interface FeignTextToImg {

    @GetMapping(value = "internal/image/from/text/", produces = MediaType.IMAGE_JPEG_VALUE)
    ResponseEntity<byte[]> getImg(@RequestParam("description") String description);

}
