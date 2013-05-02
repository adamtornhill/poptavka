package com.eprovement.poptavka.client.root.activation;

import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.common.smallPopups.ThankYouPopup;
import com.eprovement.poptavka.client.root.RootEventBus;
import com.eprovement.poptavka.shared.domain.BusinessUserDetail;
import com.eprovement.poptavka.shared.domain.root.UserActivationResult;
import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocalizableMessages;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.LazyPresenter;
import com.mvp4g.client.view.LazyView;
import java.util.Date;

/**
 *
 * @author Martin Slavkovsky
 */
@Presenter(view = ActivationCodePopupView.class, multiple = true)
public class ActivationCodePopupPresenter
        extends LazyPresenter<ActivationCodePopupPresenter.ActivationCodePopupInterface, RootEventBus> {

    /**************************************************************************/
    /* ATTRIBUTES                                                             */
    /**************************************************************************/
    /** Class attributes. **/
    private static final LocalizableMessages MSGS = GWT.create(LocalizableMessages.class);
    private int widgetToLoad = Constants.NONE;
    private BusinessUserDetail user;

    /**************************************************************************/
    /* VIEW INTERFACE                                                         */
    /**************************************************************************/
    public interface ActivationCodePopupInterface extends LazyView {

        /** TEXTBOX. **/
        TextBox getActivationCodeBox();

        /** BUTTONS. **/
        Button getCloseButton();

        Button getActivateButton();

        Button getSendAgainButton();

        Button getReportButton();

        /** WIDGET. **/
        Alert getStatus();

        ActivationCodePopupView getWidgetView();
    }

    /**************************************************************************/
    /* BIND                                                                   */
    /**************************************************************************/
    @Override
    public void bindView() {
        view.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((PopupPanel) view.getWidgetView()).hide();
                Timer additionalAction = new Timer() {
                    @Override
                    public void run() {
                        eventBus.goToHomeWelcomeModule();
                    }
                };
                ThankYouPopup.create(Storage.MSGS.thankYouActivationClose(), additionalAction);
            }
        });
        view.getActivateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.activateUser(user, view.getActivationCodeBox().getText());
            }
        });
        view.getSendAgainButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                eventBus.sendActivationCodeAgain(user);
            }
        });
        view.getReportButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                view.getWidgetView().hide();
                //presmerovat na root module kde sa toto zavola
                eventBus.sendUsEmail(Constants.SUBJECT_REPORT_ISSUE, (new Date()).toString());
            }
        });
    }

    /**************************************************************************/
    /* Initialization                                                         */
    /**************************************************************************/
    public void initActivationCodePopup(BusinessUserDetail user, int widgetToLoad) {
        this.user = user;
        this.widgetToLoad = widgetToLoad;
        view.getStatus().setHeading(MSGS.activationCodeSent() + " " + user.getEmail());
    }

    /**************************************************************************/
    /* Response methods                                                       */
    /**************************************************************************/
    public void onResponseActivateUser(UserActivationResult activationResult) {
        if (activated) {
            view.getStatus().setType(AlertType.SUCCESS);
        } else {
            view.getStatus().setType(AlertType.ERROR);
        }

        //inform user
        switch (activationResult) {
            case OK:
                reportActivationSuccessAndLoginUser();
                break;
            case ERROR_UNKNOWN_USER:
                reportActivationFailure(MSGS.activationFailedUnknownUser());
                break;
            case ERROR_INCORRECT_ACTIVATION_CODE:
                reportActivationFailure(MSGS.activationFailedIncorrectActivationCode());
                break;
            case ERROR_EXPIRED_ACTIVATION_CODE:
                reportActivationFailure(MSGS.activationFailedExpiredActivationCode());
                break;
            default:
                reportActivationFailure(MSGS.activationFailedUnknownError());
        }

    }

    public void onResponseSendActivationCodeAgain(boolean sent) {
        if (sent) {
            view.getStatus().setType(AlertType.SUCCESS);
        } else {
            view.getStatus().setType(AlertType.ERROR);
        }

        //inform user
        if (sent) {
            view.getStatus().setHeading(
                    MSGS.activationNewCodeSent() + " " + user.getEmail());
        } else {
            reportActivationFailure(MSGS.activationFailedSentNewCode());
        }
    }

    private void reportActivationSuccessAndLoginUser() {
        view.getStatus().setHeading(MSGS.activationPassed());
        view.getStatus().setType(AlertType.SUCCESS);
        //close activation popup
        ((PopupPanel) view.getWidgetView()).hide();
        //login user automatically
        Timer additionalAction = new Timer() {
            @Override
            public void run() {
                eventBus.autoLogin(
                        user.getEmail(),
                        user.getPassword(),
                        widgetToLoad);
            }
        };
        ThankYouPopup.create(Storage.MSGS.thankYouCodeActivated(), additionalAction);
    }

    private void reportActivationFailure(String errorMessage) {
        view.getStatus().setHeading(errorMessage);
        view.getStatus().setType(AlertType.ERROR);
        view.getReportButton().setVisible(true);
    }
}
