package ch.unige.Twic;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import ch.unige.Twic.Twic.Exceptions.TwicException;
import ch.unige.Twic.Twic.TwicFields;

public class WebService implements TwicFields{
    public static String callUrl(String path) throws TwicException {
        HttpURLConnection connection;
        String response = "";
        try {
            URI uri = new URI("http://latlapps.unige.ch/Synt?lg=fr&in=bite");
            URL url = new URL(uri.toURL().toString());
            connection = (HttpURLConnection) url.openConnection();

            response = readResponse(connection);
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response.equals(""))
            throw new TwicException(R.string.serverError);
        return response;
    }


    public static String callMsUrl(String path) throws TwicException {
        String response="";
        String token = getPostMsTokenUrl();
        HttpURLConnection connection;
        try {
            URI uri = new URI(path+"&appId=" + URLEncoder.encode("Bearer " + token, "UTF-8"));
            URL url = new URL(uri.toURL().toString());
            connection = (HttpURLConnection) url.openConnection();

            response = readResponse(connection);

            response = response.replace("/&#39;/g", "'").replace("/&quot;/g", "\"").replace("/&gt;/g", ">").replace("/&lt;/g", "<").replace("/&amp;/g", "&");
            if (response.charAt(0)== '"')
                response = response.substring(1, response.length());
            if (response.charAt(response.length()-1) == '"')
                response = response.substring(0, response.length()-1);
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (response.equals(""))
            throw new TwicException(R.string.serverError);
        return response;
    }

    public static String getPostMsTokenUrl() throws TwicException {
        String response="";
        try {
            URL url = new URL(MICROSOFTACCESSADDRESS);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("grant_type", "client_credentials"));
            params.add(new BasicNameValuePair("scope", "http://api.microsofttranslator.com"));
            params.add(new BasicNameValuePair("client_id", "TWiC"));
            params.add(new BasicNameValuePair("client_secret", MICROSOFTACCESSSECRET));

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            connection.connect();

            // Get response
            JSONObject json = new JSONObject(readResponse(connection));
            response = json.getString("access_token");
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response.equals(""))
            throw new TwicException(R.string.serverError);
        return response;
    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    private static String readResponse(URLConnection connection) throws IOException {
    // Read response
        InputStream stream = connection.getInputStream();
        InputStreamReader isReader = new InputStreamReader(stream);

        //put output stream into a string
        BufferedReader br = new BufferedReader(isReader);

        String result = "";
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            result += line;
        }
        br.close();
        return result;
    }
}
