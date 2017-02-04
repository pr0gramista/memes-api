package com.poprosturonin.sites;

import com.poprosturonin.utils.URLUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class URLUtilsTest {

    @Test
    public void emptyOptionalIsReturned() throws Exception {
        String url = "http://example.org";
        assertFalse(URLUtils.cutToSecondSlash(url).isPresent());

        url = "http://example.org/foo";
        assertFalse(URLUtils.cutToSecondSlash(url).isPresent());
    }

    @Test
    public void properlyCutStringIsReturned() throws Exception {
        String url = "http://example.org/foo/wow";
        Optional<String> result = URLUtils.cutToSecondSlash(url);

        assertTrue(result.isPresent());

        assertThat(result.get(), equalTo("/wow"));
    }

    @Test
    public void parameterCutWorks() throws Exception {
        String url = "http://example.org/wow?id=123&wow=false";
        String cutUrl = URLUtils.cutOffParameters(url);

        assertThat(cutUrl, equalTo("http://example.org/wow"));
    }
}
