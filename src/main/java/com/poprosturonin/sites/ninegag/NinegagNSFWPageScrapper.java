package com.poprosturonin.sites.ninegag;

import com.poprosturonin.data.Meme;
import com.poprosturonin.data.Page;
import com.poprosturonin.exceptions.LoginFailedException;
import com.poprosturonin.exceptions.MemeSiteResponseFailedException;
import com.poprosturonin.exceptions.PageIsEmptyException;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Mistrzowie scrapper
 */
@Component
public class NinegagNSFWPageScrapper extends NinegagPageScrapper {
    private static final String LOGIN_URL = "http://9gag.com/login";
    private static final String CHARSET = "UTF-8";

    private String session = "";

    /**
     * Pattern for url
     * fe. /nsfw?id=arbN9e0%2CajXDj41%2CagLGjgw&c=20&
     * group 1 is parameter id
     * group 2 is parameter c
     */
    private Pattern urlPattern = Pattern.compile("^/nsfw\\?id=(.+)&c=(\\d+)");

    @Value("${9gag.email}")
    private String email;

    @Value("${9gag.password}")
    private String password;

    private boolean login() {
        try {
            URL url = new URL(LOGIN_URL);

            //Create params
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("username", email);
            params.put("password", password);

            //Transform params to request body
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, Object> param : params.entrySet()) {
                if (postData.length() != 0) postData.append('&');
                postData.append(URLEncoder.encode(param.getKey(), CHARSET));
                postData.append('=');
                postData.append(URLEncoder.encode(String.valueOf(param.getValue()), CHARSET));
            }
            byte[] postDataBytes = postData.toString().getBytes(CHARSET);

            //Let's open the connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/form-data");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            connection.setDoOutput(true);
            connection.getOutputStream().write(postDataBytes);

            //Read response=
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), CHARSET));

            String name = null;
            for (int i = 1; (name = connection.getHeaderFieldKey(i)) != null; i++) {
                if (name.equals("Set-Cookie")) {
                    String cookie = connection.getHeaderField(i);
                    if (cookie.startsWith("PHPSESSID")) {
                        session = cookie;
                        return true;
                    }
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            throw new MemeSiteResponseFailedException();
        }
    }

    private boolean isLoggedIn() {
        return session != null && session.length() > 0;
    }

    @Override
    public String getJSON(String url) {
        try {
            if (!isLoggedIn()) {
                if (!login())
                    throw new LoginFailedException();
            }

            URL downloadURL = new URL(url);
            URLConnection connection = downloadURL.openConnection();
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            connection.setRequestProperty("Cookie", session + ";safemode=0"); //Disabling safemode makes post visible without having to click on them
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

    @Override
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
            page.setNextPage("/9gagnsfw/page" + m.group(1));

        return page;
    }
}
