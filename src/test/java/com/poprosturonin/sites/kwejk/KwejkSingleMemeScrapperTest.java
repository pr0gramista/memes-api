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
 * Tests for kwejk single meme scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class KwejkSingleMemeScrapperTest {

    private static String CHARSET = "UTF-8";
    /**
     * This page contains single image meme
     */
    private static Document testDocumentImage;

    @Autowired
    private KwejkSingleMemeScrapper kwejkSingleMemeScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocumentImage = Jsoup.parse(new File(KwejkSingleMemeScrapper.class.
                getClassLoader()
                .getResource("sites/kwejk_single.html")
                .toURI()), CHARSET);
    }

    @Test(expected = CouldNotParseMemeException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        kwejkSingleMemeScrapper.parseMeme(document);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void parsesImageMemeProperly() throws Exception {
        Optional<Meme> memeOptional = kwejkSingleMemeScrapper.parseMeme(testDocumentImage);

        assertTrue(memeOptional.isPresent());

        Meme meme = memeOptional.get();

        assertNotNull(meme);

        assertThat(meme, allOf(
                hasProperty("title", equalToIgnoringWhiteSpace("A co z twoim chłopakiem?")),
                hasProperty("url", equalToIgnoringWhiteSpace("https://kwejk.pl/obrazek/3175539/a-co-z-twoim-chlopakiem.html")),
                hasProperty("points", is(224)),
                hasProperty("commentAmount", is(129)),
                hasProperty("content",
                        allOf(
                                hasProperty("url", equalTo("https://i1.kwejk.pl/k/obrazki/2018/04/BpFFWYFoMhJLrArm.jpg")),
                                hasProperty("contentType", equalTo(ContentType.IMAGE))
                        )),
                hasProperty("comments", hasItem(
                        allOf(
                                hasProperty("content", equalToIgnoringWhiteSpace("jak większość kobiet")),
                                hasProperty("author", allOf(
                                        hasProperty("name", equalTo("Kontaktron")),
                                        hasProperty("profileUrl", equalTo("https://kwejk.pl/uzytkownik/kontaktron"))
                                )),
                                hasProperty("responses", hasItem(
                                        allOf(
                                                hasProperty("content", equalToIgnoringWhiteSpace("@kontaktron czemu ?")),
                                                hasProperty("author", allOf(
                                                        hasProperty("name", equalTo("Anonimowy")),
                                                        hasProperty("profileUrl", equalTo(""))
                                                ))
                                        )
                                ))
                        )
                )),
                hasProperty("tags", hasItems(
                        allOf(
                                hasProperty("name", equalTo("humor")),
                                hasProperty("sourceUrl", equalTo("https://kwejk.pl/tag/humor")),
                                hasProperty("slug", equalTo("humor"))
                        ),
                        allOf(
                                hasProperty("name", equalToIgnoringWhiteSpace("klasycznememy")),
                                hasProperty("sourceUrl", equalTo("https://kwejk.pl/tag/klasycznememy")),
                                hasProperty("slug", equalToIgnoringWhiteSpace("klasycznememy"))
                        )
                ))
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
