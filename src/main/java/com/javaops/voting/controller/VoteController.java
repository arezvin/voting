package com.javaops.voting.controller;

import com.javaops.voting.AuthorizedUser;
import com.javaops.voting.model.Vote;
import com.javaops.voting.repository.UserRepository;
import com.javaops.voting.repository.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.javaops.voting.repository.RestaurantRepository;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static com.javaops.voting.util.TimeUtil.checkTime;
import static com.javaops.voting.util.ValidationUtil.*;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class VoteController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final VoteRepository voteRepository;

    private final UserRepository userRepository;

    private final RestaurantRepository restaurantRepository;

    public VoteController(VoteRepository voteRepository, UserRepository userRepository,
                          RestaurantRepository restaurantRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/admin/votes")
    public List<Vote> getAll() {
        log.info("getAll");
        return voteRepository.findAll();
    }

    @PostMapping(value = "/votes/{restaurantId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Vote> create(@Valid @RequestBody Vote vote, @PathVariable int restaurantId,
                                       @AuthenticationPrincipal AuthorizedUser authUser) {
        Assert.notNull(vote, "vote must not be null");
        checkTime();
        checkNew(vote);
        int userId = authUser.getId();
        Vote updated = voteRepository.getByDate(LocalDate.now(), userId);
        if(updated == null) {
            log.info("create {}", vote);
            vote.setUser(userRepository.getOne(userId));
            vote.setUserId(userId);
            vote.setRestaurant(restaurantRepository.getOne(restaurantId));
            vote.setRestaurantId(restaurantId);
            Vote created = voteRepository.save(vote);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/votes/{id}")
                    .buildAndExpand(created.getId()).toUri();
            return ResponseEntity.created(uriOfNewResource).body(created);
        } else {
            updated.setRestaurant(restaurantRepository.getOne(restaurantId));
            update(updated, updated.getId());
            return ResponseEntity.noContent().build();
        }
    }

    private void update(@Valid @RequestBody Vote vote, int id) {
        log.info("update {} with id={}", vote, id);
        Assert.notNull(vote, "vote must not be null");
        assureIdConsistent(vote, id);
        checkNotFoundWithId(voteRepository.save(vote), vote.id());
    }
}
