package com.javaops.voting.controller;

import com.javaops.voting.model.Restaurant;
import com.javaops.voting.repository.DishRepository;
import com.javaops.voting.util.RestaurantUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.javaops.voting.View;
import com.javaops.voting.service.RestaurantService;
import com.javaops.voting.to.RestaurantTo;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.javaops.voting.util.ValidationUtil.assureIdConsistent;
import static com.javaops.voting.util.ValidationUtil.checkNew;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class RestaurantController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DishRepository dishRepository;

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService, DishRepository dishRepository) {
        this.restaurantService = restaurantService;
        this.dishRepository = dishRepository;
    }

    @GetMapping("/menus")
    public List<RestaurantTo> getAllMenus() {
        log.info("getAllMenus");
        return RestaurantUtil.getTos(getAll(), dishRepository.getByDate(LocalDate.now()));
    }

    @GetMapping("/admin/restaurants")
    public List<Restaurant> getAll() {
        log.info("getAll");
        return restaurantService.getAll();
    }

    @GetMapping("/admin/restaurants/{id}")
    public Restaurant get(@PathVariable int id) {
        log.info("get {}", id);
        return restaurantService.get(id);
    }

    @PostMapping(value = "/admin/restaurants", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> create(@Validated(View.Web.class) @RequestBody Restaurant restaurant) {
        log.info("create {}", restaurant);
        checkNew(restaurant);
        Restaurant created = restaurantService.create(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/restaurants/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/admin/restaurants/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        restaurantService.delete(id);
    }

    @PutMapping(value = "/admin/restaurants/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(View.Web.class) @RequestBody Restaurant restaurant, @PathVariable int id) {
        log.info("update {} with id={}", restaurant, id);
        assureIdConsistent(restaurant, id);
        restaurantService.update(restaurant, id);
    }
}
