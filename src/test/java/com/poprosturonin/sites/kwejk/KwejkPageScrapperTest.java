package com.poprosturonin.sites.kwejk;

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
 * Tests for kwejk scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class KwejkPageScrapperTest {

    private static String CHARSET = "UTF-8";

    private static Document testDocument;

    @Autowired
    private KwejkPageScrapper kwejkScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocument = Jsoup.parse(new File(KwejkPageScrapperTest.class
                .getClassLoader()
                .getResource("sites/kwejk.html")
                .toURI()), CHARSET);
    }

    @Test(expected = PageIsEmptyException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        kwejkScrapper.parsePage(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = kwejkScrapper.parsePage(testDocument);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertTrue(page.getNextPage() != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = kwejkScrapper.parsePage(testDocument);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("miał farta")),
                        hasProperty("url", equalTo("https://kwejk.pl/obrazek/3143697/mial-farta.html")),
                        hasProperty("commentAmount", is(5)),
                        hasProperty("points", is(73)),
                        hasProperty("content", hasProperty("url", equalTo("https://i1.kwejk.pl/k/obrazki/2018/02/lqXPRFvJzmDzSmdI.jpg")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("Patologia w Polsce")),
                        hasProperty("url", equalTo("https://kwejk.pl/obrazek/3143691/patologia-w-polsce.html")),
                        hasProperty("commentAmount", is(5)),
                        hasProperty("points", is(125)),
                        hasProperty("content", hasProperty("url", equalTo("https://i1.kwejk.pl/k/obrazki/2018/02/xyPmsaVrZZ8sVXbr.jpg")))
                )
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public KwejkPageScrapper getKwejkScrapper() {
            return new KwejkPageScrapper();
        }
    }
}
