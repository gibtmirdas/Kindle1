package ch.unige.Twic.core;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
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

import ch.unige.Twic.R;
import ch.unige.Twic.exceptions.TwicException;

/**
 * Handle web service calls to the TWiC server.
 */
public class WebService extends AsyncTask<String, String, String> implements TwicFields{

    private WebServiceObserver tab;

    public WebService(WebServiceObserver tab) {
        this.tab = tab;
    }

    /**
     * Query an HTTP URL address.
     * @param path URL address to query.
     * @return HTTP response of the query.
     * @throws TwicException
     */
    public static String callUrl(String path) throws TwicException {
        HttpURLConnection connection;
        String response = "";
        try {
            URI uri = new URI(path);
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

    /**
     * Get a Microsoft OAuth access token.
     * @return Microsoft access token.
     * @throws TwicException
     */
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
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(buildQueryParameters(params));
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

    /**
     * Build the query list of parameters.
     * @param params parameters of the query.
     * @return Query parameters list.
     * @throws UnsupportedEncodingException
     */
    private static String buildQueryParameters(List<NameValuePair> params) throws UnsupportedEncodingException {
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

    /**
     * Execute and read the query response.
     * @param connection url connexion to execute and read.
     * @return Query response
     * @throws IOException
     */
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

    /**
     * Execute an HTTP query asynchronously.
     * @param uri query to execute
     * @return Query response
     */
    @Override
    protected String doInBackground(String... uri) {
        String final_uri = uri[0];

        // If request for Microsoft => get the token before
        if(uri.length > 1 && uri[1].equals("ms")){
            try {
                String token = getPostMsTokenUrl();
                final_uri += "&appId=" + URLEncoder.encode("Bearer " + token, "UTF-8");
            } catch (TwicException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(final_uri));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    /**
     * Automatically notify the tab when an asynchronous query was completed.
     * @param result query response
     */

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        tab.updateResponse(result);
    }
}
