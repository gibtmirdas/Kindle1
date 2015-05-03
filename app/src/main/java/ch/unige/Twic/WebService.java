package ch.unige.Twic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import ch.unige.Twic.Twic.Exceptions.TwicException;

public class WebService {
    public static String callUrl(String path) throws TwicException {
        HttpURLConnection connection;
        String response = "";
        try {
            URI uri = new URI(path);
            URL url = new URL(uri.toURL().toString());
            connection = (HttpURLConnection) url.openConnection();

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
            response = result;
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response == null || response == "")
            throw new TwicException(R.string.serverError);

        return response;
    }
}
