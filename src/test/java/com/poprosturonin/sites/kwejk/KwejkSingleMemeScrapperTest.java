package com.poprosturonin.sites.kwejk;

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
public class KwejkSingleMemeScrapperTest {

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
    private KwejkSingleMemeScrapper kwejkSingleMemeScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocumentImage = Jsoup.parse(new File(KwejkSingleMemeScrapperTest.class.
                getClassLoader()
                .getResource("sites/kwejk_single_image.html")
                .toURI()), CHARSET);
        testDocumentVideo = Jsoup.parse(new File(KwejkSingleMemeScrapperTest.class
                .getClassLoader()
                .getResource("sites/kwejk_single_video.html")
                .toURI()), CHARSET);
    }

    @Test(expected = CouldNotParseMemeException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        kwejkSingleMemeScrapper.parseMeme(document);
    }

    @Test
    public void parsesVideoMemeProperly() throws Exception {
        Optional<Meme> memeOptional = kwejkSingleMemeScrapper.parseMeme(testDocumentVideo);

        assertTrue(memeOptional.isPresent());

        Meme meme = memeOptional.get();

        assertNotNull(meme);

        assertThat(meme, allOf(
                hasProperty("title", equalToIgnoringWhiteSpace("Hula hop")),
                hasProperty("url", equalToIgnoringWhiteSpace("/3039657/hula-hop.html")),
                // hasProperty("commentAmount", is(0)),  has dynamic comments
                hasProperty("points", is(130)),
                hasProperty("content",
                        allOf(
                                hasProperty("url", equalTo("https://i1.kwejk.pl/k/obrazki/2017/09/c229d277a9c17105ac484858c116886a.mp4")),
                                hasProperty("contentType", equalTo(ContentType.VIDEO))
                        )),
                hasProperty("author", allOf(
                        hasProperty("name", equalToIgnoringWhiteSpace("Ellhuir")),
                        hasProperty("profileUrl", equalTo("https://kwejk.pl/ups/Ellhuir"))
                ))
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void parsesImageMemeProperly() throws Exception {
        Optional<Meme> memeOptional = kwejkSingleMemeScrapper.parseMeme(testDocumentImage);

        assertTrue(memeOptional.isPresent());

        Meme meme = memeOptional.get();

        assertNotNull(meme);

        assertThat(meme, allOf(
                hasProperty("title", equalToIgnoringWhiteSpace("Ciemność widzę")),
                hasProperty("url", equalToIgnoringWhiteSpace("https://kwejk.pl/obrazek/3041505/ciemnosc-widze.html")),
                hasProperty("viewUrl", equalToIgnoringWhiteSpace("/kwejk/3041505")),
                hasProperty("points", is(115)),
                hasProperty("content",
                        allOf(
                                hasProperty("url", equalTo("https://i1.kwejk.pl/k/obrazki/2017/09/0cbf559cb4558e5c3f88288378192e00.jpg")),
                                hasProperty("contentType", equalTo(ContentType.IMAGE))
                        )),
                hasProperty("comments", hasItem(allOf(
                        hasProperty("content", equalToIgnoringWhiteSpace("coś ciemno to widzę...")),
                        hasProperty("author", allOf(
                                hasProperty("name", equalTo("mrsheenck")),
                                hasProperty("profileUrl", equalTo("https://kwejk.pl/ups/mrsheenck"))
                        )),
                        hasProperty("reply", equalTo(false))
                )))
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public KwejkSingleMemeScrapper getKwejkSingleMemeScrapper() {
            return new KwejkSingleMemeScrapper();
        }
    }
}
