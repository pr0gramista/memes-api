package com.poprosturonin.sites.kwejk;

import com.poprosturonin.utils.URLUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kwejk tests
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class KwejkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnJson() throws Exception {
        mockMvc.perform(get("/kwejk/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("nextPage", matchesPattern(URLUtils.CUT_URL_PATTERN)));
        mockMvc.perform(get("/kwejk/3201709"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void shouldReturnEmptyPage() throws Exception {
        mockMvc.perform(get("/kwejk/page/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturnNoMeme() throws Exception {
        mockMvc.perform(get("/kwejk/-1"))
                .andExpect(status().isNotFound());
    }
}