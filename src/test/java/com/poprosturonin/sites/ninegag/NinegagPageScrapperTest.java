package com.poprosturonin.sites.ninegag;

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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.*;

/**
 * Tests for 9gag scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class NinegagPageScrapperTest {

    private static String CHARSET = "UTF-8";

    private static Document testDocument;

    @Autowired
    private NinegagPageScrapper ninegagScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocument = Jsoup.parse(new File(NinegagPageScrapperTest.class
                .getClassLoader()
                .getResource("sites/9gag.html")
                .toURI()), CHARSET);
    }

    @Test(expected = PageIsEmptyException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        ninegagScrapper.parsePage(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = ninegagScrapper.parsePage(testDocument);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertNotNull(page.getNextPage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = ninegagScrapper.parsePage(testDocument);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalTo("Thomas the trick engine")),
                        hasProperty("url", equalTo("https://9gag.com/gag/ax0rwjD")),
                        hasProperty("commentAmount", is(92)),
                        hasProperty("points", is(4019)),
                        hasProperty("content", hasProperty("url", equalTo("https://img-9gag-fun.9cache.com/photo/ax0rwjD_460sv.mp4")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("Please rage")),
                        hasProperty("url", equalTo("https://9gag.com/gag/a1K6BPP")),
                        hasProperty("commentAmount", is(302)),
                        hasProperty("points", is(3922)),
                        hasProperty("content", hasProperty("url", equalTo("https://img-9gag-fun.9cache.com/photo/a1K6BPP_460s.jpg")))
                )
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public NinegagPageScrapper getNinegagScrapper() {
            return new NinegagPageScrapper();
        }
    }
}
