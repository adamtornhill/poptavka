/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.login.activation;

import com.eprovement.poptavka.client.login.LoginEventBus;
import com.eprovement.poptavka.client.common.security.SecuredAsyncCallback;
import com.eprovement.poptavka.client.service.login.LoginRPCServiceAsync;
import com.eprovement.poptavka.shared.domain.BusinessUserDetail;
import com.eprovement.poptavka.shared.domain.root.UserActivationResult;
import com.google.inject.Inject;
import com.mvp4g.client.annotation.EventHandler;
import com.mvp4g.client.event.BaseEventHandler;

@EventHandler
public class ActivationCodePopupHandler extends BaseEventHandler<LoginEventBus> {

    @Inject
    private LoginRPCServiceAsync loginService;

    /**************************************************************************/
    /* Activation methods                                                     */
    /**************************************************************************/
    public void onActivateUser(BusinessUserDetail user, String activationCode) {
        loginService.activateUser(user, activationCode, new SecuredAsyncCallback<UserActivationResult>(eventBus) {
            @Override
            public void onSuccess(UserActivationResult result) {
                eventBus.responseActivateUser(result);
            }
        });
    }

    public void onSendActivationCodeAgain(BusinessUserDetail user) {
        loginService.sendActivationCodeAgain(user, new SecuredAsyncCallback<Boolean>(eventBus) {
            @Override
            public void onSuccess(Boolean activationResult) {
                eventBus.responseSendActivationCodeAgain(activationResult);
            }
        });
    }

    public void onCheckActivationEmail(BusinessUserDetail user) {
        loginService.hasActivationEmail(user, new SecuredAsyncCallback<Void>(eventBus) {
            @Override
            public void onSuccess(Void emailSend) {
            }
        });
    }
}
