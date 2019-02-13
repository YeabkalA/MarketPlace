package com.example.yeabkalwubshit.marketplace;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import java.net.HttpURLConnection;
import java.net.URL;

public class AddressNetworkServices {

    private static final String API_KEY = "I0OyFyPaJ6aovf11UOGzHUA81OHACv7wdu32QZ3SWT3SvU4gwvoAvMJ6WbOp2aua";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String RESPONSE_DISTANCE_KEY = "distance";

    static String buildRequest(String zip1, String zip2, String units) {
        String requestUrl =
                String.format("https://www.zipcodeapi.com/rest/%s/distance.json" +
                        "/%s/%s/%s", API_KEY, zip1, zip2, units);
        return requestUrl;
    }

    static Double getDistanceBetweenTwoZips(String zip1, String zip2, String units) {
        try {
            String requestUrl = buildRequest(zip1, zip2, units);
            URL obj = new URL(requestUrl);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String respStr = response.toString();
            String parsedResponseStr = parseResponse(respStr);

            return Double.parseDouble(parsedResponseStr);
        } catch (Exception e) {
            return -1.0;
        }
    }

    private static String parseResponse(String respStr) {
        respStr = respStr.replace("distance", "");
        respStr = respStr.replace("\"", "");
        respStr = respStr.replaceAll("[\\{,\\},:]", "");
        return  respStr;
    }

}
