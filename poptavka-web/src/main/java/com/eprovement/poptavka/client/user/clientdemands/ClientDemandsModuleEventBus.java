/*
 * Copyright (C), eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.client.user.clientdemands;

import com.eprovement.poptavka.client.common.BaseChildEventBus;
import com.eprovement.poptavka.client.detail.interfaces.IDetailModule;
import com.eprovement.poptavka.client.home.createDemand.interfaces.IDemandCreationModule;
import com.eprovement.poptavka.client.home.createSupplier.interfaces.ISupplierCreationModule;
import com.eprovement.poptavka.client.root.gateways.ActionBoxGateway;
import com.eprovement.poptavka.client.root.gateways.CatLocSelectorGateway;
import com.eprovement.poptavka.client.user.clientdemands.interfaces.HandleClientResizeEvent;
import com.eprovement.poptavka.client.user.clientdemands.widgets.ClientAssignedDemandsPresenter;
import com.eprovement.poptavka.client.user.clientdemands.widgets.ClientDemandsPresenter;
import com.eprovement.poptavka.client.user.clientdemands.widgets.ClientDemandsWelcomePresenter;
import com.eprovement.poptavka.client.user.clientdemands.widgets.ClientOffersPresenter;
import com.eprovement.poptavka.client.user.clientdemands.widgets.ClientRatingsPresenter;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid.IEventBusData;
import com.eprovement.poptavka.shared.domain.clientdemands.ClientDashboardDetail;
import com.eprovement.poptavka.shared.domain.clientdemands.ClientDemandConversationDetail;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail;
import com.eprovement.poptavka.shared.domain.message.UnreadMessagesDetail;
import com.eprovement.poptavka.shared.domain.offer.ClientOfferedDemandOffersDetail;
import com.eprovement.poptavka.shared.search.SearchDefinition;
import com.eprovement.poptavka.shared.search.SearchModuleDataHolder;
import com.google.gwt.user.client.ui.IsWidget;
import com.mvp4g.client.annotation.Debug;
import com.mvp4g.client.annotation.Event;
import com.mvp4g.client.annotation.Events;
import com.mvp4g.client.annotation.Forward;
import com.mvp4g.client.annotation.Start;
import com.mvp4g.client.event.EventBusWithLookup;
import java.util.List;

/**
 * Client section for managing projects.
 *
 * @author Martin Slavkovsky
 */
@Debug(logLevel = Debug.LogLevel.DETAILED)
@Events(startPresenter = ClientDemandsModulePresenter.class, module = ClientDemandsModule.class)
public interface ClientDemandsModuleEventBus extends EventBusWithLookup, IEventBusData,
        BaseChildEventBus, IDetailModule.Gateway, CatLocSelectorGateway, ActionBoxGateway,
        ISupplierCreationModule.Gateway,
        IDemandCreationModule.Gateway {

    /**************************************************************************/
    /* General Module events                                                  */
    /**************************************************************************/
    /**
     * First event to be handled.
     */
    @Start
    @Event(handlers = ClientDemandsModulePresenter.class)
    void start();

    /**
     * Forward event is called only if it is configured here. If there is nothing to carry out
     * in this method we should remove forward event to save the number of method invocations.
     */
    @Forward
    @Event(handlers = ClientDemandsModulePresenter.class, navigationEvent = true)
    void forward();

    /**************************************************************************/
    /* Parent events + DetailsWrapper related                                 */
    /**************************************************************************/
    @Event(handlers = ClientDemandsModulePresenter.class)
    void loadingDivShow(String loadingMessage);

    @Event(handlers = ClientDemandsModulePresenter.class)
    void loadingDivHide();

    /**************************************************************************/
    /* History events                                                         */
    /**************************************************************************/
    @Event(historyConverter = ClientDemandsModuleHistoryConverter.class, name = "token")
    String createTokenForHistory();

    /**************************************************************************/
    /* Navigation events.                                                     */
    /**************************************************************************/
    //Module navigation
    //--------------------------------------------------------------------------
    /**
     * The only entry point to this module. This module is not asynchronous.
     *
     * @param filter - defines data holder to be displayed in advanced search bar
     */
    @Event(handlers = ClientDemandsModulePresenter.class, navigationEvent = true)
    void goToClientDemandsModule(SearchModuleDataHolder filterm, int loadWidget);

    //Init by default
    //--------------------------------------------------------------------------
    @Event(handlers = ClientDemandsWelcomePresenter.class)
    void initClientDemandsWelcome();

    @Event(handlers = ClientDemandsPresenter.class)
    void initClientDemands(SearchModuleDataHolder filter);

    @Event(handlers = ClientOffersPresenter.class)
    void initClientOffers(SearchModuleDataHolder filter);

    @Event(handlers = ClientAssignedDemandsPresenter.class)
    void initClientAssignedDemands(SearchModuleDataHolder filter);

    @Event(handlers = ClientAssignedDemandsPresenter.class)
    void initClientClosedDemands(SearchModuleDataHolder filter);

    @Event(handlers = ClientRatingsPresenter.class)
    void initClientRatings(SearchModuleDataHolder filter);

    /**************************************************************************/
    /* Navigation Parent events */
    /**************************************************************************/
    @Event(forwardToParent = true)
    void atAccount();

    @Event(forwardToParent = true)
    void goToHomeDemandsModule(SearchModuleDataHolder filter);

    @Event(forwardToParent = true)
    void goToHomeSuppliersModule(SearchModuleDataHolder filter);

    @Event(forwardToParent = true)
    void setUpdatedUnreadMessagesCount(UnreadMessagesDetail numberOfMessages);

    /**************************************************************************/
    /* Common event                                                           */
    /**************************************************************************/
    @Event(broadcastTo = HandleClientResizeEvent.class, passive = true)
    void resize(int actualWidth);

    /**************************************************************************/
    /* Business events handled by ClientDemandsModulePresenter.               */
    /**************************************************************************/
    // Forward methods don't need history converter because they have its owns
    @Event(handlers = ClientDemandsModulePresenter.class)
    void displayView(IsWidget content);

    /**************************************************************************/
    /* Business events handled by ClientWelcomePresenter.                     */
    /**************************************************************************/
    @Event(handlers = ClientDemandsWelcomePresenter.class)
    void loadClientDashboardDetail(ClientDashboardDetail result);

    /**************************************************************************/
    /* Business events handled by ClientDemandsPresenter.                     */
    /**************************************************************************/
    @Event(handlers = ClientDemandsPresenter.class)
    void displayClientDemandConversations(List<ClientDemandConversationDetail> result);

    @Event(handlers = ClientDemandsPresenter.class)
    void responseConversationNoData();

    /**************************************************************************/
    /* Business events handled by ClientOffersPresenter.                      */
    /**************************************************************************/
    @Event(handlers = ClientOffersPresenter.class)
    void displayClientOfferedDemandOffers(List<ClientOfferedDemandOffersDetail> result);

    /**************************************************************************/
    /* Business events handled by ClientAssignedDemandsPresenter.             */
    /**************************************************************************/
    @Event(handlers = ClientAssignedDemandsPresenter.class)
    void responseFeedback();

    /**************************************************************************/
    /* Business events handled by Handlers.                                   */
    /**************************************************************************/
    /*
     * Request/Response Method pair fo AcceptOffer.
     * @param offerId
     */
    @Event(handlers = ClientDemandsModuleHandler.class)
    void requestAcceptOffer(long offerid);

    @Event(handlers = ClientOffersPresenter.class)
    void responseAcceptOffer();

    @Event(handlers = ClientDemandsModuleHandler.class)
    void updateUnreadMessagesCount();

    @Event(handlers = ClientDemandsModuleHandler.class)
    void requestCloseAndRateSupplier(long demandID, long offerID, Integer rating, String comment);

    @Event(handlers = ClientDemandsModuleHandler.class)
    void getClientDashboardDetail();

    @Event(handlers = ClientDemandsModuleHandler.class)
    void requestSubstractCredit(long offerId, int credits);

    /**************************************************************************/
    /* Business events for demand's CRUD operations                           */
    /**************************************************************************/
    @Event(handlers = ClientDemandsModuleHandler.class)
    void requestDeleteDemand(long demandId);

    @Event(handlers = ClientDemandsPresenter.class)
    void responseDeleteDemand(boolean result);

    @Event(handlers = ClientDemandsModuleHandler.class)
    void requestUpdateDemand(long demandId, FullDemandDetail updatedDemand);

    @Event(handlers = ClientDemandsPresenter.class)
    void responseUpdateDemand(FullDemandDetail result);

    /**************************************************************************/
    /* Overriden methods of IEventBusData interface. */
    /* Should be called only from UniversalAsyncGrid. */
    /**************************************************************************/
    @Override
    @Event(handlers = ClientDemandsModuleHandler.class)
    void getDataCount(UniversalAsyncGrid grid, SearchDefinition searchDefinition);

    @Override
    @Event(handlers = ClientDemandsModuleHandler.class)
    void getData(UniversalAsyncGrid grid, SearchDefinition searchDefinition, int requestId);

    /**************************************************************************/
    /* Client Demands MENU                                                    */
    /**************************************************************************/
    @Event(handlers = ClientDemandsModulePresenter.class)
    void clientDemandsMenuStyleChange(int loadedWidget);
}
