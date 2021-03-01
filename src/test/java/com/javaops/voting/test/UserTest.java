package com.javaops.voting.test;

import com.javaops.voting.AuthorizedUser;
import com.javaops.voting.controller.ProfileController;
import com.javaops.voting.model.User;
import com.javaops.voting.model.Vote;
import com.javaops.voting.repository.VoteRepository;
import com.javaops.voting.to.UserTo;
import com.javaops.voting.util.TimeUtil;
import com.javaops.voting.util.UserUtil;
import com.javaops.voting.util.exception.NotFoundException;
import com.javaops.voting.util.json.JsonUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.javaops.voting.TestData.*;
import static com.javaops.voting.TestUtil.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserTest extends AbstractTest {

    @Autowired
    ProfileController profileController;

    @Autowired
    VoteRepository voteRepository;

    @Test
    public void getMenu() throws Exception {
        perform(MockMvcRequestBuilders.get("/menus")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(RESTAURANT_TO_MATCHER.contentJson(RESTAURANT1_TO, RESTAURANT2_TO));
    }

    @Test
    public void getMenuUnAuth() throws Exception {
        perform(MockMvcRequestBuilders.get("/menu"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void vote() throws Exception {
        TimeUtil.hourLimitForVote = 23;
        Vote newVote = getNewVote();
        ResultActions action = perform(MockMvcRequestBuilders.post("/votes/" + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(newVote)))
                .andExpect(status().isCreated());

        Vote created = readFromJson(action, Vote.class);
        int newId = created.id();
        newVote.setId(newId);
        VOTE_MATCHER.assertMatch(created, newVote);
        VOTE_MATCHER.assertMatch(voteRepository.findById(newId).orElse(null), newVote);
        TimeUtil.hourLimitForVote = 10;
    }

    @Test
    public void voteSecondTimePerDay() throws Exception {
        TimeUtil.hourLimitForVote = 23;
        Vote newVote = getNewVote();
        ResultActions action = perform(MockMvcRequestBuilders.post("/votes/" + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(newVote)))
                .andExpect(status().isCreated());

        perform(MockMvcRequestBuilders.post("/votes/" + RESTAURANT2_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(newVote)))
                .andExpect(status().isNoContent());

        Vote created = readFromJson(action, Vote.class);
        Vote updated = voteRepository.findById(created.id()).get();
        Assert.assertEquals(updated.getRestaurant().getId().intValue(), RESTAURANT2_ID);
        TimeUtil.hourLimitForVote = 10;
    }

    @Test
    public void lateToVote() throws Exception {
        TimeUtil.hourLimitForVote = 0;
        perform(MockMvcRequestBuilders.post("/votes/" + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(getNewVote())))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        TimeUtil.hourLimitForVote = 10;
    }

    @Test
    public void voteInvalid() throws Exception {
        TimeUtil.hourLimitForVote = 23;
        Vote invalid = getNewVote();
        invalid.setDate(null);
        perform(MockMvcRequestBuilders.post("/votes/" + RESTAURANT1_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(invalid)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
        TimeUtil.hourLimitForVote = 10;
    }

    @Test
    public void getProfile() throws Exception {
        perform(MockMvcRequestBuilders.get("/profile")
                .with(userHttpBasic(USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(USER_MATCHER.contentJson(USER));
    }

    @Test
    public void deleteProfile() throws Exception {
        perform(MockMvcRequestBuilders.delete("/profile")
                .with(userHttpBasic(USER)))
                .andExpect(status().isNoContent());
        Assert.assertThrows(NotFoundException.class, () -> profileController.get(new AuthorizedUser(USER)));
    }

    @Test
    public void register() throws Exception {
        UserTo newTo = new UserTo(null, "newName", "newemail@gmail.com", "newPassword");
        User newUser = UserUtil.createNewFromTo(newTo);
        ResultActions action = perform(MockMvcRequestBuilders.post("/profile/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isCreated());

        User created = readFromJson(action, User.class);
        int newId = created.getId();
        newUser.setId(newId);
        USER_MATCHER.assertMatch(created, newUser);
        USER_MATCHER.assertMatch(profileController.get(new AuthorizedUser(newUser)), newUser);
    }

    @Test
    public void registerHtmlUnsafe() throws Exception {
        UserTo newTo = new UserTo(null, "newName", "<script>alert(123)</script>", "newPassword");
        perform(MockMvcRequestBuilders.post("/profile/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.writeValue(newTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void updateProfile() throws Exception {
        UserTo updatedTo = new UserTo(null, "newName", "newemail@gmail.com", "newPassword");
        perform(MockMvcRequestBuilders.put("/profile").contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isNoContent());

        USER_MATCHER.assertMatch(profileController.get(new AuthorizedUser(USER)),
                UserUtil.updateFromTo(getClonedUser(), updatedTo));
    }

    @Test
    public void updateInvalid() throws Exception {
        UserTo updatedTo = new UserTo(null, null, "password", null);
        perform(MockMvcRequestBuilders.put("/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .with(userHttpBasic(USER))
                .content(JsonUtil.writeValue(updatedTo)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }
}
