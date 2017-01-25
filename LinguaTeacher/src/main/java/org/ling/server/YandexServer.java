package org.ling.server;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import odyssey.ru.linglibrary.Util;

import org.ling.Cache;
import org.R;;
import org.ling.WebClient;
import org.ling.server.result.Tword;
import org.ling.server.result.YandexWord;
import org.ling.task.DownloadPage;
import org.ling.utils.IAction;
import org.ling.utils.IActionHandler;
import org.ling.utils.LangVoice;

/**
 * Created by Dmitry on 07.07.2016.
 */
public class YandexServer implements ISearchServer {
    ISearchServer wrapped;
    IActionHandler handlerTrans;
    IActionHandler handlerImage;
    IActionHandler handlerSound;
    AsyncHttpClient client = null;
    IAction afterGetSID;
    static Cache cache = new Cache("Yandex");
    Context mContext;

    public YandexServer(Context con) {
        mContext = con;
//        this.pb = new ProgressBar();
    }



    class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            analiseString(url);
            super.onLoadResource(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }


    public void initSID() {
        String sid = (String) cache.get("SID");
        if (sid != null) {
            return;
        }

        WebView wv = new WebView(mContext);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebViewClient(new MyWebViewClient());
        downloadPage("https://translate.yandex.ru/?text=cat&lang=en-ru", wv);

    }

    public String downloadPage(String url, WebView wv) {
        DownloadPage task = new DownloadPage( null, false);
        String html = null;
        try {
            html = task.execute(url).get(6000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }


        wv.loadDataWithBaseURL(url, html, "text/html", "UTF-8", null);
        return html;

    }

    public static String SID = "";
    String lang1;
    String lang2;
    String word;

    public void analiseString(String url) {
        Log.d("yandex_analyse", url);
        //&& url.contains("https://dictionary.yandex.net/dicservice.json/lookup?")
        if (SID.isEmpty() ) {
            Map<String, List<String>> params = getQueryParams(url);
            if(params.containsKey("sid")){
                SID = params.get("sid").get(0);
                cache.put("SID", SID);
                if (afterGetSID != null) afterGetSID.onSucces("");
            }
        }
    }

    public static Map<String, List<String>> getQueryParams(String url) {
        try {
            Map<String, List<String>> params = new HashMap<String, List<String>>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }

                    List<String> values = params.get(key);
                    if (values == null) {
                        values = new ArrayList<String>();
                        params.put(key, values);
                    }
                    values.add(value);
                }
            }

            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    public static String postTranslate(String url, String text) throws Exception {

        try {
            HttpClient client = HttpClientBuilder.create().useSystemProperties().build();

            HttpPost post = new HttpPost(url);
            post.addHeader("charset", "utf-8");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


            builder.addTextBody("text", text, ContentType.APPLICATION_JSON);
            builder.addTextBody("option", "4", ContentType.DEFAULT_BINARY);
            builder.setCharset(Charset.forName("UTF-8"));
            post.setEntity(builder.build());


            HttpResponse response = client.execute(post);
            return getContent(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public static String getContent(HttpResponse response) throws IOException {

        InputStream is = response.getEntity().getContent();

        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String body = "";
        String content = "";

        while ((body = rd.readLine()) != null) {
            content += body + "\n";
        }
        return content.trim();
    }


    void translate2(final String lang1, final String lang2, final String text){

    }


    @Override
    public void translate(final String lang1, final String lang2, final String text) {


        try {


            this.lang1 = lang1;
            this.lang2 = lang2;
            this.word = text;


            String sid = (String) cache.get("SID");
            if (sid != null) {
                SID = sid;
            }


            if (SID.isEmpty()) {
                afterGetSID = new IAction() {
                    @Override
                    public void onSucces(Object rs) {
                        translate(lang1, lang2, word);
                    }
                };
                initSID();

            } else {

                RequestParams rp = new RequestParams();
                rp.setForceMultipartEntityContentType(true);

                final AsyncHttpClient client = WebClient.client;
                client.removeAllHeaders();


                client.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0");
                final String url = "https://dictionary.yandex.net/dicservice.json/lookup?ui=en&srv=tr-text&sid=" + SID +
                        "&text=" + text + "&type=&lang=" + lang1 + "-" + lang2 + "&flags=7";

                final String url3 = "https://translate.yandex.net/api/v1/tr.json/translate?id=" + SID + "-0-0&srv=tr-text&lang=" + lang1 + "-" + lang2 + "&reason=paste";




                client.get(url, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("YandexServer", "onFail5" + throwable.getMessage() + " " + responseString);

                        System.out.println("\nYandexServer fail");
                        if (throwable.getMessage().equals("BAD REQUEST")) {
                            threadWord.start();

                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        System.out.println("\nYandexServer succes");
                        List<YandexWord> listYW = new ArrayList<YandexWord>();
                        try {

                            JSONObject sobj = new JSONObject(responseString);
                            Log.d("YandexServer", "onSuccess4");


                            JSONArray defs = sobj.getJSONArray("def");
                            urlCore = url3;
                            if (defs.length() == 0) {
                                //нет данных
                                threadWord.start();
                                return;
                            }


                            for (int i = 0; i < defs.length(); i++) {
                                JSONObject def = defs.getJSONObject(i);


                                String word = "";

                                if (def.has("text")) word = def.getString("text");

                                if (word.isEmpty()) continue;
                                YandexWord yw = new YandexWord(word, lang1, lang2);

                                if (def.has("pos")) yw.setClassf(def.getString("pos"));
                                if (def.has("gen")) yw.setGender(def.getString("gen"));
                                ;
                                if (def.has("ts")) yw.setTscr(def.getString("ts"));

                                List<Tword> twords = new ArrayList<Tword>();

                                JSONArray trans = def.getJSONArray("tr");

                                for (int j = 0; j < trans.length(); j++) {
                                    JSONObject tr = trans.getJSONObject(j);


                                    String tword = "";
                                    if (tr.has("text") && !tr.isNull("text"))
                                        tword = tr.getString("text");

                                    if (tword.isEmpty()) continue;
                                    Tword tw = new Tword(tword);

                                    if (tr.has("pos") && !tr.isNull("pos"))
                                        tw.setClassf(tr.getString("pos"));
                                    if (tr.has("gen") && !tr.isNull("gen"))
                                        tw.setGender(tr.getString("gen"));
                                    ;
                                    if (tr.has("ts") && !tr.isNull("ts"))
                                        tw.setTscr(tr.getString("ts"));


                                    if (tr.has("mean") && !tr.isNull("mean")) {
                                        List<String> means = new ArrayList<String>();

                                        JSONArray mns = tr.getJSONArray("mean");
                                        for (int u = 0; u < mns.length(); u++) {
                                            means.add(mns.getJSONObject(u).getString("text"));
                                        }
                                        tw.setMeans(means);
                                    }

                                    if (tr.has("syn")) {
                                        List<Tword> syns = new ArrayList<Tword>();

                                        JSONArray syn = tr.getJSONArray("syn");
                                        for (int u = 0; u < syn.length(); u++) {

                                            JSONObject synobj = syn.getJSONObject(u);

                                            String wsyn = "";
                                            if (synobj.has("text")) wsyn = synobj.getString("text");

                                            if (wsyn.isEmpty()) continue;
                                            Tword synT = new Tword(wsyn);

                                            if (synobj.has("pos") && !synobj.isNull("pos"))
                                                synT.setClassf(synobj.getString("pos"));
                                            if (synobj.has("gen") && !synobj.isNull("gen"))
                                                synT.setGender(synobj.getString("gen"));
                                            ;
                                            if (synobj.has("ts") && !synobj.isNull("ts"))
                                                synT.setTscr(synobj.getString("ts"));
                                            syns.add(synT);
                                        }
                                        tw.setSyns(syns);
                                    }
                                    twords.add(tw);
                                    yw.setListtword(twords);
                                }
                                listYW.add(yw);
                                Log.d("YandexServer", "onSuccess5");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (handlerTrans != null)
                                handlerTrans.onFailAction("", new Throwable(e));
                        }

                        if (handlerTrans != null) handlerTrans.onSuccessAction(listYW);
                    }
                });

            }
        } catch (Exception e) {
            handlerTrans.onFailAction("", new Throwable(e));
        }

    }


    @Override
    public void searchImage(final String search) {

    }

    @Override
    public void getSound(String lang, final String text) {

    }

    @Override
    public void setServerActionListnerTanslate(IActionHandler ser) {
        handlerTrans = ser;
    }

    @Override
    public void setServerActionListnerImage(IActionHandler ser) {
        handlerImage = ser;
    }

    @Override
    public void setServerActionListnerSound(IActionHandler ser) {
        handlerSound = ser;
    }


    public void setWebClient(AsyncHttpClient client) {
        this.client = client;
    }

    public AsyncHttpClient getWebClient() {
        return client;
    }

    @Override
    public boolean isGetBan() {
        return false;
    }

    @Override
    public void setWrapped(ISearchServer s) {
        wrapped = s;
    }

    @Override
    public ISearchServer getWrapped() {
        return wrapped;
    }


    private String urlCore;

    Thread threadWord =  new Thread(new Runnable() {
        @Override
        public void run() {
            try {
//                                    String w  = java.net.URLEncoder.encode(word, "UTF-8");
                String responseString = postTranslate(urlCore, word);
                List<YandexWord> listYW = new ArrayList<YandexWord>();

                JSONObject sobj = new JSONObject(responseString);
                Log.d("YandexServer", "onSuccess4" + responseString);


                JSONArray texts = sobj.getJSONArray("text");
                if (texts.length() == 0) {
                    //нет данных
                    handlerTrans.onFailAction(Util.getStringById(R.string.no_data), null);
                    return;
                }
                if (Integer.valueOf(sobj.getString("code")) > 300) {
                    //ошибка
                    handlerTrans.onFailAction(Util.getStringById(R.string.error), null);
                    return;
                }


                YandexWord yw = new YandexWord(word, lang1, lang2);
                List<Tword> twords = new ArrayList<Tword>();

                for (int i = 0; i < texts.length(); i++) {
                    String tword = texts.getString(i);
                    if (word.equals(tword)) {
                        //ошибка слово не переведено
                        handlerTrans.onFailAction(Util.getStringById(R.string.err_no_translated) , null);
                        return;
                    }
                    Tword tw = new Tword(tword);
                    twords.add(tw);

                }
                yw.setListtword(twords);
                listYW.add(yw);
                if (handlerTrans != null)
                    handlerTrans.onSuccessAction(listYW);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    });

}
