package org.ling.forvo;

import android.util.Base64;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;

import org.ling.service.SoundService;
import org.ling.utils.FileHelper;
import org.ling.utils.IActionHandler;
import org.ling.utils.Md5;
import org.ling.model.Pronounce;
import org.ling.server.BingServer;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Dmitry on 22.06.2016.
 */
public class ForvoHelper {




    public static void getRobotVoice(final String word,final String lang, final IActionHandler handler) {

        final BingServer bs = new BingServer();
        bs.setWebClient(new AsyncHttpClient());

        bs.setServerActionListnerSound(new IActionHandler() {
            @Override
            public void onSuccessAction(final Object rs) {


                //переделатьб
                final String url = (String) rs;
                String nameWord = Md5.md5(url);

                FileHelper.downloadMp3(url, nameWord, lang, bs.getWebClient(), new IActionHandler() {
                    @Override
                    public void onSuccessAction(Object rs) {
                        Pronounce pr = new Pronounce(url, -100, "Robot", "", "");
                        handler.onSuccessAction(pr);
                    }

                    @Override
                    public void onFailAction(String s, Throwable throwable) {
//                        Toast.makeText(MainActivity.activity, "Ошибка  загрузки голоса робота " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                        handler.onFailAction(s, throwable);
                    }
                });


            }

            @Override
            public void onFailAction(String s, Throwable throwable) {
//                Toast.makeText(MainActivity.activity, "Ошибка " + s, Toast.LENGTH_SHORT).show();
                handler.onFailAction(s, throwable);
            }
        });
        bs.getSound(lang, word);

    }


    public static void getForvoWord(final String word, final String lingua, final IActionHandler handler) {


        final ForvoWord res = new ForvoWord(word, lingua);
        final  String url ;

        url = "http://forvo.com/word/" + word;

        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }


        final String URLtoSound = "http://audio.forvo.com:80/mp3/";
        AsyncHttpClient client = new AsyncHttpClient();

        client.setSSLSocketFactory(
                new SSLSocketFactory(sslContext,
                        SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));


        client.get(url, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                if (handler != null) handler.onFailAction(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                try {
                    Document doc = Jsoup.parse(responseString);
                    //получаю список произношений
                    Elements elems = doc.select("article.pronunciations");
                    Element wordsList = null;
                    //выбираю с нужным языком
                    for (Element e : elems) {
                        String str = e.getElementsByTag("em").attr("id");

                        if (str.equals(lingua)) {
                            wordsList = e;
                            break;
                        }
                    }

                    //выбираю произноешния
                    List<Pronounce> lpr = new ArrayList<>();
                    if(wordsList == null){
                        if (handler != null) handler.onSuccessAction(res);
                        return;
                    }
                    int x = 0;
                    Elements prs = wordsList.child(1).select("li");
                    for (Element e : prs) {
                        try{
                            x++;
                            String encodedString = e.select(".play").attr("onclick").split("'")[1];
                            Log.d("MyForvo",encodedString  + " #" + x);
                            if(x == 19){
                                x =19;
                            }

                            Elements elem = e.select(".uLink");
                            String userName = "";
                            if(elem.size() == 0){
                                userName =  e.text().split(" ")[2];
                            }else {
                                userName = e.select(".uLink").first().text();
                            }

                            Log.d("MyForvo",userName );

                            Elements nums = e.select(".num_votes");
                            String numVotes = nums.first().text();
                            Log.d("MyForvo",numVotes);


                            String[] arr2 = numVotes.split(" ");
                            int likes = Integer.valueOf(arr2[0]);

                            Log.d("MyForvo",likes+ "");
                            String from = e.select(".from").text();


                            Log.d("MyForvo",from);
                            String from2 = from.substring(1, from.length() - 1);
                            String[] arr = from2.split("from");

                            //String encodedString = "ODk4NjIzNy8zOS84OTg2MjM3XzM5XzMxODQzMV83OTg4NjMubXAz";
                            String mp3Url = URLtoSound + decode64String(encodedString);

                            Log.d("MyForvo", URLtoSound + mp3Url);
                            String place = arr[1];
                            Log.d("MyForvo",place);
                            String gender = arr[0];
                            Log.d("MyForvo",gender);
                            if (mp3Url != null && userName != null && from2 != null) {
                                Pronounce pr = new Pronounce(mp3Url, likes, userName, place, gender);
                                lpr.add(pr);
                            }
                            Log.d("MyForvo", "end");
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }


                    }
                    res.setPronounces(lpr);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (handler != null)
                        handler.onFailAction("", new Throwable("error in forvo " + e));
                    return;
                }
                if (handler != null) handler.onSuccessAction(res);
            }
        });

    }


    public static String decode64String(String encodedString) {

        byte[] bytesEncoded = Base64.decode(encodedString, Base64.DEFAULT);
        return new String(bytesEncoded, Charset.forName("UTF-8"));
    }

}
