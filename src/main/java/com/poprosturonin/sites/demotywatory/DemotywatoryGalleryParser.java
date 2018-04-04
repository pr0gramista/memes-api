package com.poprosturonin.sites.demotywatory;

import com.poprosturonin.data.contents.CaptionedGalleryContent;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.poprosturonin.sites.demotywatory.DemotywatoryController.ROOT_URL;

/**
 * Demotywatory changed their galleries to be so complicated that
 * we need a separate parser for it
 */
class DemotywatoryGalleryParser {
    Pattern pattern = Pattern.compile("galleryPics = \\[\"(.+)\"\\];");

    public List<CaptionedGalleryContent.CaptionedGallerySlide> parse(Element demot) {
        List<CaptionedGalleryContent.CaptionedGallerySlide> slides = new ArrayList<>(25);

        Elements scripts = demot.getElementsByTag("script");
        for (Element script : scripts) {
            Matcher matcher = pattern.matcher(script.html());
            if (matcher.find()) {
                String listContent = matcher.group(1);

                String[] slideElementsHTML = listContent.split("\",\"");
                for (String slideElementHTML : slideElementsHTML) {
                    slideElementHTML = StringEscapeUtils.unescapeJava(slideElementHTML);
                    Document slideElement = Jsoup.parse(slideElementHTML);
                    slides.add(new CaptionedGalleryContent.CaptionedGallerySlide(
                            ROOT_URL + slideElement.select("img.rsImg").attr("src"),
                            slideElement.getElementsByTag("h3").text(),
                            slideElement.getElementsByTag("p").text()
                    ));
                }
            }
        }

        // Remove false slides
        slides = slides.stream()
                .filter(captionedGallerySlide -> !captionedGallerySlide.getUrl().endsWith("demotywatory.pl"))
                .collect(Collectors.toList());

        return slides;
    }
}
