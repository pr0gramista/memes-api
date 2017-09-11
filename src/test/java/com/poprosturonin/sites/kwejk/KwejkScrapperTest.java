package com.poprosturonin.sites.kwejk;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.Content;
import com.poprosturonin.data.contents.GalleryContent;
import com.poprosturonin.data.contents.ImageContent;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
public class KwejkScrapperTest {
    private static String CHARSET = "UTF-8";
    /**
     * This site contains 1 gallery and 7 images
     */
    private static Document testDocument;
    /**
     * This page contains 2 videos, 1 gallery and 5 images
     */
    private static Document testDocument2;

    @Autowired
    private KwejkScrapper kwejkScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testDocument = Jsoup.parse(new File(KwejkScrapperTest.class.
                getClassLoader()
                .getResource("sites/kwejk.html")
                .toURI()), CHARSET);
        testDocument2 = Jsoup.parse(new File(KwejkScrapperTest.class
                .getClassLoader()
                .getResource("sites/kwejk2.html")
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
    public void numberOfMemesMatch() throws Exception {
        Page page = kwejkScrapper.parsePage(testDocument2);

        List<Content> contentList = page.getMemes()
                .stream()
                .map(Meme::getContent)
                .collect(Collectors.toList());

        assertEquals(8, contentList.size());

        List<GalleryContent> galleries = contentList
                .stream()
                .filter(content -> content instanceof GalleryContent)
                .map(content -> (GalleryContent) content)
                .collect(Collectors.toList());

        assertEquals(1, galleries.size());
        assertEquals(15, galleries.get(0).getUrls().size());

        List<VideoContent> videos = contentList
                .stream()
                .filter(content -> content instanceof VideoContent)
                .map(content -> (VideoContent) content)
                .collect(Collectors.toList());

        assertEquals(2, videos.size());

        List<ImageContent> images = contentList
                .stream()
                .filter(content -> content instanceof ImageContent)
                .map(content -> (ImageContent) content)
                .collect(Collectors.toList());

        assertEquals(5, images.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void gotMemesProperly() throws Exception {
        Page page = kwejkScrapper.parsePage(testDocument);

        assertThat(page.getMemes(), hasItems(
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("IMPREZA :)")),
                        hasProperty("url", equalTo("http://kwejk.pl/obrazek/2803669/impreza.html")),
                        hasProperty("commentAmount", is(1)),
                        hasProperty("points", is(96)),
                        hasProperty("content", hasProperty("url", equalTo("http://i1.kwejk.pl/k/obrazki/2016/10/nX5hTD3AjRz0vEoKkH4CZ5GzBy0VyCao.jpg")))
                ),
                allOf(
                        hasProperty("title", equalToIgnoringWhiteSpace("NIGDY NIE MA TWOJEGO TATY")),
                        hasProperty("url", equalTo("http://kwejk.pl/obrazek/2803727/nigdy-nie-ma-twojego-taty.html")),
                        hasProperty("commentAmount", is(6)),
                        hasProperty("points", is(64)),
                        hasProperty("content", hasProperty("url", equalTo("http://i1.kwejk.pl/k/obrazki/2016/10/782db6afb8715ca8d43da021749cd212.jpg")))
                )
        ));
    }

    @Test
    public void isGalleryPresentAndOk() throws Exception {
        Page page = kwejkScrapper.parsePage(testDocument);

        Optional<Meme> galleryOptional = page.getMemes()
                .stream()
                .filter(meme -> meme.getContent() instanceof GalleryContent)
                .findFirst();

        assertTrue(galleryOptional.isPresent());
        Meme gallery = galleryOptional.get();
        GalleryContent galleryContent = (GalleryContent) gallery.getContent();

        assertEquals("Ludzie, którzy mieli zły dzień (22 zdjęć)", gallery.getTitle());
        assertEquals(22, galleryContent.getUrls().size());
    }

    @Configuration
    static class Config {
        @Bean
        public KwejkScrapper getKwejkScrapper() {
            return new KwejkScrapper();
        }
    }
}
