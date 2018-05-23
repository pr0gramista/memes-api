package com.poprosturonin.sites.thecodinglove;

import com.poprosturonin.data.Page;
import com.poprosturonin.exceptions.PageIsEmptyException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.*;

/**
 * Tests for the coding love scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TheCodingLovePageScrapperTest {

    private static String CHARSET = "UTF-8";
    private static Document testDocument;

    @Autowired
    private TheCodingLovePageScrapper theCodingLoveScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocument = Jsoup.parse(new File(TheCodingLovePageScrapperTest.class
                .getClassLoader()
                .getResource("sites/thecodinglove.html")
                .toURI()), CHARSET);
    }

    @Test(expected = PageIsEmptyException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        theCodingLoveScrapper.parsePage(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = theCodingLoveScrapper.parsePage(testDocument);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertTrue(page.getNextPage() != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = theCodingLoveScrapper.parsePage(testDocument);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("When I discover that I can earn cash with an open source contest")),
                        hasProperty("url", equalTo("https://thecodinglove.com/when-i-discover-that-i-can-earn-cash-with-an-open-source-contest")),
                        hasProperty("content", hasProperty("url", equalTo("https://ljdchost.com/ZOVBzyn.gif")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("When I’m searching for a command in my Linux history")),
                        hasProperty("url", equalTo("https://thecodinglove.com/when-im-searching-for-a-command-in-my-linux-history")),
                        hasProperty("content", hasProperty("url", equalTo("https://ljdchost.com/KwzJ3D8.gif")))
                )
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public TheCodingLovePageScrapper getTheCodingLoveScrapper() {
            return new TheCodingLovePageScrapper();
        }
    }
}
