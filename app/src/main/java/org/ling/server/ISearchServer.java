package org.ling.server;

import org.ling.utils.IActionHandler;

/**
 * Created by Dmitry on 28.06.2016.
 */
public interface ISearchServer {

    public boolean isGetBan();
    public void setWrapped(ISearchServer s);
    public ISearchServer getWrapped();

    public void translate(String natv, String lang, String text);
    public void searchImage(String text);
    public void getSound(String lang, String text);

    public void setServerActionListnerTanslate(IActionHandler ser);
    public void setServerActionListnerImage(IActionHandler ser);
    public void setServerActionListnerSound(IActionHandler ser);

}
