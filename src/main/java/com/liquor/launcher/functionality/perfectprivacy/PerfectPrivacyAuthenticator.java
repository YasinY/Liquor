package com.liquor.launcher.functionality.perfectprivacy;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

@Slf4j
public class PerfectPrivacyAuthenticator {


    public static final String PERFECT_PRIVACY_URL = "https://www.perfect-privacy.com/en/login";

    @SneakyThrows
    public static boolean authenticate(final String username, final String password) {

        HttpsURLConnection GETRequest;
        try {
            GETRequest = (HttpsURLConnection) new URL(PERFECT_PRIVACY_URL).openConnection();
        } catch (IOException e) {
            log.error("Couldn't connect to url " + PERFECT_PRIVACY_URL);
            return false;
        }
        GETRequest.setRequestMethod("GET");

        final String csrfToken = parseCsrfToken(GETRequest);
        HttpsURLConnection POSTRequest = (HttpsURLConnection) new URL(PERFECT_PRIVACY_URL).openConnection();
        setRequestAttributes(GETRequest, POSTRequest);

        Map<String, String> credentials = defineCredentials(username, password, csrfToken);
        byte[] requestContentBytes = defineLength(POSTRequest, credentials);

        POSTRequest.setFixedLengthStreamingMode(requestContentBytes.length);
        POSTRequest.connect();
        try (OutputStream outputStream = POSTRequest.getOutputStream()) {
            outputStream.write(requestContentBytes);
        } catch (Exception e) {
            log.error(String.format("Couldn't write output stream to url %s", PERFECT_PRIVACY_URL));
        }
        boolean successful = POSTRequest.getHeaderFields().get("Set-Cookie") != null;
        GETRequest.disconnect();
        POSTRequest.disconnect();
        return successful;
    }

    private static Map<String, String> defineCredentials(String username, String password, String csrfToken) {
        Map<String, String> arguments = new LinkedHashMap<>();
        arguments.put("_csrf_protection_token", csrfToken);
        arguments.put("_username", username);
        arguments.put("_password", password);
        return arguments;
    }

    private static byte[] defineLength(HttpsURLConnection POSTRequest, Map<String, String> arguments) {
        String requestContent = arguments.entrySet().stream().map((set) -> set.getKey() + "=" + set.getValue()).collect(Collectors.joining("&"));
        POSTRequest.addRequestProperty("Content-Length", String.valueOf(requestContent.length()));
        return requestContent.getBytes(StandardCharsets.UTF_8);
    }

    private static void setRequestAttributes(HttpsURLConnection GETRequest, HttpsURLConnection POSTRequest) throws ProtocolException {
        POSTRequest.setInstanceFollowRedirects(true);
        POSTRequest.setRequestMethod("POST");
        POSTRequest.setDoOutput(true);
        POSTRequest.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        POSTRequest.addRequestProperty("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
        POSTRequest.addRequestProperty("Connection", "keep-alive");
        POSTRequest.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        POSTRequest.addRequestProperty("Host", "www.perfect-privacy.com");
        POSTRequest.addRequestProperty("Origin", "https://www.perfect-privacy.com");
        POSTRequest.addRequestProperty("Referer", PERFECT_PRIVACY_URL);
        POSTRequest.addRequestProperty("Upgrade-Insecure-Requests", "1");
        POSTRequest.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:77.0) Gecko/20100101 Firefox/77.0");
        POSTRequest.addRequestProperty("Cookie", GETRequest.getHeaderField("Set-Cookie"));
    }

    private static String parseCsrfToken(HttpsURLConnection GETRequest) throws IOException {
        Scanner s = new Scanner(GETRequest.getInputStream()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        int csrfTokenIndex = result.indexOf("_csrf_protection_token");
        return result.substring(csrfTokenIndex + 31, result.indexOf("\"", csrfTokenIndex + 32));
    }
}
