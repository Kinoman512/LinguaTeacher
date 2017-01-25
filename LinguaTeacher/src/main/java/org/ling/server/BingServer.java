package org.ling.server;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import org.ling.utils.IActionHandler;
import org.ling.utils.LangVoice;
import org.ling.WebClient;

/**
 * Created by Dmitry on 28.06.2016.
 */
public class BingServer   {
    ISearchServer wrapped;
    IActionHandler handlerTrans;
    IActionHandler handlerImage;
    IActionHandler handlerSound;
    AsyncHttpClient client = null;


    public void setWebClient(AsyncHttpClient client ){
         this.client = client;
    }

    public AsyncHttpClient getWebClient() {
        return client;
    }

    public boolean isGetBan() {
        return false;
    }

    public void setWrapped(ISearchServer s) {
        wrapped = s;
    }

    public ISearchServer getWrapped() {
        return wrapped;
    }

    public void translate(final String natv, final String lang, final String text) {



//        String l1 = "en";
//
////добавить обработку 0
//
//        final IServerActionHandler funhandler;
//
//        if (!natv.equals("en") ^ !(lang.equals("en"))) {
//            funhandler = handlerTrans;
//            l1 = lang;
//        } else {
//            funhandler = new IServerActionHandler() {
//                @Override
//                public void onSuccessAction(ResultSearch rs) {
//                    BingServer.this.translate("en", lang, rs.getTranslatedWord().getTranslation().get(0).getSource());
//                }
//
//                @Override
//                public void onFailAction(String s, Throwable throwable) {
//
//                }
//            };
//        }
//
//
//        final AsyncHttpClient client = WebClient.client;
//        client.removeAllHeaders();
//
//        client.addHeader("Host", "www.bing.com");
//        client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
//        client.addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
//        client.addHeader("Accept-Encoding", " gzip, deflate, br");
//        client.addHeader("Connection", "keep-alive");
//        client.addHeader("Cache-Control", "max-age=0");
//
//
//        client.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//
//
//        String url1 = "https://www.bing.com/translator/";
//
//        final String url4 = "https://www.bing.com/translator/api/Dictionary/Lookup";
//        final RequestParams params = new RequestParams();
//        params.add("from", natv);
//        params.add("to", l1);
//        params.add("text", text);
//
//        client.get(url1, new TextHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, String s) {
//                Log.d("BingServer", "onSuccess1" + s);
//
//                client.removeHeader("Accept");
//                client.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
//                client.addHeader("Content-Type", "application/json; charset=utf-8");
//                client.addHeader("X-Requested-With", "XMLHttpRequest");
//                client.addHeader("Referer", "https://www.bing.com/translator/");
//
//                client.get(url4, params, new TextHttpResponseHandler() {
//                    @Override
//                    public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
//                        Log.d("BingServer", "onFailure" + responseString + " 123 " + throwable.getMessage());
//                        if (funhandler != null) funhandler.onFailAction(responseString, throwable);
//
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
//                        TranslatedWord sw = null;
//                        Log.d("BingServer", "onSuccess4");
//
//                        try {
//                            JSONObject sobj = new JSONObject(responseString);
//                            String source = sobj.getString("normalizedSource");
//                            String from = sobj.getString("from");
//                            String to = sobj.getString("to");
//
//                            sw = new TranslatedWord(source);
//                            sw.setLang(lang);
//                            sw.setNatv(natv);
//                            JSONArray items = sobj.getJSONArray("items").getJSONArray(0);
//
//                            if (!sobj.isNull("items")) {
//
//                                for (int i = 0; i < items.length(); i++) {
//                                    JSONObject e = items.getJSONObject(i);
//                                    String dest = e.getString("normalizedTarget");
//                                    TranslatedWord translateWord = new TranslatedWord(dest);
//                                    JSONArray backTranslations = e.getJSONArray("backTranslations");
//
//                                    if (!e.isNull("backTranslations")) {
//                                        for (int j = 0; j < backTranslations.length(); j++) {
//                                            JSONObject e2 = backTranslations.getJSONObject(j);
//                                            String text = e2.getString("normalizedText");
//                                            TranslatedWord backTrans = new TranslatedWord(text);
//                                            translateWord.addTraslate(backTrans);
//                                        }
//                                    }
//
//                                    sw.addTraslate(translateWord);
//                                }
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        ResultSearch rs = new ResultSearch(text);
//                        rs.setTranslatedWord(sw);
//                        if (funhandler != null)
//                            funhandler.onSuccessAction(rs);
//
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, String s, Throwable throwable) {
//                Log.d("BingServer", s + "onFailure1  " + i + " " + throwable.getMessage() + "\n");
//                if (funhandler != null) funhandler.onFailAction(s, throwable);
//
//            }
//
//
//        });


    }


    public void searchImage(final String search) {


        final AsyncHttpClient client = new WebClient().client;
        client.removeAllHeaders();

        client.addHeader("Host", "www.bing.com");
        client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
        client.addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
        client.addHeader("Accept-Encoding", " gzip, deflate, br");
        client.addHeader("Connection", "keep-alive");
        client.addHeader("Cache-Control", "max-age=0");
        client.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");


        final String url1 = " http://www.bing.com/images/search";


        final RequestParams params = new RequestParams();
        params.add("q", search);
        params.add("qs", "n");
        params.add("form", "QBIR");
        params.add("scope", "images");
        params.add("sc", "8-3");
        params.add("sp", "-1");
        params.add("pq", search);

        client.get(url1, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("BingServer Image", "onFailure 1" + throwable);
                if (handlerImage != null) handlerImage.onFailAction(responseString, throwable);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.d("BingServer Image", "Success 1");
                Document doc = Jsoup.parse(responseString);
                Elements es = doc.select("div.dg_u div a img");
                List<String> list = new ArrayList<String>();
                for (Element e : es) {
                    list.add(e.attr("src2"));
                }
                if (list.size() == 0) {
                    if (handlerImage != null)
                        handlerImage.onFailAction(responseString, new Throwable("There are no images for  " + search + " in bing"));
                    return;
                }
                Log.d("BingServer Image", "Success 2" + list.get(0));
                if (handlerImage != null) handlerImage.onSuccessAction(list);

            }
        });


    }

    public void getSound(String lang, final String text) {

        client.removeAllHeaders();

        client.addHeader("Host", "www.bing.com");
        client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
//        client.addHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
//        client.addHeader("Accept-Encoding", " gzip, deflate, br");
//        client.addHeader("Connection", "keep-alive");
//        client.addHeader("Cache-Control", "max-age=0");


        client.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");


        String url1 = "https://www.bing.com/translator/";
        LangVoice langVoice = LangVoice.findByLang(lang);
        if(langVoice == null){
            handlerSound.onFailAction("",new Throwable("Нет голоса для " + lang));
            return;
        }

        final String url4 = "https://www.bing.com/translator/api/language/Speak?locale=" + langVoice.getCodeVoice()  + "&gender=male&media=audio/mp3&text=" + text;

        client.get(url1, new TextHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//
                if (handlerSound != null) handlerSound.onSuccessAction(url4);
            }
        });


    }

    public void setServerActionListnerTanslate(IActionHandler ser) {
        handlerTrans = ser;
    }

    public void setServerActionListnerImage(IActionHandler ser) {
        handlerImage = ser;
    }

    public void setServerActionListnerSound(IActionHandler ser) {
        handlerSound = ser;
    }
}
