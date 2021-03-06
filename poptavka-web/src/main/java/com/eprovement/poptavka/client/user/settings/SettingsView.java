/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.user.settings;

import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.CssInjector;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.user.settings.interfaces.ISettings;
import com.eprovement.poptavka.client.user.settings.toolbar.SettingsToolbarView;
import com.eprovement.poptavka.resources.StyleResource;
import com.eprovement.poptavka.shared.domain.BusinessUserDetail;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * View consists of left vertical menu and content area for placing module's widgets.
 * @author Martin Slavkovsky
 */
public class SettingsView extends Composite implements ISettings.View {

    /**************************************************************************/
    /* UiBinder                                                               */
    /**************************************************************************/
    private static SettingsViewUiBinder uiBinder = GWT.create(SettingsViewUiBinder.class);

    interface SettingsViewUiBinder extends UiBinder<Widget, SettingsView> {
    }

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    /** UiBinder attributes. **/
    @UiField Button menuUserBtn, menuClientBtn, menuSupplierBtn, menuSystemBtn, menuSecurityBtn;
    @UiField SimplePanel contentContainer, footerContainer;
    /** Class attribute. **/
    @Inject
    private SettingsToolbarView toolbar;

    /**************************************************************************/
    /* Initialization                                                         */
    /**************************************************************************/
    /**
     * Creates Settings view's compontents.
     */
    @Override
    public void createView() {
        initWidget(uiBinder.createAndBindUi(this));
        if (Storage.getBusinessUserDetail() != null
            && !Storage.getBusinessUserDetail().getBusinessRoles().contains(
                BusinessUserDetail.BusinessRole.CLIENT)) {
            menuClientBtn.setVisible(false);
        }

        CssInjector.INSTANCE.ensureCommonStylesInjected();
        StyleResource.INSTANCE.modal().ensureInjected();
    }

    /**************************************************************************/
    /*  Methods handled by view                                               */
    /**************************************************************************/
    /**
     * Allows supplier settings button in menu if logged user is a supplier.
     * @param visible true to display, false to hide
     */
    @Override
    public void allowSupplierSettings(boolean visible) {
        menuSupplierBtn.setVisible(visible);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void settingsTabStyleChange(ISettings.SettingsTab tab) {
        //Remove ACT style from all tabs
        menuUserBtn.removeStyleName(Constants.ACT);
        menuClientBtn.removeStyleName(Constants.ACT);
        menuSupplierBtn.removeStyleName(Constants.ACT);
        menuSystemBtn.removeStyleName(Constants.ACT);
        menuSecurityBtn.removeStyleName(Constants.ACT);
        //Add ACT style to given tab
        switch (tab) {
            case USER:
                menuUserBtn.addStyleName(Constants.ACT);
                break;
            case CLIENT:
                menuClientBtn.addStyleName(Constants.ACT);
                break;
            case SUPPLIER:
                menuSupplierBtn.addStyleName(Constants.ACT);
                break;
            case SYSTEM:
                menuSystemBtn.addStyleName(Constants.ACT);
                break;
            case SECURITY:
                menuSecurityBtn.addStyleName(Constants.ACT);
                break;
            default:
                break;
        }
    }

    /**************************************************************************/
    /*  Getters                                                               */
    /**************************************************************************/
    /**
     * {@inheritDoc}
     */
    @Override
    public SimplePanel getContentPanel() {
        return contentContainer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimplePanel getFooterContainer() {
        return footerContainer;
    }

    /**
     * @return the custom toolbar widget
     */
    @Override
    public Widget getToolbarContent() {
        return toolbar;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Button getTabBtn(ISettings.SettingsTab tab) {
        switch (tab) {
            case USER:
                return menuUserBtn;
            case CLIENT:
                return menuClientBtn;
            case SUPPLIER:
                return menuSupplierBtn;
            case SYSTEM:
                return menuSystemBtn;
            case SECURITY:
                return menuSecurityBtn;
            default:
                return null;
        }
    }
}
