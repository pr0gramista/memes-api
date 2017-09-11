package com.poprosturonin.sites.mistrzowie;

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
 * Tests for mistrzowie scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MistrzowieScrapperTest {

    private static String CHARSET = "UTF-8";

    private static Document testDocument;

    @Autowired
    private MistrzowieScrapper mistrzowieScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocument = Jsoup.parse(new File(MistrzowieScrapperTest.class
                .getClassLoader()
                .getResource("sites/mistrzowie.html")
                .toURI()), CHARSET);
    }

    @Test(expected = PageIsEmptyException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        mistrzowieScrapper.parsePage(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = mistrzowieScrapper.parsePage(testDocument);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertTrue(page.getNextPage() != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = mistrzowieScrapper.parsePage(testDocument);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("Kim jest?")),
                        hasProperty("url", equalTo("http://mistrzowie.org/679961/Kim-jest")),
                        hasProperty("commentAmount", is(3)),
                        hasProperty("points", is(233)),
                        hasProperty("content", hasProperty("url", equalTo("http://mistrzowie.org/uimages/services/mistrzowie/i18n/pl_PL/201611/1479071055_by_Nightstalker.jpg?1479071055")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("Satyrycy oglądają youtuberów")),
                        hasProperty("url", equalTo("http://mistrzowie.org/679963/Satyrycy-ogladaja-youtuberow")),
                        hasProperty("commentAmount", is(3)),
                        hasProperty("points", is(216)),
                        hasProperty("content", hasProperty("url", equalTo("http://mistrzowie.org/uimages/services/mistrzowie/i18n/pl_PL/201611/1479071999_by_bartekkk1.jpg?1479071999")))
                )
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public MistrzowieScrapper getMistrzowieScrapper() {
            return new MistrzowieScrapper();
        }
    }
}
