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
        jbzdScrapper.parsePage(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = jbzdScrapper.parsePage(testDocument);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertTrue(page.getNextPage() != null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = jbzdScrapper.parsePage(testDocument);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("Idealnie")),
                        hasProperty("url", equalTo("http://jbzd.pl/obr/518889/idealnie")),
                        hasProperty("points", is(246)),
                        hasProperty("content", hasProperty("url", equalTo("http://i1.jbzd.pl/contents/2017/01/4e2cc4a9ac76b4fe7776dc28d935d3c7.gif")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("NFS")),
                        hasProperty("url", equalTo("http://jbzd.pl/obr/519096/nfs")),
                        hasProperty("points", is(199)),
                        hasProperty("content", hasProperty("url", equalTo("http://i1.jbzd.pl/contents/2017/01/6dde374abf0e009c738539117dc08de3.jpg")))
                )
        ));
    }

    @Test
    public void isVideoPresentAndOk() throws Exception {
        Page page = jbzdScrapper.parsePage(testDocument);

        Optional<Meme> videoOptional = page.getMemes()
                .stream()
                .filter(meme -> meme.getContent() instanceof VideoContent)
                .findFirst();

        assertTrue(videoOptional.isPresent());
        Meme video = videoOptional.get();
        VideoContent videoContent = (VideoContent) video.getContent();

        assertEquals("Jest moc", video.getTitle());
        assertEquals("http://jbzd.pl/obr/519066/jest-moc", video.getUrl());
        assertEquals("http://i1.jbzd.pl/contents/2017/01/6cc205fbab78f4f678b8a1f5096137da.mp4", videoContent.getUrl());
    }

    @Configuration
    static class Config {
        @Bean
        public JbzdScrapper getJbzdScrapper() {
            return new JbzdScrapper();
        }
    }
}
