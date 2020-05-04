package dev.jun0.dcalimi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class RequestHttpURLConnection {
    public String get(String _url) throws IOException {
        return get(_url, 15000);
    }
    public String get(String _url, int timeoutMills) throws IOException {
        try {
            URL url = new URL(_url);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setConnectTimeout(timeoutMills);

            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return null;

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(), StandardCharsets.UTF_8));
            String strLine;
            StringBuilder stringBuilder = new StringBuilder();

            while ((strLine = reader.readLine()) != null) {
                stringBuilder.append(strLine);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
