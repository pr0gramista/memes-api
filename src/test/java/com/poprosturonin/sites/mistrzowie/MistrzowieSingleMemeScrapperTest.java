package com.poprosturonin.sites.mistrzowie;

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
 * Tests for mistrzowie single meme scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MistrzowieSingleMemeScrapperTest {

    private static String CHARSET = "UTF-8";
    /**
     * This page contains single image meme
     */
    private static Document testDocumentImage;

    @Autowired
    private MistrzowieSingleMemeScrapper mistrzowieSingleMemeScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocumentImage = Jsoup.parse(new File(MistrzowieSingleMemeScrapper.class.
                getClassLoader()
                .getResource("sites/mistrzowie_single.html")
                .toURI()), CHARSET);
    }

    @Test(expected = CouldNotParseMemeException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        mistrzowieSingleMemeScrapper.parseMeme(document);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void parsesImageMemeProperly() throws Exception {
        Optional<Meme> memeOptional = mistrzowieSingleMemeScrapper.parseMeme(testDocumentImage);

        assertTrue(memeOptional.isPresent());

        Meme meme = memeOptional.get();

        assertNotNull(meme);

        assertThat(meme, allOf(
                hasProperty("title", equalToIgnoringWhiteSpace("Test")),
                hasProperty("url", equalToIgnoringWhiteSpace("http://mistrzowie.org/715370")),
                hasProperty("points", is(107)),
                hasProperty("author", allOf(
                        hasProperty("name", equalToIgnoringCase("GreenRanger")),
                        hasProperty("profileUrl", equalToIgnoringCase("http://mistrzowie.org/user/GreenRanger"))
                )),
                hasProperty("content",
                        allOf(
                                hasProperty("url", equalTo("http://mistrzowie.org/uimages/services/mistrzowie/i18n/pl_PL/201804/1522688009_by_GreenRanger.jpg")),
                                hasProperty("contentType", equalTo(ContentType.IMAGE))
                        )
                ),
                hasProperty("comments", hasItem(
                        allOf(
                                hasProperty("content", equalToIgnoringWhiteSpace("Sprzedam test na HIV pozytywny, zrob zart dziewczynie ;D")),
                                hasProperty("author", allOf(
                                        hasProperty("name", equalTo("Niepowiemkto")),
                                        hasProperty("profileUrl", equalTo("http://mistrzowie.org/user/Niepowiemkto"))
                                )),
                                hasProperty("responses", hasItem(
                                        allOf(
                                                hasProperty("content", equalToIgnoringWhiteSpace("@SOBEKK: od twojej starej")),
                                                hasProperty("author", allOf(
                                                        hasProperty("name", equalTo("Niepowiemkto")),
                                                        hasProperty("profileUrl", equalTo("http://mistrzowie.org/user/Niepowiemkto"))
                                                ))
                                        )
                                ))
                        )
                ))
        ));
    }

    @Configuration
    static class Config {
        @Bean
        public MistrzowieSingleMemeScrapper getMistrzowieSingleMemeScrapper() {
            return new MistrzowieSingleMemeScrapper();
        }
    }
}
