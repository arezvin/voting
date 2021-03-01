package com.javaops.voting.controller;

import com.javaops.voting.model.Dish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.javaops.voting.View;
import com.javaops.voting.repository.DishRepository;
import com.javaops.voting.repository.RestaurantRepository;

import java.net.URI;
import java.util.List;

import static com.javaops.voting.util.ValidationUtil.*;

@RestController
@RequestMapping(value = "/admin/dishes", produces = MediaType.APPLICATION_JSON_VALUE)
public class DishController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final DishRepository dishRepository;

    private final RestaurantRepository restaurantRepository;

    public DishController(DishRepository dishRepository, RestaurantRepository restaurantRepository) {
        this.dishRepository = dishRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping()
    public List<Dish> getAll() {
        log.info("getAll");
        return dishRepository.findAll();
    }

    @GetMapping("/{id}")
    public Dish get(@PathVariable int id) {
        log.info("get {}", id);
        return checkNotFoundWithId(dishRepository.findById(id).orElse(null), id);
    }

    @PostMapping(value = "/{restaurantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> create(@Validated(View.Web.class) @RequestBody Dish dish, @PathVariable int restaurantId) {
        log.info("create {}", dish);
        Assert.notNull(dish, "dish must not be null");
        checkNew(dish);
        dish.setRestaurant(restaurantRepository.getOne(restaurantId));
        Dish created = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/dishes/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        log.info("delete {}", id);
        checkNotFoundWithId(dishRepository.delete(id) != 0, id);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void update(@Validated(View.Web.class) @RequestBody Dish dish, @PathVariable int id) {
        log.info("update {} with id={}", dish, id);
        Assert.notNull(dish, "dish must not be null");
        assureIdConsistent(dish, id);
        dish.setRestaurant(restaurantRepository.getOne(get(id).getRestaurant().getId()));
        checkNotFoundWithId(dishRepository.save(dish), dish.id());
    }
}
