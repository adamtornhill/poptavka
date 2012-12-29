/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eprovement.poptavka.client.common.address;

import com.eprovement.poptavka.client.common.SimpleIconLabel;
import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.Storage;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Class needed to access SuggestBox's popup.
 * Class also contain another popup to display loading information.
 *
 * @author Martin Slavkovsky
 */
public class MySuggestDisplay extends SuggestBox.DefaultSuggestionDisplay {

    /**************************************************************************/
    /* ATTRIBUTES                                                             */
    /**************************************************************************/
    private static final int OFFSET_LEFT = 0;
    private static final int OFFSET_TOP = 20;
    private SimpleIconLabel loader = new SimpleIconLabel();
    private CitySuggestOracle oracle = null;
    private PopupPanel loadingPopup = new PopupPanel(true);

    /**************************************************************************/
    /* INITIALIZATION                                                         */
    /**************************************************************************/
    public MySuggestDisplay() {
        loader.setImageResource(Storage.RSCS.images().loadIcon32());
    }

    /**************************************************************************/
    /* GETTERS                                                                */
    /**************************************************************************/
    @Override
    public PopupPanel getPopupPanel() {
        return super.getPopupPanel();
    }

    /**************************************************************************/
    /* SETTERS                                                                */
    /**************************************************************************/
    public void setOracle(CitySuggestOracle oracle) {
        this.oracle = oracle;
    }

    public void setLoadingPopupPosition(SuggestBox suggestBox) {
        loadingPopup.setPopupPosition(
                DOM.getAbsoluteLeft(suggestBox.getElement()) + OFFSET_LEFT,
                DOM.getAbsoluteTop(suggestBox.getElement()) + OFFSET_TOP);
    }

    /**************************************************************************/
    /* SHOW                                                                   */
    /**************************************************************************/
    public void showNoCitiesFound() {
        VerticalPanel vp = new VerticalPanel();
        vp.add(new Label(Storage.MSGS.addressNoCityFound()));
        Anchor anchor = new Anchor(Storage.MSGS.report());
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO implement report feature
            }
        });
        vp.add(anchor);
        loadingPopup.setWidget(vp);
        loadingPopup.show();
    }

    public void showShortCitiesInfo(int minCityChars) {
        VerticalPanel vp = new VerticalPanel();
        vp.add(new Label(Storage.MSGS.addressLoadingInfoLabel(Constants.MIN_CHARS_TO_SEARCH)));
        Anchor anchor = new Anchor(Storage.MSGS.addressMyCityIsLessInfoLabel(Constants.MIN_CHARS_TO_SEARCH));
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                oracle.requestShortCitySuggestions();
            }
        });
        vp.add(anchor);
        loadingPopup.setWidget(vp);
        loadingPopup.show();
    }

    public void showLoading() {
        loadingPopup.setWidget(loader);
        loadingPopup.show();
    }

    /**************************************************************************/
    /* HIDE                                                                   */
    /**************************************************************************/
    @Override
    public void hideSuggestions() {
        super.hideSuggestions();
    }

    public void hideLoadingPopup() {
        loadingPopup.hide();
    }
}
