package org.alexburchak.trice;

import org.alexburchak.trice.config.SpringConfiguration;
import org.alexburchak.trice.controller.HookController;
import org.alexburchak.trice.controller.TriceController;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.arquillian.cube.CubeIp;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.testng.Arquillian;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.UUID;
import java.util.function.Consumer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author alexburchak
 */
public class TriceITest extends Arquillian {
    private static final long TEST_TIMEOUT = 60000;
    private static final int CONNECT_TIMEOUT = 10000;
    private static final int SOCKET_TIMEOUT = 10000;

    private static final By BY_XPATH_METHOD = By.xpath("//div[@id='container']/div[@class='method value inline-block']");

    @Drone
    private WebDriver webDriver;
    @CubeIp(containerName = "trice")
    private String ip;

    private int port;

    @BeforeClass
    public void setUpClass() throws GeneralSecurityException, IOException {
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);

        SpringConfiguration configuration = new Yaml(representer)
                .loadAs(getClass().getResourceAsStream("/application.yml"), SpringConfiguration.class);
        port = configuration.getServer().getPort();
    }

    @Test(timeOut = TEST_TIMEOUT)
    public void testVersion() {
        URI version = createURI("http", ip, port, "/version.txt", b -> {
        });

        webDriver.get(version.toString());

        String text = webDriver.getPageSource();
        assertTrue(text.matches("^\\d+\\.\\d+\\.\\d+(-SNAPSHOT)?$"), text);
    }

    @Test(timeOut = TEST_TIMEOUT)
    public void testHookPost() {
        String sid = UUID.randomUUID().toString();

        URI trice = createURI("http", ip, port, TriceController.PATH_TRICE, b -> b.addParameter(TriceController.PARAM_SID, sid));

        webDriver.get(trice.toString());

        URI hook = createURI("http", ip, port, HookController.PATH_HOOK, b -> b.addParameter(HookController.PARAM_SID, sid));

        post(hook);

        //todo will work one day? new WebDriverWait(webDriver, 30).until(ExpectedConditions.textToBePresentInElementLocated(BY_XPATH_METHOD, "POST"));
    }

    @Test(timeOut = TEST_TIMEOUT)
    public void testHookGet() {
        String sid = UUID.randomUUID().toString();

        URI trice = createURI("http", ip, port, TriceController.PATH_TRICE, b -> b.addParameter(TriceController.PARAM_SID, sid));

        webDriver.get(trice.toString());

        URI hook = createURI("http", ip, port, HookController.PATH_HOOK, b -> b.addParameter(HookController.PARAM_SID, sid));

        get(hook);

        //todo will work one day? new WebDriverWait(webDriver, 30).until(ExpectedConditions.textToBePresentInElementLocated(BY_XPATH_METHOD, "GET"));
    }

    private void post(URI uri) {
        try (CloseableHttpClient client = createHttpClient()) {
            HttpPost post = new HttpPost();
            post.setConfig(createRequestConfig());
            post.setURI(uri);

            try (CloseableHttpResponse response = client.execute(post)) {
                StatusLine statusLine = response.getStatusLine();

                assertEquals(statusLine.getStatusCode(), HttpStatus.SC_OK);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to send POST request to " + uri, e);
        }
    }

    private void get(URI uri) {
        try (CloseableHttpClient client = createHttpClient()) {
            HttpGet get = new HttpGet();
            get.setConfig(createRequestConfig());
            get.setURI(uri);

            try (CloseableHttpResponse response = client.execute(get)) {
                StatusLine statusLine = response.getStatusLine();

                assertEquals(statusLine.getStatusCode(), HttpStatus.SC_OK);
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to send GET request to " + uri, e);
        }
    }

    private RequestConfig createRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
    }

    private CloseableHttpClient createHttpClient() {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();

        return clientBuilder.build();
    }

    private URI createURI(String scheme, String ip, int port, String path, Consumer<URIBuilder> consumer) {
        URIBuilder builder = new URIBuilder()
                .setScheme(scheme)
                .setHost(ip)
                .setPort(port)
                .setPath(path);

        consumer.accept(builder);

        try {
            return builder.build();
        } catch (URISyntaxException e) {
            throw new AssertionError("Failed to build URI for scheme " + scheme + ", ip " + ip + ", port " + port + ", path " + path, e);
        }
    }
}
