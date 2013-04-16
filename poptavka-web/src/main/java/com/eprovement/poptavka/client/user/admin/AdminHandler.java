package com.eprovement.poptavka.client.user.admin;

import com.eprovement.poptavka.client.common.security.SecuredAsyncCallback;
import com.eprovement.poptavka.client.common.session.Constants;
import com.eprovement.poptavka.client.common.session.Storage;
import com.eprovement.poptavka.client.service.demand.AdminRPCServiceAsync;
import com.eprovement.poptavka.client.user.widget.grid.UniversalAsyncGrid;
import com.eprovement.poptavka.shared.domain.adminModule.AccessRoleDetail;
import com.eprovement.poptavka.shared.domain.adminModule.ActivationEmailDetail;
import com.eprovement.poptavka.shared.domain.adminModule.ClientDetail;
import com.eprovement.poptavka.shared.domain.adminModule.InvoiceDetail;
import com.eprovement.poptavka.shared.domain.adminModule.OfferDetail;
import com.eprovement.poptavka.shared.domain.adminModule.PaymentDetail;
import com.eprovement.poptavka.shared.domain.adminModule.PaymentMethodDetail;
import com.eprovement.poptavka.shared.domain.adminModule.PermissionDetail;
import com.eprovement.poptavka.shared.domain.adminModule.PreferenceDetail;
import com.eprovement.poptavka.shared.domain.adminModule.ProblemDetail;
import com.eprovement.poptavka.shared.domain.ChangeDetail;
import com.eprovement.poptavka.shared.domain.demand.FullDemandDetail;
import com.eprovement.poptavka.shared.domain.message.MessageDetail;
import com.eprovement.poptavka.shared.domain.message.UnreadMessagesDetail;
import com.eprovement.poptavka.shared.domain.supplier.FullSupplierDetail;
import com.eprovement.poptavka.shared.search.SearchDefinition;
import com.google.inject.Inject;
import com.mvp4g.client.annotation.EventHandler;
import com.mvp4g.client.event.BaseEventHandler;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@EventHandler
public class AdminHandler extends BaseEventHandler<AdminEventBus> {

    @Inject
    private AdminRPCServiceAsync adminService = null;

    //*************************************************************************/
    // Overriden methods of IEventBusData interface.                          */
    //*************************************************************************/
    public void onGetDataCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        switch (Storage.getCurrentlyLoadedView()) {
            case Constants.ADMIN_ACCESS_ROLE:
                getAdminAccessRolesCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_CLIENTS:
                getAdminClientsCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_DEMANDS:
                getAdminDemandsCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_EMAILS_ACTIVATION:
                getAdminEmailsActivationCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_INVOICES:
                getAdminInvoicesCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_MESSAGES:
                getAdminMessagesCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_NEW_DEMANDS:
                getAdminNewDemandsCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_OFFERS:
                getAdminOffersCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_OUR_PAYMENT_DETAILS:
                getAdminOurPaymentDetailsCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_PAYMENT_METHODS:
                getAdminOurPaymentDetailsCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_PERMISSIONS:
                getAdminPermissionsCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_PREFERENCES:
                getAdminPreferencesCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_PROBLEMS:
                getAdminProblemsCount(grid, searchDefinition);
                break;
            case Constants.ADMIN_SUPPLIERS:
                getAdminSuppliersCount(grid, searchDefinition);
                break;
            default:
                break;
        }
    }

    public void onGetData(SearchDefinition searchDefinition) {
        switch (Storage.getCurrentlyLoadedView()) {
            case Constants.ADMIN_ACCESS_ROLE:
                getAdminAccessRoles(searchDefinition);
                break;
            case Constants.ADMIN_CLIENTS:
                getAdminClients(searchDefinition);
                break;
            case Constants.ADMIN_DEMANDS:
                getAdminDemands(searchDefinition);
                break;
            case Constants.ADMIN_EMAILS_ACTIVATION:
                getAdminEmailsActivation(searchDefinition);
                break;
            case Constants.ADMIN_INVOICES:
                getAdminInvoices(searchDefinition);
                break;
            case Constants.ADMIN_MESSAGES:
                getAdminMessages(searchDefinition);
                break;
            case Constants.ADMIN_NEW_DEMANDS:
                getAdminNewDemands(searchDefinition);
                break;
            case Constants.ADMIN_OFFERS:
                getAdminOffers(searchDefinition);
                break;
            case Constants.ADMIN_OUR_PAYMENT_DETAILS:
                getAdminOurPaymentDetails(searchDefinition);
                break;
            case Constants.ADMIN_PAYMENT_METHODS:
                getAdminOurPaymentDetails(searchDefinition);
                break;
            case Constants.ADMIN_PERMISSIONS:
                getAdminPermissions(searchDefinition);
                break;
            case Constants.ADMIN_PREFERENCES:
                getAdminPreferences(searchDefinition);
                break;
            case Constants.ADMIN_PROBLEMS:
                getAdminProblems(searchDefinition);
                break;
            case Constants.ADMIN_SUPPLIERS:
                getAdminSuppliers(searchDefinition);
                break;
            default:
                break;
        }
    }

    /**********************************************************************************************
     ***********************  DEMAND SECTION. *****************************************************
     **********************************************************************************************/
    public void getAdminDemandsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminDemandsCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
//                grid.getDataProvider().updateRowCount(result.intValue(), true);
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminDemands(SearchDefinition searchDefinition) {
        adminService.getAdminDemands(searchDefinition,
                new SecuredAsyncCallback<List<FullDemandDetail>>(eventBus) {
                @Override
                public void onSuccess(List<FullDemandDetail> result) {
                    eventBus.displayAdminTabDemands(result);
                }
            });
    }

    public void onUpdateDemands(HashMap<Long, ArrayList<ChangeDetail>> changes) {
        adminService.updateDemands(changes, new SecuredAsyncCallback<Boolean>(eventBus) {
            @Override
            public void onSuccess(Boolean result) {
                eventBus.responseUpdateDemands(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  SUPPLIER SECTION. *****************************************************
     **********************************************************************************************/
    public void getAdminSuppliersCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminSuppliersCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminSuppliers(SearchDefinition searchDefinition) {
        adminService.getAdminSuppliers(searchDefinition,
                new SecuredAsyncCallback<List<FullSupplierDetail>>(eventBus) {
                @Override
                public void onSuccess(List<FullSupplierDetail> result) {
                    eventBus.displayAdminTabSuppliers(result);
                }
            });
    }

    public void onUpdateSupplier(FullSupplierDetail supplier) {
        adminService.updateSupplier(supplier, new SecuredAsyncCallback<FullSupplierDetail>(eventBus) {
            @Override
            public void onSuccess(FullSupplierDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  OFFER SECTION. *****************************************************
     **********************************************************************************************/
    public void getAdminOffersCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminOffersCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminOffers(SearchDefinition searchDefinition) {
        adminService.getAdminOffers(searchDefinition,
                new SecuredAsyncCallback<List<OfferDetail>>(eventBus) {
                @Override
                public void onSuccess(List<OfferDetail> result) {
                    eventBus.displayAdminTabOffers(result);
                }
            });
    }

    public void onUpdateOffer(OfferDetail offer) {
        adminService.updateOffer(offer, new SecuredAsyncCallback<OfferDetail>(eventBus) {
            @Override
            public void onSuccess(OfferDetail result) {
                // TODO LATER: what's wrong with this ?
//                eventBus.refreshUpdatedOffer(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  CLIENT SECTION. *****************************************************
     **********************************************************************************************/
    public void getAdminClientsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminClientsCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminClients(SearchDefinition searchDefinition) {
        adminService.getAdminClients(searchDefinition,
                new SecuredAsyncCallback<List<ClientDetail>>(eventBus) {
                @Override
                public void onSuccess(List<ClientDetail> result) {
                    eventBus.displayAdminTabClients(result);
                }
            });
    }

    public void onUpdateClient(ClientDetail client) {
        adminService.updateClient(client, new SecuredAsyncCallback<ClientDetail>(eventBus) {
            @Override
            public void onSuccess(ClientDetail result) {
                // TODO LATER: what's wrong with this ?
                //                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  ACCESS ROLE SECTION. *************************************************
     **********************************************************************************************/
    public void getAdminAccessRolesCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminAccessRolesCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminAccessRoles(SearchDefinition searchDefinition) {
        adminService.getAdminAccessRoles(searchDefinition,
                new SecuredAsyncCallback<List<AccessRoleDetail>>(eventBus) {
                @Override
                public void onSuccess(List<AccessRoleDetail> result) {
                    eventBus.displayAdminTabAccessRoles(result);
                }
            });
    }

    public void onUpdateAccessRole(AccessRoleDetail role) {
        adminService.updateAccessRole(role, new SecuredAsyncCallback<AccessRoleDetail>(eventBus) {
            @Override
            public void onSuccess(AccessRoleDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  EMAIL ACTIVATION SECTION.*********************************************
     **********************************************************************************************/
    public void getAdminEmailsActivationCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminEmailsActivationCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminEmailsActivation(SearchDefinition searchDefinition) {
        adminService.getAdminEmailsActivation(searchDefinition,
                new SecuredAsyncCallback<List<ActivationEmailDetail>>(eventBus) {
                @Override
                public void onSuccess(List<ActivationEmailDetail> result) {
                    eventBus.displayAdminTabEmailsActivation(result);
                }
            });
    }

    public void onUpdateEmailActivation(ActivationEmailDetail client) {
        adminService.updateEmailActivation(client, new SecuredAsyncCallback<ActivationEmailDetail>(eventBus) {
            @Override
            public void onSuccess(ActivationEmailDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  INVOICE SECTION. *****************************************************
     **********************************************************************************************/
    public void getAdminInvoicesCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminInvoicesCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminInvoices(SearchDefinition searchDefinition) {
        adminService.getAdminInvoices(searchDefinition,
                new SecuredAsyncCallback<List<InvoiceDetail>>(eventBus) {
                @Override
                public void onSuccess(List<InvoiceDetail> result) {
                    eventBus.displayAdminTabInvoices(result);
                }
            });
    }

    public void onUpdateInvoice(InvoiceDetail client) {
        adminService.updateInvoice(client, new SecuredAsyncCallback<InvoiceDetail>(eventBus) {
            @Override
            public void onSuccess(InvoiceDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  Message SECTION. *****************************************************
     **********************************************************************************************/
    public void getAdminMessagesCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminMessagesCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminMessages(SearchDefinition searchDefinition) {
        adminService.getAdminMessages(searchDefinition,
                new SecuredAsyncCallback<List<MessageDetail>>(eventBus) {
                @Override
                public void onSuccess(List<MessageDetail> result) {
                    eventBus.displayAdminTabMessages(result);
                }
            });
    }

    public void onUpdateMessage(MessageDetail client) {
        adminService.updateMessage(client, new SecuredAsyncCallback<MessageDetail>(eventBus) {
            @Override
            public void onSuccess(MessageDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  New Demands SECTION. ************************************************
     **********************************************************************************************/
    public void getAdminNewDemandsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminNewDemandsCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminNewDemands(SearchDefinition searchDefinition) {
        adminService.getAdminNewDemands(searchDefinition,
                new SecuredAsyncCallback<List<FullDemandDetail>>(eventBus) {
                @Override
                public void onSuccess(List<FullDemandDetail> result) {
                    eventBus.displayAdminNewDemands(result);
                }
            });
    }

    public void onRequestThreadRootId(long demandId) {
        adminService.getThreadRootMessageId(demandId, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                eventBus.responseThreadRootId(result);
            }
        });
    }

    public void onRequestApproveDemands(final UniversalAsyncGrid grid, Set<FullDemandDetail> demandsToApprove) {
        adminService.approveDemands(demandsToApprove, new SecuredAsyncCallback<Void>(eventBus) {
            @Override
            public void onSuccess(Void result) {
                grid.refresh();
            }
        });
    }

    /**********************************************************************************************
     ***********************  OUR PAYMENT DETAILS SECTION. *****************************************
     **********************************************************************************************/
    public void getAdminOurPaymentDetailsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminOurPaymentDetailsCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminOurPaymentDetails(SearchDefinition searchDefinition) {
        adminService.getAdminOurPaymentDetails(searchDefinition,
                new SecuredAsyncCallback<List<PaymentDetail>>(eventBus) {
                @Override
                public void onSuccess(List<PaymentDetail> result) {
                    eventBus.displayAdminTabOurPaymentDetails(result);
                }
            });
    }

    public void onUpdateOurPaymentDetail(PaymentDetail client) {
        adminService.updateOurPaymentDetail(client, new SecuredAsyncCallback<PaymentDetail>(eventBus) {
            @Override
            public void onSuccess(PaymentDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  PAYMENT METHOD SECTION. *****************************************
     **********************************************************************************************/
    public void getAdminPaymentMethodsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminPaymentMethodsCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminPaymentMethods(SearchDefinition searchDefinition) {
        adminService.getAdminPaymentMethods(searchDefinition,
                new SecuredAsyncCallback<List<PaymentMethodDetail>>(eventBus) {
                @Override
                public void onSuccess(List<PaymentMethodDetail> result) {
                    eventBus.displayAdminTabPaymentMethods(result);
                }
            });
    }

    public void onUpdatePaymentMethod(PaymentMethodDetail paymentMethods) {
        adminService.updatePaymentMethod(paymentMethods, new SecuredAsyncCallback<PaymentMethodDetail>(eventBus) {
            @Override
            public void onSuccess(PaymentMethodDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  PERMISSIONS SECTION. *****************************************
     **********************************************************************************************/
    public void getAdminPermissionsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminPermissionsCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminPermissions(SearchDefinition searchDefinition) {
        adminService.getAdminPermissions(searchDefinition,
                new SecuredAsyncCallback<List<PermissionDetail>>(eventBus) {
                @Override
                public void onSuccess(List<PermissionDetail> result) {
                    eventBus.displayAdminTabPermissions(result);
                }
            });
    }

    public void onUpdatePermission(PermissionDetail permissions) {
        adminService.updatePermission(permissions, new SecuredAsyncCallback<PermissionDetail>(eventBus) {
            @Override
            public void onSuccess(PermissionDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  PREFERENCES SECTION. *****************************************
     **********************************************************************************************/
    public void getAdminPreferencesCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminPreferencesCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminPreferences(SearchDefinition searchDefinition) {
        adminService.getAdminPreferences(searchDefinition,
                new SecuredAsyncCallback<List<PreferenceDetail>>(eventBus) {
                @Override
                public void onSuccess(List<PreferenceDetail> result) {
                    eventBus.displayAdminTabPreferences(result);
                }
            });
    }

    public void onUpdatePreference(PreferenceDetail client) {
        adminService.updatePreference(client, new SecuredAsyncCallback<PreferenceDetail>(eventBus) {
            @Override
            public void onSuccess(PreferenceDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    /**********************************************************************************************
     ***********************  PROBLEM SECTION. *****************************************
     **********************************************************************************************/
    public void getAdminProblemsCount(final UniversalAsyncGrid grid, SearchDefinition searchDefinition) {
        adminService.getAdminProblemsCount(searchDefinition, new SecuredAsyncCallback<Long>(eventBus) {
            @Override
            public void onSuccess(Long result) {
                grid.getDataProvider().updateRowCount(result.intValue(), true);
            }
        });
    }

    public void getAdminProblems(SearchDefinition searchDefinition) {
        adminService.getAdminProblems(searchDefinition,
                new SecuredAsyncCallback<List<ProblemDetail>>(eventBus) {
                @Override
                public void onSuccess(List<ProblemDetail> result) {
                    eventBus.displayAdminTabProblems(result);
                }
            });
    }

    public void onUpdateProblem(ProblemDetail client) {
        adminService.updateProblem(client, new SecuredAsyncCallback<ProblemDetail>(eventBus) {
            @Override
            public void onSuccess(ProblemDetail result) {
//                eventBus.refreshUpdatedDemand(result);
            }
        });
    }

    public void onUpdateUnreadMessagesCount() {
        adminService.updateUnreadMessagesCount(new SecuredAsyncCallback<UnreadMessagesDetail>(eventBus) {
            @Override
            public void onSuccess(UnreadMessagesDetail result) {
                eventBus.setUpdatedUnreadMessagesCount(result);
            }
        });
    }

    /**************************************************************************/
    /* DevelDetailWrapper widget methods                                      */
    /**************************************************************************/
    public void onRequestDemandDetail(Long demandId) {
        adminService.getFullDemandDetail(demandId, new SecuredAsyncCallback<FullDemandDetail>(eventBus) {
            @Override
            public void onSuccess(FullDemandDetail result) {
                eventBus.responseDemandDetail(result);
            }
        });
    }

    /**
     * Creates conversation between <code>Client</code> and Admin/Operator user. Conversation is created in such a
     * way that new <code>UserMessage</code> is created for every <code>User</code> who invokes this method. Thus
     * enabling the user to write a reply message to <code>Client</code> in order to update <code>Demand</code>
     * description or title before this demand is approved.
     *
     * @param demandId for which the conversation is created
     * @param userAdminId id of operator or admin user
     */
    public void onRequestCreateConversation(long demandId) {
        adminService.createConversation(demandId, Storage.getUser().getUserId(),
                new SecuredAsyncCallback<Long>(eventBus) {
                @Override
                public void onSuccess(Long result) {
                    eventBus.responseCreateConversation(result);
                }
            });
    }

    /**
     * Load conversation between client and supplier related to particular demand.
     * Only conversations where signed user take part are retrieved.
     *
     * @param threadId
     * @param demandId
     */
    public void onRequestConversation(Long demandId) {
        adminService.getConversation(demandId, Storage.getUser().getUserId(),
                new SecuredAsyncCallback<List<MessageDetail>>(eventBus) {
                @Override
                public void onSuccess(List<MessageDetail> result) {
                    eventBus.responseConversation(result);
                }
            });
    }

    /**
     * Load conversation between client and supplier related to particular demand.
     * Admin needn't be part of that conversation but he can see it.
     *
     * @param threadId
     * @param demandId
     */
    public void onRequestConversationForAdmin(Long demandId) {
        adminService.getConversationForAdmin(demandId, Storage.getUser().getUserId(),
                new SecuredAsyncCallback<List<MessageDetail>>(eventBus) {
                @Override
                public void onSuccess(List<MessageDetail> result) {
                    eventBus.responseConversationForAdmin(result);
                }
            });
    }

    /**
     * Send message from Admin.
     * @param messageToSend
     */
    public void onRequestSendAdminMessage(MessageDetail messageToSend) {
        adminService.sendQuestionMessage(messageToSend, new SecuredAsyncCallback<MessageDetail>(eventBus) {
            @Override
            public void onSuccess(MessageDetail sentMessage) {
                eventBus.responseSendAdminMessage(sentMessage);
            }
        });
    }
}
