package ch.unige.kindle1;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Created by thomas on 2/27/15.
 */
public class WebService {
    private String path = "http://latlapps.unige.ch/Twicff?act=twic&pos=20&srclg=de&tgtlg=fr&text=Heute%20wird%20%C3%BCber%20die%20Personalengp%C3%A4sse%20in%20der%20Verfassung%20abgestimmt";
    private String response;

    public WebService() {
        HttpURLConnection connection;

        try {
            URI uri = new URI(path);
            URL url = new URL(uri.toURL().toString());
            connection = (HttpURLConnection) url.openConnection();

            InputStream stream = connection.getInputStream();
            InputStreamReader isReader = new InputStreamReader(stream );

            //put output stream into a string
            BufferedReader br = new BufferedReader(isReader );

            String result="";
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
                result+= line;
            }
            response = result;
            br.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResponse() {
        return response;
    }
}
