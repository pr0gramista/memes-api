package com.poprosturonin.sites.demotywatory;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.CaptionedGalleryContent;
import com.poprosturonin.data.contents.Content;
import com.poprosturonin.data.contents.GIFContent;
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
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests for demotywatory scrapper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class DemotywatoryScrapperTest {

    private static String CHARSET = "UTF-8";
    /**
     * This site contains 3 galleries and 7 images
     */
    private static Document testFile;
    /**
     * This page contains 1 video, 1 gif, no galleries and 8 images
     */
    private static Document testFile2;
    @Autowired
    private DemotywatoryScrapper demotywatoryScrapper;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        testFile = Jsoup.parse(new File(DemotywatoryScrapperTest.class.
                getClassLoader()
                .getResource("sites/demotywatory.html")
                .toURI()), CHARSET);
        testFile2 = Jsoup.parse(new File(DemotywatoryScrapperTest.class
                .getClassLoader()
                .getResource("sites/demotywatory2.html")
                .toURI()), CHARSET);
    }

    @Test(expected = PageIsEmptyException.class)
    public void pageIsEmptyExceptionWasCalled() throws Exception {
        Document document = new Document("test");
        demotywatoryScrapper.parse(document);
    }

    @Test
    public void parsesOk() throws Exception {
        Page page = demotywatoryScrapper.parse(testFile);

        assertNotNull(page);
        assertFalse(page.isEmpty());
        assertTrue(page.getMemes().size() > 0);
        assertTrue(page.getNextPage() != null);
    }

    @Test
    public void gotVideoAndGif() throws Exception {
        Page page = demotywatoryScrapper.parse(testFile2);

        List<Content> contents = page.getMemes().stream()
                .map(Meme::getContent)
                .collect(Collectors.toList());

        //GIF
        List<GIFContent> gifContents = contents.stream()
                .filter(content -> content instanceof GIFContent)
                .map(content -> (GIFContent) content)
                .collect(Collectors.toList());

        assertEquals(1, gifContents.size());
        assertTrue(gifContents.get(0).getUrl().endsWith(".gif"));

        //Video
        List<VideoContent> videoContents = contents.stream()
                .filter(content -> content instanceof VideoContent)
                .map(content -> (VideoContent) content)
                .collect(Collectors.toList());

        assertEquals(1, videoContents.size());
        assertTrue(videoContents.get(0).getUrl().endsWith(".mp4"));
    }

    @Test
    public void isGalleryPresentAndOk() throws Exception {
        Page page = demotywatoryScrapper.parse(testFile);

        List<Meme> memes = page.getMemes()
                .stream()
                .filter(meme -> meme.getContent() instanceof CaptionedGalleryContent)
                .collect(Collectors.toList());

        assertEquals(3, memes.size()); //There are 3 galleries on this site

        List<CaptionedGalleryContent> galleries = memes.stream()
                .map(meme -> (CaptionedGalleryContent) meme.getContent())
                .collect(Collectors.toList());

        assertEquals(1, galleries.stream().filter(galleryContent -> galleryContent.getImages().size() == 21).count());
        assertEquals(1, galleries.stream().filter(galleryContent -> galleryContent.getImages().size() == 8).count());
        assertEquals(1, galleries.stream().filter(galleryContent -> galleryContent.getImages().size() == 18).count());
    }

    @Configuration
    static class Config {
        @Bean
        public DemotywatoryScrapper getDemotywatoryScrappery() {
            return new DemotywatoryScrapper();
        }
    }
}
