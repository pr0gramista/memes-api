package com.poprosturonin.sites.jbzd;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.contents.ContentType;
import com.poprosturonin.exceptions.CouldNotParseMemeException;
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for jbzd scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class JbzdSingleMemeScrapperTest {

    private static String CHARSET = "UTF-8";
    /**
     * This page contains single image meme
     */
    private static Document testDocumentImage;
    /**
     * This page contains single video meme
     */
    private static Document testDocumentVideo;

    @Autowired
    private JbzdSingleMemeScrapper jbzdSingleMemeScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocumentImage = Jsoup.parse(new File(JbzdSingleMemeScrapperTest.class.
                getClassLoader()
                .getResource("sites/jbzd_single_image.html")
                .toURI()), CHARSET);
        testDocumentVideo = Jsoup.parse(new File(JbzdSingleMemeScrapperTest.class
                .getClassLoader()
                .getResource("sites/jbzd_single_video.html")
                .toURI()), CHARSET);
    }

    @Test(expected = CouldNotParseMemeException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        jbzdSingleMemeScrapper.parseMeme(document);
    }

    @Test
    public void parsesVideoMemeProperly() throws Exception {
        Optional<Meme> memeOptional = jbzdSingleMemeScrapper.parseMeme(testDocumentVideo);

        assertTrue(memeOptional.isPresent());

        Meme meme = memeOptional.get();

        assertNotNull(meme);

        assertThat(meme, allOf(
                hasProperty("title", equalToIgnoringWhiteSpace("Duch jest wśród nas!")),
                hasProperty("url", equalToIgnoringWhiteSpace("https://jbzdy.pl/obr/608551/duch-jest-wsrod-nas")),
                hasProperty("viewUrl", equalToIgnoringWhiteSpace("/jbzd/608551")),
                // hasProperty("commentAmount", is(0)), Jbzd has dynamic comments
                hasProperty("points", is(140)),
                hasProperty("content",
                        allOf(
                                hasProperty("url", equalTo("https://i1.jbzdy.pl/contents/2017/09/232c1721e99e657c0e33acd950ad2e71.mp4")),
                                hasProperty("contentType", equalTo(ContentType.VIDEO))
                        )),
                hasProperty("author", allOf(
                        hasProperty("name", equalTo("SchoolShooter")),
                        hasProperty("profileUrl", equalTo("https://jbzdy.pl/uzytkownik/SchoolShooter"))
                ))
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void parsesImageMemeProperly() throws Exception {
        Optional<Meme> memeOptional = jbzdSingleMemeScrapper.parseMeme(testDocumentImage);

        assertTrue(memeOptional.isPresent());

        Meme meme = memeOptional.get();

        assertNotNull(meme);

        assertThat(meme, allOf(
                hasProperty("title", equalToIgnoringWhiteSpace("Wróżka")),
                hasProperty("url", equalToIgnoringWhiteSpace("https://jbzdy.pl/obr/608757/wrozka")),
                hasProperty("viewUrl", equalToIgnoringWhiteSpace("/jbzd/608757")),
                hasProperty("points", is(136)),
                hasProperty("content",
                        allOf(
                                hasProperty("url", equalTo("https://i1.jbzdy.pl/contents/2017/09/e5151c9e7ce7f8414e4b755cfaf15507.jpg")),
                                hasProperty("contentType", equalTo(ContentType.IMAGE))
                        )),
                hasProperty("comments", hasItem(allOf(
                        hasProperty("content", equalToIgnoringWhiteSpace("co tam robi Matt deimon !")),
                        hasProperty("author", allOf(
                                hasProperty("name", equalTo("arek-arekk")),
                                hasProperty("profileUrl", equalTo("https://jbzdy.pl/uzytkownik/arek-arekk"))
                        )),
                        hasProperty("reply", equalTo(false))
                )))
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public JbzdSingleMemeScrapper getJbzdSingleMemeScrapper() {
            return new JbzdSingleMemeScrapper();
        }
    }
}
