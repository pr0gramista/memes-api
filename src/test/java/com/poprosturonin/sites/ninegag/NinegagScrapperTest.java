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
public class NinegagScrapperTest {

    private static String CHARSET = "UTF-8";

    private static Document testDocument;

    @Autowired
    private NinegagScrapper ninegagScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocument = Jsoup.parse(new File(NinegagScrapperTest.class
                .getClassLoader()
                .getResource("sites/9gag.html")
                .toURI()), CHARSET);
    }

    @Test(expected = PageIsEmptyException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        ninegagScrapper.parse(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = ninegagScrapper.parse(testDocument);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertTrue(page.getNextPage() != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = ninegagScrapper.parse(testDocument);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalTo("I see no difference")),
                        hasProperty("url", equalTo("http://9gag.com/gag/aAdPmE9")),
                        hasProperty("comments", is(156)),
                        hasProperty("points", is(4030)),
                        hasProperty("content", hasProperty("url", equalTo("https://img-9gag-fun.9cache.com/photo/aAdPmE9_460s.jpg")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("This thing is like.. really dangerous!")),
                        hasProperty("url", equalTo("http://9gag.com/gag/arbNy9y")),
                        hasProperty("comments", is(248)),
                        hasProperty("points", is(5317)),
                        hasProperty("content", hasProperty("url", equalTo("https://img-9gag-fun.9cache.com/photo/arbNy9y_460s.jpg")))
                )
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public NinegagScrapper getNinegagScrapper() {
            return new NinegagScrapper();
        }
    }
}
