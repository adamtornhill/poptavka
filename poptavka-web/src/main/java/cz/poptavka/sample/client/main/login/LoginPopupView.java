package cz.poptavka.sample.client.main.login;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocalizableMessages;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.mvp4g.client.view.ReverseViewInterface;

import cz.poptavka.sample.client.main.common.SimpleIconLabel;
import cz.poptavka.sample.client.main.login.LoginPopupPresenter.LoginPopupInterface;
import cz.poptavka.sample.client.resources.StyleResource;
import cz.poptavka.sample.shared.domain.UserDetail.Role;

//public class LoginPopupView extends PopupPanel implements LoginPopupInterface {
public class LoginPopupView extends PopupPanel
    implements ReverseViewInterface<LoginPopupPresenter>, LoginPopupInterface {

    /************** DEVEL ONLY *********/
    private static HashMap<Role, PrivateUser> privateUsers = new HashMap<Role, PrivateUser>();

    class PrivateUser {

        private String name;
        private String pass;

        public PrivateUser(Role role) {
            if (role == Role.CLIENT) {
                name = "beho@poptavka.cz";
                pass = "mojeheslo";
            }
            if (role == Role.SUPPLIER) {
                name = "sup@pplier.cz";
                pass = "aaaaaa";
            }
        }

        public String getName() {
            return name;
        }

        public String getPass() {
            return pass;
        }
    }

    {
        privateUsers.put(Role.CLIENT, new PrivateUser(Role.CLIENT));
        privateUsers.put(Role.SUPPLIER, new LoginPopupView.PrivateUser(Role.SUPPLIER));
    }
    /************** DEVEL ONLY *********/

    private static final String LOGIN_DIV = "loginDiv";
    private static final String FORM_ID = "loginForm";
    private static final String BUTTON_SUBMIT_ID = "loginSubmit";
    private static final String BUTTON_CLOSEB_ID = "loginClose";
    private static final String LABEL_MAIL_ID = "loginMailLabel";
    private static final String LOGIN_ID = "loginMail";
    private static final String LABEL_PASSWORD_ID = "loginPasswordLabel";
    private static final String PASSWORD_ID = "loginPassword";
    private static final String STATUS_ID = "loginStatus";

    private static final LocalizableMessages MSGS = GWT
            .create(LocalizableMessages.class);

    private SimpleIconLabel statusLabel = null;
    private FormPanel form;
    private LoginPopupPresenter presenter;

    @Override
    public void createView() {
        FlowPanel panel = new FlowPanel();
        initFastLoginForDevel(panel);
        initRealLoginForm(panel);
        setWidget(panel);
        this.setModal(true);
        this.setGlassEnabled(true);
        this.center();
        this.show();
        Document.get().getElementById(LOGIN_ID).focus();
    }

    // TODO delete
    private void initFastLoginForDevel(FlowPanel panel) {
        final ListBox list = new ListBox();
        list.setWidth("100%");
        list.setVisibleItemCount(privateUsers.size());
        list.insertItem("CLIENT", "CLIENT", 0);
        list.insertItem("SUPPLIER", "SUPPLIER", 1);
        list.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                String roleString = list.getValue(list.getSelectedIndex());
                Role role = Role.values()[Role.valueOf(roleString).ordinal()];
                setLogin(role);
            }


        });
        panel.add(list);
    }

    private void setLogin(Role role) {

        ((InputElement) Document.get().getElementById(LOGIN_ID)).setValue(privateUsers.get(role).getName());
        ((InputElement) Document.get().getElementById(PASSWORD_ID)).setValue(privateUsers.get(role).getPass());

    }

    /** binding real form to create login popup **/
    private void initRealLoginForm(FlowPanel panel) {
        // Get a handle to the form and set its action to our jsni method
        FormPanel form = FormPanel.wrap(Document.get().getElementById(FORM_ID), false);
        this.form = form;
        form.setAction("javascript:__gwt_login()");

        ButtonElement submit = (ButtonElement) Document.get().getElementById(BUTTON_SUBMIT_ID);
        Element close = (Element) Document.get().getElementById(BUTTON_CLOSEB_ID);

        Document.get().getElementById(LOGIN_ID).setTabIndex(1);
        Document.get().getElementById(PASSWORD_ID).setTabIndex(2);
        Document.get().getElementById(BUTTON_SUBMIT_ID).setTabIndex(3);
        Document.get().getElementById(BUTTON_CLOSEB_ID).setTabIndex(4);

        // Localization strings
        Document.get().getElementById(LABEL_MAIL_ID).setInnerText(MSGS.email());
        Document.get().getElementById(LABEL_PASSWORD_ID).setInnerText(MSGS.pass());
        submit.setInnerText(MSGS.submit());
        close.setInnerText(MSGS.close());

        // Sinking close event
        DOM.sinkEvents(close, Event.ONCLICK);
        DOM.setEventListener(close, new CloseListener());

        // Injecting the jsni method for handling the form submit
        injectLoginFunction(presenter);
        // Add the form to the panel
        panel.add(form);
    }

    // This is our JSNI method that will be called on form submit
    private native void injectLoginFunction(LoginPopupPresenter popupPresenter)
    /*-{
        $wnd.__gwt_login = function(){
        popupPresenter.@cz.poptavka.sample.client.main.login.LoginPopupPresenter::doLogin()();
        }
    }-*/;

    /** Called to hide popup. **/
    public void hidePopup() {
        Element loginDiv = (Element) Document.get().getElementById(LOGIN_DIV);
        Element statusDiv = (Element) Document.get().getElementById(STATUS_ID);

        // Append form back to page
        DOM.appendChild(loginDiv, form.getElement());
        //clear
        clearStatusLabel(statusDiv);

        this.hide();
    }

    /** Getters values from HTML form. */
    @Override
    public String getPassword() {
        return ((InputElement) Document.get().getElementById(PASSWORD_ID)).getValue();
    }

    @Override
    public String getLogin() {
        return ((InputElement) Document.get().getElementById(LOGIN_ID)).getValue();
    }

    @Override
    public boolean isValid() {
        if ((getLogin().length() == 0) || (getPassword().length() == 0)) {
            Element elem = (Element) Document.get().getElementById(STATUS_ID);
            clearStatusLabel(elem);
            statusLabel = new SimpleIconLabel(MSGS.wrongLoginMessage(), true);
            statusLabel.setImageResource(StyleResource.INSTANCE.images().errorIcon24());
            elem.appendChild(statusLabel.getElement());
            return false;
        }

        return true;
    }

    @Override
    public void setLoadingStatus() {
        Element elem = (Element) Document.get().getElementById(STATUS_ID);
        clearStatusLabel(elem);
        statusLabel = new SimpleIconLabel(MSGS.loading(), true);
        statusLabel.setImageResource(StyleResource.INSTANCE.images().loadIcon24());
        elem.appendChild(statusLabel.getElement());
    }

    @Override
    public void setUnknownError() {
        Element elem = (Element) Document.get().getElementById("loginStatus");
        clearStatusLabel(elem);
        statusLabel = new SimpleIconLabel("Wrong name", true);
        statusLabel.setImageResource(StyleResource.INSTANCE.images().errorIcon24());
        elem.appendChild(statusLabel.getElement());
    }

    @Override
    public void setLoginError() {
        Element elem = (Element) Document.get().getElementById("loginStatus");
        clearStatusLabel(elem);
        statusLabel = new SimpleIconLabel(MSGS.wrongLoginMessage(), true);

        statusLabel.setImageResource(StyleResource.INSTANCE.images().errorIcon24());
        elem.appendChild(statusLabel.getElement());
    }

    private void clearStatusLabel(Element parent) {
        if (parent.getChildCount() != 0) {
            // remove the only child
            parent.removeChild(parent.getFirstChild());
        }
    }

    @Override
    public void setPresenter(LoginPopupPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public LoginPopupPresenter getPresenter() {
        return presenter;
    }

    /**
     * private class to handle close button click
     */
    private class CloseListener implements EventListener {

        @Override
        public void onBrowserEvent(Event event) {
            presenter.hideView();
        }
    }
}
