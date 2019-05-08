package com.example.yeabkalwubshit.marketplace.networkhandlers;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;


import java.net.HttpURLConnection;
import java.net.URL;

import java.net.Proxy;
import java.io.IOException;

/**
 * Contains static methods to handle API calls for fetching inter zip code distances.
 */
public class AddressNetworkServices {

    private static final String API_KEY = "4IPodsSOa5mbKGKRV9jnHBWCJXcGWv9ZHsC70sz7V1xoYkOCaYejfCTyfGSBW11m";

    static String buildRequest(String zip1, String zip2, String units) {
        String requestUrl =
                String.format("https://www.zipcodeapi.com/rest/%s/distance.json" +
                        "/%s/%s/%s", API_KEY, zip1, zip2, units);
        return requestUrl;
    }

    public static Double getDistanceBetweenTwoZips(String zip1, String zip2, String units) throws Exception{
        String requestUrl = buildRequest(zip1, zip2, units);
        URL obj = new URL(requestUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        InputStream inputStream;
        StringBuffer response = new StringBuffer();

        try {
            con.setRequestMethod("GET");
            inputStream = new BufferedInputStream(con.getInputStream());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(inputStream));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch(Exception e) {
            System.err.println("Network error" + e.toString());
        } finally {
            con.disconnect();
        }



        String respStr = response.toString();
        System.out.println("This is the response string" + respStr);
        String parsedResponseStr = parseResponse(respStr);

        return Double.parseDouble(parsedResponseStr);

    }

    private static String parseResponse(String respStr) {
        respStr = respStr.replace("distance", "");
        respStr = respStr.replace("\"", "");
        respStr = respStr.replaceAll("[\\{,\\},:]", "");
        return  respStr;
    }

}
