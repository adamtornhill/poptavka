package com.eprovement.poptavka.client.root.footer;

import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.root.BaseChildEventBus;
import com.eprovement.poptavka.client.root.info.FooterInfo;
import com.eprovement.poptavka.client.root.info.FooterInfo.FooterInfoViews;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;

import com.eprovement.poptavka.client.root.interfaces.IFooterView;
import com.eprovement.poptavka.client.root.interfaces.IFooterView.IFooterPresenter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.mvp4g.client.event.EventBusWithLookup;
import java.util.Date;

@Presenter(view = FooterView.class)
public class FooterPresenter extends BasePresenter<IFooterView, EventBusWithLookup>
        implements IFooterPresenter {

    @Override
    public void bind() {
        view.getContactUs().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ((BaseChildEventBus) eventBus).sendUsEmail(Constants.SUBJECT_REPORT_ISSUE, (new Date()).toString());
            }
        });
        view.getAboutUs().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                History.newItem(Constants.PATH_TO_TOKEN_FOR_VIEWS.concat(FooterInfoViews.ABOUT_US.getValue()));
                ((BaseChildEventBus) eventBus).setBody(FooterInfo.createAboutUs());
            }
        });
        view.getFAQ().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                History.newItem(Constants.PATH_TO_TOKEN_FOR_VIEWS.concat(FooterInfoViews.FAQ.getValue()));
                ((BaseChildEventBus) eventBus).setBody(FooterInfo.createFAQ());
            }
        });
        view.getPrivacyPolicy().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                History.newItem(Constants.PATH_TO_TOKEN_FOR_VIEWS.concat(FooterInfoViews.PRIVACY_POLICY.getValue()));
                ((BaseChildEventBus) eventBus).setBody(FooterInfo.createPrivacyPolicy());
            }
        });
    }
}
