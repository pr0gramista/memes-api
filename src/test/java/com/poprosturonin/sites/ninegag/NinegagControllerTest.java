package com.poprosturonin.sites.ninegag;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Pattern;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 9gag tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class NinegagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Pattern good9GAGURLPattern = Pattern.compile("^/9gag/([a-zA-Z0-9%]+)$");

    @Test
    public void shouldReturnJson() throws Exception {
        mockMvc.perform(get("/9gag/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("nextPage", matchesPattern(good9GAGURLPattern)));
        mockMvc.perform(get("/9gag/a3qj6Z8%2CamYrqy6%2CaWm8Ob6"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("nextPage", matchesPattern(good9GAGURLPattern)));
    }

    @Test
    public void shouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/9gag/-1"))
                .andExpect(status().isNotFound());
    }
}