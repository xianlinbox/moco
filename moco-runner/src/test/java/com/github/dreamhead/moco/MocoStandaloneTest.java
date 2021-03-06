package com.github.dreamhead.moco;

import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.runner.JsonRunner;
import com.google.common.io.Resources;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class MocoStandaloneTest {
    private final MocoTestHelper helper = new MocoTestHelper();
    private JsonRunner runner;

    @Before
    public void setup() throws IOException {
        runner = new JsonRunner();
    }

    private void runWithConiguration(String resourceName) {
        try {
            runner.run(Resources.getResource(resourceName).openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void teardown() {
        runner.stop();
    }

    @Test
    public void should_return_expected_response() throws IOException {
        runWithConiguration("foo.json");
        assertThat(helper.get("http://localhost:8080"), is("foo"));
    }

    @Test
    public void should_return_expected_response_with_file() throws IOException {
        runWithConiguration("any_response_with_file.json");
        assertThat(helper.get("http://localhost:8080"), is("foo.response"));
    }

    @Test
    public void should_return_expected_response_with_text_based_on_specified_uri() throws IOException {
        runWithConiguration("foo.json");
        assertThat(helper.get("http://localhost:8080/foo"), is("bar"));
    }

    @Test
    public void should_return_expected_response_with_file_based_on_specified_request() throws IOException {
        runWithConiguration("foo.json");
        assertThat(helper.get("http://localhost:8080/file"), is("foo.response"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_text_request() throws IOException {
        runWithConiguration("foo.json");
        assertThat(helper.postContent("http://localhost:8080", "text_request"), is("response_for_text_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_file_request() throws IOException {
        runWithConiguration("foo.json");
        assertThat(helper.postFile("http://localhost:8080", "foo.request"), is("response_for_file_request"));
    }

    @Test
    public void should_return_expected_response_based_on_specified_get_request() throws IOException {
        runWithConiguration("get_method.json");
        assertThat(helper.get("http://localhost:8080/get"), is("response_for_get_method"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_while_request_non_get_request() throws IOException {
        runWithConiguration("get_method.json");
        helper.postContent("http://localhost:8080/get", "");
    }

    @Test
    public void should_return_expected_response_based_on_specified_post_request() throws IOException {
        runWithConiguration("post_method.json");
        assertThat(helper.postContent("http://localhost:8080/post", ""), is("response_for_post_method"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_while_request_non_post_request() throws IOException {
        runWithConiguration("post_method.json");
        helper.get("http://localhost:8080/post");
    }

    @Test
    public void should_return_expected_response_based_on_specified_header_request() throws IOException {
        runWithConiguration("header.json");
        Content content = Request.Get("http://localhost:8080/header").addHeader("content-type", "application/json").execute().returnContent();
        assertThat(content.asString(), is("response_for_header_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_unknown_header() throws IOException {
        runWithConiguration("header.json");
        helper.get("http://localhost:8080/header");
    }

    @Test
    public void should_return_expected_response_based_on_specified_query_request() throws IOException {
        runWithConiguration("query.json");
        assertThat(helper.get("http://localhost:8080/query?param=foo"), is("response_for_query_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_different_query_param() throws IOException {
        runWithConiguration("query.json");
        helper.get("http://localhost:8080/query?param2=foo");
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_different_query_param_value() throws IOException {
        runWithConiguration("query.json");
        helper.get("http://localhost:8080/query?param=foo2");
    }

    @Test
    public void should_return_expected_response_based_on_specified_xpath_request() throws IOException {
        runWithConiguration("xpath.json");
        assertThat(helper.postFile("http://localhost:8080/xpath", "foo.xml"), is("response_for_xpath_request"));
    }

    @Test(expected = IOException.class)
    public void should_throw_exception_for_unknown_xpath_request() throws IOException {
        runWithConiguration("xpath.json");
        helper.postFile("http://localhost:8080/xpath", "bar.xml");
    }

    @Test
    public void should_expected_response_status_code() throws IOException {
        runWithConiguration("foo.json");
        int statusCode = Request.Get("http://localhost:8080/status").execute().returnResponse().getStatusLine().getStatusCode();
        assertThat(statusCode, is(200));
    }

    @Test
    public void should_expected_response_header() throws IOException {
        runWithConiguration("foo.json");
        HttpResponse response = Request.Get("http://localhost:8080/response_header").execute().returnResponse();
        assertThat(response.getHeaders("content-type")[0].getValue(), is("application/json"));
        assertThat(response.getHeaders("foo")[0].getValue(), is("bar"));
    }

    @Test
    public void should_run_as_proxy() throws IOException {
        runWithConiguration("foo.json");
        HttpResponse response = Request.Get("http://localhost:8080/url").execute().returnResponse();
        assertThat(response.getStatusLine().getStatusCode(), is(200));
    }
}
