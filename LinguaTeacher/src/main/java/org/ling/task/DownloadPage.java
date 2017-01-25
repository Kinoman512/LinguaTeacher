package org.ling.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import junit.framework.Assert;

import org.ling.fragment.WebFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dmitry on 26.06.2016.
 */
public class DownloadPage extends AsyncTask<String, Integer, String> {


    String err = "";
    WebFragment.IActionWebPage action;
    boolean needUserHeader = true;

    public String getError() {
        return err;
    }

    public DownloadPage(WebFragment.IActionWebPage action, boolean needUserHeader) {
        this.action = action;
        this.needUserHeader = needUserHeader;
    }

    public DownloadPage(WebFragment.IActionWebPage action) {
        this.action = action;
    }

    @Override
    protected void onPreExecute() {


    }


    public static String toCurrectURL(String url) {
        String url2 = encode(url);
        url2 = findHttpRegExp(url2);
        return url2;
    }

    public static String encode(@NonNull String uriString) {
        if (TextUtils.isEmpty(uriString)) {
            Assert.fail("Uri string cannot be empty!");
            return uriString;
        }
        // getQueryParameterNames is not exist then cannot iterate on queries
        if (Build.VERSION.SDK_INT < 11) {
            return uriString;
        }
        // Check if uri has valid characters
        // See https://tools.ietf.org/html/rfc3986
        Pattern allowedUrlCharacters = Pattern.compile("([A-Za-z0-9_.~:/?\\#\\[\\]@!$&'()*+,;" +
                "=-]|%[0-9a-fA-F]{2})+");
        Matcher matcher = allowedUrlCharacters.matcher(uriString);
        String validUri = null;
        if (matcher.find()) {
            validUri = matcher.group();
        }
        if (TextUtils.isEmpty(validUri) || uriString.length() == validUri.length()) {
            return uriString;
        }

        // The uriString is not encoded. Then recreate the uri and encode it this time
        Uri uri = Uri.parse(uriString);
        Uri.Builder uriBuilder = new Uri.Builder()
                .scheme(uri.getScheme())
                .authority(uri.getAuthority());
        for (String path : uri.getPathSegments()) {
            uriBuilder.appendPath(path);
        }
        for (String key : uri.getQueryParameterNames()) {
            uriBuilder.appendQueryParameter(key, uri.getQueryParameter(key));
        }
        String correctUrl = uriBuilder.build().toString();
        return correctUrl;
    }

    public static String findHttpRegExp(@NonNull String uriString) {
        Pattern allowedUrlCharacters = Pattern.compile("(^(http|https)://)+");
        Matcher matcher = allowedUrlCharacters.matcher(uriString);
        String validUri = uriString;
        if (!matcher.find()) {
            validUri = "https://" + uriString;
        }
        ;

        return validUri;
    }

    String decodeNumericEntities(String s) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("\\&#(\\d+);").matcher(s);
        while (m.find()) {
            int uc = Integer.parseInt(m.group(1));
            m.appendReplacement(sb, "");
            sb.append(uc);
        }
        m.appendTail(sb);
        return sb.toString();
    }


    @Override
    protected String doInBackground(String... urls) {

        URL url = null;
        HttpURLConnection urlConnection = null;
        StringBuilder response = new StringBuilder();
        Exception exp = null;
        String charset = "utf-8";
        String res  = "" ;

        try {
            Request req = findRequestIfHave(urls[0]);
            String url2;
            if(req.isRequest){
                url2 = req.url;
            }else{
                url2 = toCurrectURL(urls[0]);
            }


            url = new URL(url2);


            urlConnection = (HttpURLConnection) url.openConnection();

//            urlConnection.setRequestProperty("Expect", "100-continue");
            if(needUserHeader){
                urlConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2; en-gb; Nexus One Build/FRF50) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1");
            }

            urlConnection.setRequestProperty("Content-Type", "text/html; charset=utf-8");
            String str = urlConnection.getRequestMethod();

            String contentType = urlConnection.getContentType();
            String[] values = contentType.split(";"); // values.length should be 2

            for (String value : values) {
                value = value.trim();

                if (value.toLowerCase().startsWith("charset=")) {
                    charset = value.substring("charset=".length());
                }
            }

            if ("".equals(charset)) {
                charset = "UTF-8";
            }

            urlConnection.setConnectTimeout(9000);

            InputStream is;

            int responseCode = urlConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                is = urlConnection.getInputStream();
            } else {
                is = urlConnection.getErrorStream();
                if(is == null){
                    is = urlConnection.getInputStream();
                }
            }



//            Scanner scanner = new Scanner(is, "ISO-8859-1");

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    is,
//                    Charset.forName("ISO-8859-1"))
                    Charset.forName(charset))
            );
            String buffer;
            String inputLine;


            response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {

                response.append(inputLine);
            }
            buffer = new String(response.toString().getBytes(charset), "UTF-8");
            res = response.toString();
            in.close();

        } catch (java.net.SocketTimeoutException e) {
            exp = e;
        } catch (MalformedURLException e) {
            exp = e;
        } catch (IOException e) {
            exp = e;
        } catch (Exception e) {
            exp = e;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }


        if (action != null)
            action.onSuccess(response.toString(), exp, String.valueOf(url),charset);


        return res;
    }

    public static Request findRequestIfHave(String url) {

        Pattern charsPattern = Pattern.compile("(\\.)+");
        Pattern charsPattern2 = Pattern.compile("([\\s]+)");
        Matcher matcher = charsPattern.matcher(url);
        Matcher matcher2 = charsPattern2.matcher(url.trim());

        Request req = new Request();
        req.isRequest = true;

        if (matcher.find()) {
            req.isRequest = false;
            //req
            if(matcher2.find()){
                req.isRequest = true;
            }
        }


        if(req.isRequest){

            req.url = encode("https://www.google.ru/search?ie=UTF-8&q=" + url);
        }
        return req;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

    }

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }


    public static class Request{
        public boolean isRequest;
        public String url;
    }
}


