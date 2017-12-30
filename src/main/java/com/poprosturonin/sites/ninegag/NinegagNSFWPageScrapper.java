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
}
