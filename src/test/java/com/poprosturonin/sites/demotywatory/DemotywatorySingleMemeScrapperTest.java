package com.poprosturonin.sites.demotywatory;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.contents.CaptionedGalleryContent;
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
 * Tests for demotywatory scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DemotywatorySingleMemeScrapperTest {

    private static String CHARSET = "UTF-8";
    /**
     * This site contains demot about bison crossing German border
     */
    private static Document testDocument;
    /**
     * This page contains gallery about video
     * production (examples from GoT), one slide (3rd, video) is actually omitted by now
     */
    private static Document testDocumentGallery;

    @Autowired
    private DemotywatorySingleMemeScrapper demotywatoryScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocument = Jsoup.parse(new File(DemotywatorySingleMemeScrapperTest.class.
                getClassLoader()
                .getResource("sites/demotywatory_single.html")
                .toURI()), CHARSET);
        testDocumentGallery = Jsoup.parse(new File(DemotywatorySingleMemeScrapperTest.class
                .getClassLoader()
                .getResource("sites/demotywatory_single_gallery.html")
                .toURI()), CHARSET);
    }

    @Test(expected = CouldNotParseMemeException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        demotywatoryScrapper.parseMeme(document);
    }

    @Test
    public void parsesMemeProperly() throws Exception {
        Optional<Meme> memeOptional = demotywatoryScrapper.parseMeme(testDocument);

        assertTrue(memeOptional.isPresent());

        Meme meme = memeOptional.get();

        assertNotNull(meme);

        assertThat(meme, allOf(
                hasProperty("title", equalToIgnoringWhiteSpace("Żubr z Polski zastrzelony w Niemczech w pierwszy dzień swojego pobytu tam")),
                hasProperty("commentAmount", is(35)),
                hasProperty("points", is(253)),
                hasProperty("content", hasProperty("url", equalTo("https://img3.demotywatoryfb.pl//uploads/201709/1505474817_f85fld_600.jpg"))),
                hasProperty("author", allOf(
                        hasProperty("name", equalTo("Quartermaster")),
                        hasProperty("profileUrl", equalTo("http://demotywatory.pl/user/Quartermaster"))
                )),
                hasProperty("comments", hasItem(allOf(
                        hasProperty("content", equalToIgnoringWhiteSpace("Żubr im, k#rwa, powodował zagrożenie, ale tabuny zdziczałych ciapaków, to już nie...")),
                        hasProperty("author", allOf(
                                hasProperty("name", equalTo("demitri")),
                                hasProperty("profileUrl", equalTo("http://demotywatory.pl/user/demitri"))
                        )),
                        hasProperty("responses", hasItem(
                                allOf(
                                        hasProperty("content", equalToIgnoringWhiteSpace("@demitri A wiesz, że o tym samym pomyślałem...")),
                                        hasProperty("author", allOf(
                                                hasProperty("name", equalTo("Iceman31")),
                                                hasProperty("profileUrl", equalTo("http://demotywatory.pl/user/Iceman31"))
                                        ))
                                )
                        ))
                )))
        ));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void parsesGalleryMemeProperly() throws Exception {
        Optional<Meme> memeOptional = demotywatoryScrapper.parseMeme(testDocumentGallery);

        assertTrue(memeOptional.isPresent());

        Meme meme = memeOptional.get();

        assertNotNull(meme);

        assertThat(meme, allOf(
                hasProperty("commentAmount", is(12)),
                hasProperty("points", is(105)),
                hasProperty("content", hasProperty("images", hasItems(
                        allOf(
                                hasProperty("url", equalTo("http://m.demotywatory.pl/uploads/201709/gallery_1505296848_148896.jpg")),
                                hasProperty("title", equalToIgnoringWhiteSpace("Aby aktorom było łatwiej, w ten sposób zastępowane są smoki")),
                                hasProperty("caption", equalTo(""))
                        ),
                        allOf(
                                hasProperty("url", equalTo("http://m.demotywatory.pl/uploads/201709/gallery_1505296853_860385.jpg")),
                                hasProperty("title", equalToIgnoringWhiteSpace("Kiedy smoki trochę podrosły zaczęto używać zielonego kija oraz poduszki")),
                                hasProperty("caption", equalTo(""))
                        )
                ))),
                hasProperty("comments", hasItem(allOf(
                        hasProperty("content", equalToIgnoringWhiteSpace("Są jeszcze w obecnych czasach megaprodukcje filmowe, które nie wykorzystują greenboxa i efektów specjalnych? Tak mi się wydaje, że autor galerii jest fanem tego serialu...")),
                        hasProperty("author", allOf(
                                hasProperty("name", equalTo("szteker")),
                                hasProperty("profileUrl", equalTo("http://demotywatory.pl/user/szteker"))
                        )),
                        hasProperty("responses", hasItem(
                                allOf(
                                        hasProperty("content", equalToIgnoringWhiteSpace("@szteker Są jeszcze w tych czasam megaprodukcje, które nie wykorzystują tych paskudnych kamer nagrywających również dźwięk? Plansze z tekstem, to prawdziwe kino, a nie żadne jakieś-tam gadanie...")),
                                        hasProperty("author", allOf(
                                                hasProperty("name", equalTo("~postęp_to_zło")),
                                                hasProperty("profileUrl", equalTo("http://demotywatory.pl/user/~postęp_to_zło"))
                                        ))
                                )
                        ))
                )))
        ));

        assertTrue(((CaptionedGalleryContent) meme.getContent()).getImages().size() == 11);
    }

    @Configuration
    static class Config {
        @Bean
        public DemotywatorySingleMemeScrapper getDemotywatoryScrapper() {
            return new DemotywatorySingleMemeScrapper();
        }
    }
}
