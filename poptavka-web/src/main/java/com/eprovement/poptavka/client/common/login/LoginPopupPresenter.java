package com.eprovement.poptavka.client.common.login;

import com.eprovement.poptavka.client.common.SecuredAsyncCallback;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.inject.Inject;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.LazyPresenter;
import com.mvp4g.client.view.LazyView;

import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.common.errorDialog.ErrorDialogPopupView;
import com.eprovement.poptavka.client.root.RootEventBus;
import com.eprovement.poptavka.client.service.demand.MailRPCServiceAsync;
import com.eprovement.poptavka.client.service.demand.UserRPCServiceAsync;
import com.eprovement.poptavka.shared.domain.UserDetail;

@Presenter(view = LoginPopupView.class, multiple = true)
public class LoginPopupPresenter extends LazyPresenter<LoginPopupPresenter.LoginPopupInterface, RootEventBus> {

    private static final Logger LOGGER = Logger.getLogger(LoginPopupPresenter.class.getName());
    private static final int COOKIE_TIMEOUT = 1000 * 60 * 60 * 24;
    private MailRPCServiceAsync mailService = null;
    private ErrorDialogPopupView errorDialog;

    public interface LoginPopupInterface extends LazyView {

        boolean isValid();

        String getLogin();

        String getPassword();

        void hidePopup();

        void setLoadingStatus();

        void setUnknownError();

        void setLoginError();

        LoginPopupPresenter getPresenter();
    }
    @Inject
    private UserRPCServiceAsync userService;

    public void onLogin() {
        LOGGER.info("++ Login Popup Widget initialized ++");
    }

    /**
     * Real html login. SHOULD/WILL be used in prod
     */
    public void doLogin() {
        if (view.isValid()) {
            view.setLoadingStatus();
            String username = view.getLogin();
            String password = view.getPassword();
            RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                    URL.encode("/poptavka/j_spring_security_check"));
            builder.setHeader("Content-type", "application/x-www-form-urlencoded");
            try {
                Request request = builder.sendRequest("j_username=" + view.getLogin()
                        + "&j_password=" + view.getPassword(),
                        new RequestCallback() {

                            public void onError(Request request,
                                    Throwable exception) {
                                // Couldn't connect to server (could be timeout,
                                // SOP violation, etc.)
                                GWT.log("error during login");
                            }

                            public void onResponseReceived(Request request,
                                    Response response) {
                                if (200 == response.getStatusCode()) {
                                    // Process the response in
                                    GWT.log(response.getText());
                                } else {
                                    // Handle the error. Can get the status text
                                    // from response.getStatusText()
                                    view.setLoginError();
                                    return;
                                }
                            }
                        });
            } catch (RequestException e) {
                // Couldn't connect to server
                GWT.log("exception during login");
            }
            //DEVEL ONLY FOR FAST LOGIN
            userService.loginUser(username, password, new SecuredAsyncCallback<UserDetail>() {

                @Override
                protected void onServiceFailure(Throwable caught) {
                    // TODO: review this failure handling code
                    view.setUnknownError();
                }

                @Override
                public void onSuccess(UserDetail loggedUser) {
                    GWT.log("user id " + loggedUser.getUserId());
                    Storage.setUser(loggedUser);
                    final String sessionId = "id=" + loggedUser.getUserId();
                    if (sessionId != null) {
                        // TODO Praso - workaround for developoment purposes
                        setSessionID(sessionId);
                        //Martin: Change id = 149 to id = 613248 for testing new user and his demands
//                        setSessionID("id=149");
//                        setSessionID("id=613248");

                        //Martin - musi byt kvoli histori.
                        //Kedze tato metoda obsarava prihlasovanie, musel som ju zahrnut.
                        //Pretoze ak sa prihlasenie podari, musi sa naloadovat iny widget
                        //ako pri neuspesnom prihlaseni. Nie je sposob ako to zistit
                        //z history convertara "externe"
                        if (History.getToken().equals("atAccount")) {
                            eventBus.setHistoryStoredForNextOne(false);
                            eventBus.atAccount();
                            History.forward();
                            Storage.setActionLoginAccountHistory("back");
                        }
                        if (History.getToken().equals("atHome")) {
                            eventBus.setHistoryStoredForNextOne(false);
                            eventBus.atAccount();
                            History.back();
                            Storage.setActionLoginHomeHistory("forward");
                        }
                        if (!History.getToken().equals("atAccount")
                                && !History.getToken().equals("atHome")) {
                            eventBus.atAccount();
                            eventBus.goToDemandModule(null, Constants.NONE);
                        }
                        hideView();
                    } else {
                        view.setLoginError();
                    }
                }
            });
            //DEVEL ONLY FOR FAST SUPPLIER LOGIN
//            This is the reason why the atAccountMethod is called twice
//            setSessionID("id=149");
//            eventBus.atAccount();
//            hideView();
        }
    }

    @Inject
    void setMailService(MailRPCServiceAsync service) {
        mailService = service;
    }

    public void hideView() {
        eventBus.removeHandler(view.getPresenter());
        view.hidePopup();
    }

    /**
     * Sets session ID *
     */
    private void setSessionID(String sessionId) {
//        LOGGER.fine("Setting SID cookie");
//        int cookieTimeout = COOKIE_TIMEOUT;
//        Date expires = new Date((new Date()).getTime() + cookieTimeout);
//        Cookies.setCookie("sid", sessionId);
    }

    /**
     * Method logs out the user via RequestBuilder request. Session should be
     * invalidated here.
     */
    public void onLogout() {
        GWT.log("logout");
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
                URL.encode("/poptavka/j_spring_security_logout"));
        builder.setHeader("Content-type", "application/x-www-form-urlencoded");
        //remove user from session management to force user input login information
        Storage.setUser(null);
        try {
            Request request = builder.sendRequest(null,
                    new RequestCallback() {

                        public void onError(Request request,
                                Throwable exception) {
                        }

                        public void onResponseReceived(Request request,
                                Response response) {
                            GWT.log("user has loged out after receiving response");
                            hideView();
                        }
                    });
        } catch (RequestException e) {
            // Couldn't connect to server
            GWT.log("exception during login");
        }
        GWT.log("User has logged out");
    }
}
