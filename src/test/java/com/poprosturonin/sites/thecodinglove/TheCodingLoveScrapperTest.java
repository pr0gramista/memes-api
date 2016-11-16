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
public class TheCodingLoveScrapperTest {

    private static String CHARSET = "UTF-8";
    private static Document testFile;

    @Autowired
    private TheCodingLoveScrapper theCodingLoveScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testFile = Jsoup.parse(new File(TheCodingLoveScrapperTest.class
                .getClassLoader()
                .getResource("sites/thecodinglove.html")
                .toURI()), CHARSET);
    }

    @Test(expected = PageIsEmptyException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        theCodingLoveScrapper.parse(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = theCodingLoveScrapper.parse(testFile);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertTrue(page.getNextPage() != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = theCodingLoveScrapper.parse(testFile);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("When a feature does more than expected")),
                        hasProperty("url", equalTo("http://thecodinglove.com/post/153260172278/when-a-feature-does-more-than-expected")),
                        hasProperty("content", hasProperty("url", equalTo("http://ljdchost.com/gOwS4tK.gif")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("When updating libraries fixes bugs")),
                        hasProperty("url", equalTo("http://thecodinglove.com/post/153257122542/when-updating-libraries-fixes-bugs")),
                        hasProperty("content", hasProperty("url", equalTo("http://ljdchost.com/k3Lrab8.gif")))
                )
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public TheCodingLoveScrapper getTheCodingLoveScrapper() {
            return new TheCodingLoveScrapper();
        }
    }
}
