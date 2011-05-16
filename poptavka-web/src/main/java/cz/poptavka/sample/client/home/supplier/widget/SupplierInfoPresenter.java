package cz.poptavka.sample.client.home.supplier.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.LocalizableMessages;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.LazyPresenter;
import com.mvp4g.client.view.LazyView;

import cz.poptavka.sample.client.main.MainEventBus;
import cz.poptavka.sample.client.main.common.StatusIconLabel;
import cz.poptavka.sample.client.main.common.StatusIconLabel.State;
import cz.poptavka.sample.shared.domain.SupplierDetail;

@Presenter(view = SupplierInfoView.class, multiple = true)
public class SupplierInfoPresenter extends
    LazyPresenter<SupplierInfoPresenter.SupplierInfoInterface, MainEventBus> {

    private static final LocalizableMessages MSGS = GWT.create(LocalizableMessages.class);

    public interface SupplierInfoInterface extends LazyView {

        Widget getWidgetView();

        HasValueChangeHandlers<String> getEmailBox();

        PasswordTextBox getPwdBox();

        PasswordTextBox getPwdConfirmBox();

        StatusIconLabel getMailStatus();

        StatusIconLabel getPwdStatus();

        StatusIconLabel getPwdConfirmStatus();

        SupplierDetail getBaseSupplier();

        SupplierDetail createSupplier();

        void setMailFlag(boolean flag);

        void setPasswordFlag(boolean flag);

        void setPasswordLengthFlag(boolean flag);

    }

    @Override
    public void bindView() {
        view.getEmailBox().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> value) {
                /** UI method **/
                initVisualMailCheck(value.getValue());
            }
        });
        view.getPwdBox().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                initVisualPwdCheck(view.getPwdBox().getText());
            }
        });
        view.getPwdConfirmBox().addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent arg0) {
                initVisualPwdConfirmCheck();
            }
        });
    }

    public void onInitSupplierForm(SimplePanel embedToWidget) {
        embedToWidget.setWidget(view.getWidgetView());
    }

    public void onCheckFreeEmailResponse(Boolean isAvailable) {
        if (isAvailable) {
            view.setMailFlag(true);
            // TODO change to global status changer eventBus call
            view.getMailStatus().setState(State.ACCEPT_16);
            view.getMailStatus().setDescription(MSGS.mailAvailable());
        } else {
            view.setMailFlag(false);
            // TODO change to global status changer eventBus call
            view.getMailStatus().setState(State.ERROR_16);
        }
    }

    /** Visualization methods **/
    private void initVisualMailCheck(String value) {
        // TODO change to global status changer eventBus call
        view.getMailStatus().setState(State.LOAD_24);
        view.getMailStatus().setVisible(true);
        if (value.contains("@") && value.contains(".")) {
            eventBus.checkFreeEmail(value);
        } else {
            // TODO change to global status changer eventBus call
            view.getMailStatus().setStateWithDescription(State.ERROR_16, MSGS.malformedEmail());
        }
    }

    private static final int SHORT = 5;
    private static final int LONG = 8;

    private void initVisualPwdCheck(String value) {
        view.getPwdStatus().setVisible(true);
        if (value.length() <= SHORT) {

            // TODO change to global status changer eventBus call
            view.getPwdStatus().setStateWithDescription(State.ERROR_16, MSGS.shortPassword());
            view.setPasswordLengthFlag(false);
            return;
        }
        view.setPasswordLengthFlag(true);
        if ((value.length() <= LONG) && (value.length() > SHORT)) {

            // TODO change to global status changer eventBus call
            view.getPwdStatus().setStateWithDescription(State.INFO_16, MSGS.semiStrongPassword());
        }
        if (value.length() > LONG) {

            // TODO change to global status changer eventBus call
            view.getPwdStatus().setStateWithDescription(State.ACCEPT_16, MSGS.strongPassword());
        }
    }

    private void initVisualPwdConfirmCheck() {
        view.getPwdConfirmStatus().setVisible(true);
        if (!view.getPwdBox().getText().equals(view.getPwdConfirmBox().getText())) {
            view.setMailFlag(false);

            // TODO change to global status changer eventBus call
            view.getPwdConfirmStatus().setStateWithDescription(State.ERROR_16, MSGS.passwordsUnmatch());
        } else {
            view.setMailFlag(true);

            // TODO change to global status changer eventBus call
            view.getPwdConfirmStatus().setStateWithDescription(State.ACCEPT_16, MSGS.passwordsMatch());
        }
    }
}
