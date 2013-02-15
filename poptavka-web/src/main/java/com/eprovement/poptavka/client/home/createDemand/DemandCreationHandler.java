package com.eprovement.poptavka.client.home.createDemand;

import com.eprovement.poptavka.client.common.security.SecuredAsyncCallback;
import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.service.demand.DemandCreationRPCServiceAsync;
import com.eprovement.poptavka.shared.domain.BusinessUserDetail;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail;
import com.google.inject.Inject;
import com.mvp4g.client.annotation.EventHandler;
import com.mvp4g.client.event.BaseEventHandler;
import java.util.logging.Logger;

/**
 * Handler for RPC calls for DemandCreationModule.
 *
 * @author Beho, Martin Slavkovsky
 *
 */
@EventHandler
public class DemandCreationHandler extends BaseEventHandler<DemandCreationEventBus> {

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private static final Logger LOGGER = Logger.getLogger("MainHandler");
    private DemandCreationRPCServiceAsync demandCreationService = null;

    /**************************************************************************/
    /* Inject RPCs                                                            */
    /**************************************************************************/
    @Inject
    void setDemandCreationModuleRPCServiceAsync(DemandCreationRPCServiceAsync service) {
        demandCreationService = service;
    }

    /**************************************************************************/
    /* Methods                                                                */
    /**************************************************************************/
    /**
     * Method registers new client and afterwards creates new demand.
     *
     * @param client newly created client
     */
    public void onRegisterNewClient(final BusinessUserDetail newClient) {
        demandCreationService.createNewClient(newClient, new SecuredAsyncCallback<BusinessUserDetail>(eventBus) {
            @Override
            public void onSuccess(BusinessUserDetail client) {
                if (client.getClientId() != -1) {
                    eventBus.loadingHide();
                    //popytaj ten overovaci kod
                    eventBus.initActivationCodePopup(newClient, Constants.CREATE_DEMAND);
                    //check if given client is the same as created client - for security ???
                }
            }
        });
    }

    /**
     * Creates new demand.
     *
     * @param detail
     *            front-end entity of demand
     * @param clientId
     *            client id
     */
    public void onCreateDemand(FullDemandDetail detail, Long clientId) {
        demandCreationService.createNewDemand(detail, clientId,
                new SecuredAsyncCallback<FullDemandDetail>(eventBus) {
                    @Override
                    public void onSuccess(FullDemandDetail result) {
                        eventBus.loadingHide();
                        eventBus.loadingShow(Storage.MSGS.demandCreationSuccessfullyCreated());
                        eventBus.goToClientDemandsModule(null, Constants.CLIENT_DEMANDS);
                    }
                });
        LOGGER.info("submitting new demand");
    }
}