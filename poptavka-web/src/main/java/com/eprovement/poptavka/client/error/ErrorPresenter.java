/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.client.error;

import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.error.interfaces.IErrorView;
import com.eprovement.poptavka.client.error.interfaces.IErrorView.IErrorPresenter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;

/**
 *
 * @author ivlcek
 */
@Presenter(view = ErrorView.class)
public class ErrorPresenter extends BasePresenter<IErrorView, ErrorEventBus> implements
        IErrorPresenter {

    private String errorId;

    /**************************************************************************/
    /* General Module events                                                  */
    /**************************************************************************/
    public void onStart() {
        // nothing
    }

    public void onForward() {
        // TODO ivlcek - is the line bellow necessary? Ask Martin
//        eventBus.setUpSearchBar(null, false, false, false);
    }


    /**
     * Bind objects and their action handlers.
     */
    @Override
    public void bind() {
        view.getReportinButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // display popup for reporting error to customer support
                eventBus.sendUsEmail(Constants.SUBJECT_REPORT_ISSUE, errorId);
            }
        });
    }

    /**************************************************************************/
    /* Navigation events                                                      */
    /**************************************************************************/
    public void onDisplayError(int errorResponseCode, String errorId) {
        this.errorId = errorId;
        view.setErrorResponseCode(errorResponseCode);
        view.setErrorId(errorId);
    }

    /**************************************************************************/
    /* Business events handled by presenter                                   */
    /**************************************************************************/


    /**************************************************************************/
    /* Business events handled by eventbus or RPC                             */
    /**************************************************************************/

}

