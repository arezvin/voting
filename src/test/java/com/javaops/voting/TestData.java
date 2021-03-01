package com.javaops.voting;

import com.javaops.voting.model.*;
import com.javaops.voting.to.RestaurantTo;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static com.javaops.voting.model.AbstractBaseEntity.START_SEQ;

public class TestData {
    public static TestMatcher<User> USER_MATCHER = TestMatcher.usingFieldsComparator(User.class, "registered", "password");
    public static TestMatcher<Restaurant> RESTAURANT_MATCHER = TestMatcher.usingFieldsComparator(Restaurant.class, "dishes");
    public static TestMatcher<Dish> DISH_MATCHER = TestMatcher.usingFieldsComparator(Dish.class, "restaurant");
    public static TestMatcher<Vote> VOTE_MATCHER = TestMatcher.usingFieldsComparator(Vote.class, "user", "restaurant");
    public static TestMatcher<RestaurantTo> RESTAURANT_TO_MATCHER = TestMatcher.usingFieldsComparator(RestaurantTo.class, "menu.restaurant");

    public static final int NOT_FOUND = 10;
    public static final int USER_ID = START_SEQ;
    public static final int ADMIN_ID = START_SEQ + 1;

    public static final User USER = new User(USER_ID, "User", "user@gmail.com", "user", Role.USER);
    public static final User ADMIN = new User(ADMIN_ID, "Admin", "admin@gmail.com", "admin", Role.ADMIN, Role.USER);

    public static final int RESTAURANT1_ID = START_SEQ + 2;
    public static final int RESTAURANT2_ID = START_SEQ + 3;

    public static final Restaurant RESTAURANT1 = new Restaurant(RESTAURANT1_ID, "Континенталь");
    public static final Restaurant RESTAURANT2 = new Restaurant(RESTAURANT2_ID, "Кушай хорошо");

    public static final int DISH1_ID = START_SEQ + 4;
    public static final int DISH2_ID = START_SEQ + 5;

    public static final Dish DISH1 = new Dish(DISH1_ID, "Куличики", 15.5, LocalDate.of(2020, Month.APRIL, 4));
    public static final Dish DISH2 = new Dish(DISH2_ID, "Холодец", 30d, LocalDate.of(2020, Month.APRIL, 4));
    public static final Dish DISH3 = new Dish(START_SEQ + 6, "Окрошка", 31.1, LocalDate.of(2020, Month.APRIL, 4));
    public static final Dish DISH4 = new Dish(START_SEQ + 7, "Пюре", 23.4, LocalDate.of(2020, Month.MAY, 4));
    public static final Dish DISH5 = new Dish(START_SEQ + 8, "Крабовье", 27d, LocalDate.of(2020, Month.MAY, 4));
    public static final Dish DISH6 = new Dish(START_SEQ + 9, "Борщь", 28.7, LocalDate.of(2020, Month.MAY, 4));
    public static final Dish DISH7 = new Dish(START_SEQ + 10, "Оливье", 20.2, LocalDate.now());
    public static final Dish DISH8 = new Dish(START_SEQ + 11, "Макароны по флотски", 25d, LocalDate.now());
    public static final Dish DISH9 = new Dish(START_SEQ + 12, "Россольник", 28.2, LocalDate.now());
    public static final Dish DISH10 = new Dish(START_SEQ + 13, "Карпачо", 35d, LocalDate.now());
    public static final Dish DISH11 = new Dish(START_SEQ + 14, "Фуагра", 40.4, LocalDate.now());
    public static final Dish DISH12 = new Dish(START_SEQ + 15, "Карпачо", 38.7, LocalDate.now());
    public static final Dish DISH13 = new Dish(START_SEQ + 16, "Лазанья", 45d, LocalDate.now());

    public static final List<Dish> DISHES = List.of(DISH1, DISH2, DISH3, DISH4, DISH5, DISH6, DISH7, DISH8, DISH9, DISH10, DISH11, DISH12, DISH13);

    public static final RestaurantTo RESTAURANT1_TO = new RestaurantTo(RESTAURANT1_ID, RESTAURANT1.getName(), List.of(DISH7, DISH8, DISH9, DISH10));
    public static final RestaurantTo RESTAURANT2_TO = new RestaurantTo(RESTAURANT2_ID, RESTAURANT2.getName(), List.of(DISH11, DISH12, DISH13));

    public static final int VOTE1_ID = START_SEQ + 17;

    public static final Vote VOTE1 = new Vote(VOTE1_ID, LocalDate.of(2020, Month.APRIL, 4), USER_ID, RESTAURANT2_ID);
    public static final Vote VOTE2 = new Vote(START_SEQ + 18, LocalDate.of(2020, Month.APRIL, 4), ADMIN_ID, RESTAURANT2_ID);
    public static final Vote VOTE3 = new Vote(START_SEQ + 19, LocalDate.of(2020, Month.APRIL, 5), USER_ID, RESTAURANT1_ID);
    public static final Vote VOTE4 = new Vote(START_SEQ + 20, LocalDate.of(2020, Month.APRIL, 5), ADMIN_ID, RESTAURANT2_ID);

    public static final List<Vote> VOTES = List.of(VOTE1, VOTE2, VOTE3, VOTE4);

    public static Restaurant getNewRestaurant() {
        return new Restaurant(null, "NewRestaurant");
    }

    public static Restaurant getUpdatedRestaurant() {
        Restaurant updated = new Restaurant(RESTAURANT1_ID, RESTAURANT1.getName());
        updated.setName("UpdatedName");
        return updated;
    }

    public static Dish getNewDish() {
        return new Dish(null, "newDish", 3.4, LocalDate.now());
    }

    public static Dish getUpdatedDish() {
        Dish updated =  new Dish(DISH1_ID, DISH1.getName(), DISH1.getPrice(), DISH1.getDate());
        updated.setName("updatedDish");
        updated.setPrice(4.5);
        return updated;
    }

    public static Vote getNewVote() {
        return new Vote(null, LocalDate.now(), USER_ID, RESTAURANT1_ID);
    }

    public static User getClonedUser() {
        return new User(USER_ID, USER.getName(), USER.getEmail(), USER.getPassword(), USER.isEnabled(), USER.getRoles());
    }
}
