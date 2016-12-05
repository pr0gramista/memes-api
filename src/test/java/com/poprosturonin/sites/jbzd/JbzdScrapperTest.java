package com.poprosturonin.sites.jbzd;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.VideoContent;
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
import java.util.Optional;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.*;

/**
 * Tests for Jbzd scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JbzdScrapperTest {
    private static String CHARSET = "UTF-8";

    /**
     * This site contains 1 video and 7 images
     */
    private static Document testDocument;

    @Autowired
    private JbzdScrapper jbzdScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocument = Jsoup.parse(new File(JbzdScrapperTest.class.
                getClassLoader()
                .getResource("sites/jbzd.html")
                .toURI()), CHARSET);
    }

    @Test(expected = PageIsEmptyException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        jbzdScrapper.parse(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = jbzdScrapper.parse(testDocument);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertTrue(page.getNextPage() != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = jbzdScrapper.parse(testDocument);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("chyba nie")),
                        hasProperty("url", equalTo("http://jbzd.pl/obr/213493/chyba-nie")),
                        hasProperty("points", is(77)),
                        hasProperty("content", hasProperty("url", equalTo("http://img.jbzd.pl/2014/09/794d99be99c1c069cb8e783b844586ba.jpg")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("Piękny drift")),
                        hasProperty("url", equalTo("http://jbzd.pl/obr/213466/piekny-drift")),
                        hasProperty("points", is(103)),
                        hasProperty("content", hasProperty("url", equalTo("http://img.jbzd.pl/2014/09/2681846cf39b5ed534392b755f1d752d.gif")))
                )
        ));
    }

    @Test
    public void isVideoPresentAndOk() throws Exception {
        Page page = jbzdScrapper.parse(testDocument);

        Optional<Meme> videoOptional = page.getMemes()
                .stream()
                .filter(meme -> meme.getContent() instanceof VideoContent)
                .findFirst();

        assertTrue(videoOptional.isPresent());
        Meme video = videoOptional.get();
        VideoContent videoContent = (VideoContent) video.getContent();

        assertEquals("Więcej gazu!", video.getTitle());
        assertEquals("http://jbzd.pl/obr/213469/wiecej-gazu", video.getUrl());
        assertEquals("http://img.jbzd.pl/2014/09/c10ccf20af5dd03fca6ecf3d8fab4e0a.webm", videoContent.getUrl());
    }

    @Configuration
    static class Config {
        @Bean
        public JbzdScrapper getJbzdScrapper() {
            return new JbzdScrapper();
        }
    }
}
