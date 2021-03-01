package com.javaops.voting.controller;

import com.javaops.voting.AuthorizedUser;
import com.javaops.voting.View;
import com.javaops.voting.model.User;
import com.javaops.voting.service.UserService;
import com.javaops.voting.to.UserTo;
import com.javaops.voting.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static com.javaops.voting.util.ValidationUtil.assureIdConsistent;
import static com.javaops.voting.util.ValidationUtil.checkNew;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(@AuthenticationPrincipal AuthorizedUser authUser) {
        int id = authUser.getId();
        log.info("get {}", id);
        return userService.get(id);
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<User> register(@Validated(View.Web.class) @RequestBody UserTo userTo) {
        log.info("create {}", userTo);
        checkNew(userTo);
        User created = userService.create(UserUtil.createNewFromTo(userTo));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/profile").build().toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal AuthorizedUser authUser) {
        int id = authUser.getId();
        log.info("delete {}", id);
        userService.delete(id);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@Validated(View.Web.class) @RequestBody UserTo userTo, @AuthenticationPrincipal AuthorizedUser authUser) {
        int id = authUser.getId();
        log.info("update {} with id={}", userTo, id);
        assureIdConsistent(userTo, id);
        userService.update(userTo);
    }
}
