package com.poprosturonin.sites.ninegag;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.data.contents.ImageContent;
import com.poprosturonin.data.contents.VideoContent;
import com.poprosturonin.exceptions.MemeSiteResponseFailedException;
import com.poprosturonin.exceptions.PageIsEmptyException;
import com.poprosturonin.sites.PageScrapper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mistrzowie scrapper
 */
@Component
public class NinegagPageScrapper implements PageScrapper {
    /**
     * Pattern for url
     * fe. after=a9AQqz1%2Car56dGd%2Ca9AQ2Am&c=10
     * group 1 is parameter after
     * group 2 is parameter c
     */
    private Pattern urlPattern = Pattern.compile("after=(.+)&c=(\\d+)");

    public String downloadUrl(String url) {
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

    public JSONObject getJSON(String rawString) {
        return new JSONObject(rawString);
    }

    @Override
    public Page scrapPage(String url) {
        return parseJSONPage(getJSON(downloadUrl(url)));
    }

    public Meme parseMeme(JSONObject post) {
        Meme meme = new Meme();
        meme.setTitle(StringEscapeUtils.unescapeHtml4(post.getString("title")));
        meme.setUrl(post.getString("url"));
        meme.setCommentAmount(post.getInt("commentsCount"));
        meme.setPoints(post.getInt("upVoteCount"));

        String type = post.getString("type");
        if (type.equals("Animated")) {
            JSONObject images = post.getJSONObject("images");
            Set<String> imageKeys = images.keySet();
            for (String key : imageKeys) {
                JSONObject image = images.getJSONObject(key);
                if (image.has("duration")) {
                    meme.setContent(new VideoContent(image.getString("url")));
                    break;
                }
            }
        } else  { // Photo
            ImageContent imageContent = new ImageContent(post.getJSONObject("images").getJSONObject("image700").getString("url"));
            meme.setContent(imageContent);
        }

        return meme;
    }

    public Page parseJSONPage(JSONObject response) {
        List<Meme> memes = new ArrayList<Meme>(15);
        JSONArray posts = response.getJSONObject("data").getJSONArray("posts");
        for (int i = 0; i < posts.length(); i++)  {
            memes.add(parseMeme(posts.getJSONObject(i)));
        }

        if (memes.size() <= 0)
            throw new PageIsEmptyException();

        Page page = new Page();
        page.getMemes().addAll(memes);

        //Get next page url
        Matcher m = urlPattern.matcher(response.getJSONObject("data").getString("nextCursor"));
        if (m.find())
            page.setNextPage("/9gag/page/" + m.group(1));

        return page;
    }

    public Page parsePage(Document document) {
        return null;
    }
}
