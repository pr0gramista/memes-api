package com.poprosturonin.sites.ninegag;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.Content;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.data.contents.PreviewContent;
import com.poprosturonin.data.contents.VideoContent;
import com.poprosturonin.exceptions.MemeSiteResponseFailedException;
import com.poprosturonin.exceptions.PageIsEmptyException;
import com.poprosturonin.sites.PageScrapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Mistrzowie scrapper
 */
@Component
public class NinegagPageScrapper implements PageScrapper {
    /**
     * Pattern for url
     * fe. /?id=arbN9e0%2CajXDj41%2CagLGjgw&c=20&
     * group 1 is parameter id
     * group 2 is parameter c
     */
    private Pattern urlPattern = Pattern.compile("^/\\?id=(.+)&c=(\\d+)");

    public String getJSON(String url) {
        try {
            URL downloadURL = new URL(url);
            URLConnection connection = downloadURL.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read);

            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new MemeSiteResponseFailedException();
        }
    }

    public String getHTML(JSONObject response) {
        JSONArray ids = response.getJSONArray("ids");
        JSONObject items = response.getJSONObject("items");

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < ids.length(); i++) {
            String id = ids.getString(i);
            String html = items.getString(id);
            stringBuilder.append(html);
        }
        //How can we preserve information about next page? Let's inject it into html. lol.
        stringBuilder.append("<a id=\"nextpage\" href=\"");
        stringBuilder.append(response.getString("loadMoreUrl"));
        stringBuilder.append("\" />");
        return stringBuilder.toString();
    }

    @Override
    public Page scrapPage(String url) {
        return parsePage(Jsoup.parse(getHTML(new JSONObject(getJSON(url)))));
    }

    public Meme parseArticle(Element element) {
        String title;
        Content content;

        title = element.select("h2.badge-item-title > a").text();

        //Find out the content
        Elements videoElement = element.select("video");
        if (videoElement.size() > 0) {
            Elements videoSrcElement = element.select("div.badge-animated-container-animated");
            content = new VideoContent(videoSrcElement.attr("data-mp4"));
        } else {
            Elements imageElement = element.select("img.badge-item-img");

            String image_src = imageElement.attr("src");

            //Check if it is a preview
            Elements previewElement = element.select("a.post-read-more");
            if(previewElement.size() > 0) {
                content = new PreviewContent(image_src);
            }
            else {
                content = new ImageContent(image_src);
            }
        }

        //Get comments, points
        int comments = Integer.parseInt(element.attr("data-entry-comments"));
        int points = Integer.parseInt(element.attr("data-entry-votes"));
        String url = element.attr("data-entry-url");

        return new Meme(title, content, url, comments, points);
    }

    public Page parsePage(Document document) {
        List<Meme> memes = document.select("article")
                .stream()
                .map(this::parseArticle)
                .collect(Collectors.toList());

        if (memes.size() <= 0)
            throw new PageIsEmptyException();

        Page page = new Page();
        page.getMemes().addAll(memes);

        //Get next page url
        Matcher m = urlPattern.matcher(document.select("a#nextpage").attr("href"));
        if (m.find())
            page.setNextPage("/9gag/" + m.group(1));

        return page;
    }
}
