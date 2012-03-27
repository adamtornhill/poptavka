/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.poptavka.sample.server.service.admin;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.Sort;
import cz.poptavka.sample.client.main.common.search.SearchModuleDataHolder;
import cz.poptavka.sample.client.service.demand.AdminRPCService;
import cz.poptavka.sample.domain.activation.EmailActivation;
import cz.poptavka.sample.domain.common.OrderType;
import cz.poptavka.sample.domain.demand.Demand;
import cz.poptavka.sample.domain.demand.DemandStatus;
import cz.poptavka.sample.domain.invoice.Invoice;
import cz.poptavka.sample.domain.invoice.OurPaymentDetails;
import cz.poptavka.sample.domain.invoice.PaymentMethod;
import cz.poptavka.sample.domain.message.Message;
import cz.poptavka.sample.domain.message.MessageState;
import cz.poptavka.sample.domain.offer.Offer;
import cz.poptavka.sample.domain.offer.OfferState;
import cz.poptavka.sample.domain.settings.Preference;
import cz.poptavka.sample.domain.user.BusinessType;
import cz.poptavka.sample.domain.user.BusinessUserData;
import cz.poptavka.sample.domain.user.Client;
import cz.poptavka.sample.domain.user.Problem;
import cz.poptavka.sample.domain.user.Supplier;
import cz.poptavka.sample.domain.user.Verification;
import cz.poptavka.sample.domain.user.rights.AccessRole;
import cz.poptavka.sample.domain.user.rights.Permission;
import cz.poptavka.sample.server.service.AutoinjectingRemoteService;
import cz.poptavka.sample.service.GeneralService;
import cz.poptavka.sample.service.demand.DemandService;
import cz.poptavka.sample.shared.domain.adminModule.AccessRoleDetail;
import cz.poptavka.sample.shared.domain.adminModule.ClientDetail;
import cz.poptavka.sample.shared.domain.adminModule.EmailActivationDetail;
import cz.poptavka.sample.shared.domain.adminModule.InvoiceDetail;
import cz.poptavka.sample.shared.domain.adminModule.OfferDetail;
import cz.poptavka.sample.shared.domain.adminModule.PaymentDetail;
import cz.poptavka.sample.shared.domain.adminModule.PaymentMethodDetail;
import cz.poptavka.sample.shared.domain.adminModule.PermissionDetail;
import cz.poptavka.sample.shared.domain.adminModule.PreferenceDetail;
import cz.poptavka.sample.shared.domain.adminModule.ProblemDetail;
import cz.poptavka.sample.shared.domain.demand.FullDemandDetail;
import cz.poptavka.sample.shared.domain.message.MessageDetail;
import cz.poptavka.sample.shared.domain.supplier.FullSupplierDetail;
import cz.poptavka.sample.shared.domain.type.MessageType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Martin Slavkovsky
 */
public class AdminRPCServiceImpl extends AutoinjectingRemoteService implements AdminRPCService {

    private static final long serialVersionUID = 1132667081084321575L;
    private GeneralService generalService;
    private DemandService demandService;

    public GeneralService getAdminGeneralService() {
        return generalService;
    }

    @Autowired
    public void setGeneralService(GeneralService generalService) {
        this.generalService = generalService;
    }

    /**********************************************************************************************
     ***********************  DEMAND SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminDemandsCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Demand.class);
        } else {
            search = this.setAdminDemandFilters(searchDataHolder, new Search(Demand.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<FullDemandDetail> getAdminDemands(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Demand.class);
        if (searchDataHolder != null) {
            search = this.setAdminDemandFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createDemandDetailList(generalService.search(search));
    }

    @Override
    public FullDemandDetail updateDemand(FullDemandDetail fullDemandDetail) {
        return generalService.merge(fullDemandDetail);
    }

//    private Search setAdminClientsFilters(SearchModuleDataHolder searchDataHolder, Search search) {
//    }
    private Search setAdminDemandFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminDemands().getDemandIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminDemands().getDemandIdFrom());
        }
        if (searchDataHolder.getAdminDemands().getDemandIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminDemands().getDemandIdTo());
        }
        if (searchDataHolder.getAdminDemands().getClientIdFrom() != null) {
            search.addFilterGreaterOrEqual("client.id", searchDataHolder.getAdminDemands().getClientIdFrom());
        }
        if (searchDataHolder.getAdminDemands().getClientIdTo() != null) {
            search.addFilterLessOrEqual("client.id", searchDataHolder.getAdminDemands().getClientIdTo());
        }
        if (searchDataHolder.getAdminDemands().getDemandTitle() != null) {
            search.addFilterEqual("title", "%" + searchDataHolder.getAdminDemands().getDemandTitle() + "%");
        }
        if (searchDataHolder.getAdminDemands().getDemandType() != null) {
            search.addFilterEqual("type", demandService.getDemandType(
                    searchDataHolder.getAdminDemands().getDemandType()));
        }
        if (searchDataHolder.getAdminDemands().getDemandStatus() != null) {
            search.addFilterEqual("status", DemandStatus.valueOf(searchDataHolder.getAdminDemands().getDemandStatus()));
        }
        if (searchDataHolder.getAdminDemands().getExpirationDateFrom() != null) {
            search.addFilterGreaterOrEqual("validTo", searchDataHolder.getAdminDemands().getExpirationDateFrom());
        }
        if (searchDataHolder.getAdminDemands().getExpirationDateTo() != null) {
            search.addFilterLessOrEqual("validTo", searchDataHolder.getAdminDemands().getExpirationDateTo());
        }
        if (searchDataHolder.getAdminDemands().getEndDateFrom() != null) {
            search.addFilterGreaterOrEqual("endDate", searchDataHolder.getAdminDemands().getEndDateFrom());
        }
        if (searchDataHolder.getAdminDemands().getEndDateTo() != null) {
            search.addFilterLessOrEqual("endDate", searchDataHolder.getAdminDemands().getEndDateTo());
        }
        return search;
    }

    private List<FullDemandDetail> createDemandDetailList(Collection<Demand> demands) {
        List<FullDemandDetail> demandDetail = new ArrayList<FullDemandDetail>();
        for (Demand demand : demands) {
            demandDetail.add(FullDemandDetail.createDemandDetail(demand));
        }
        return demandDetail;
    }

    /**********************************************************************************************
     ***********************  CLIENT SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminClientsCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Client.class);
        } else {
            search = this.setAdminClientsFilters(searchDataHolder, new Search(Client.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<ClientDetail> getAdminClients(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Client.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createClientDetailList(generalService.search(search));
    }

    @Override
    public ClientDetail updateClient(ClientDetail accessRoleDetail) {
        return generalService.merge(accessRoleDetail);
    }

//    @Override
//    public List<ClientDetail> getAdminSortedClients(int start, int count, Map<String, OrderType> orderColumns) {
//        return this.createClientDetailList(
//                generalService.search(this.setSortSearch(start, count, orderColumns, Client.class)));
//    }
    private Search setAdminClientsFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminClients().getIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminClients().getIdFrom());
        }
        if (searchDataHolder.getAdminClients().getIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminClients().getIdTo());
        }
        if (searchDataHolder.getAdminClients().getCompanyName() != null) {
            Collection<BusinessUserData> data = generalService.search(
                    new Search(BusinessUserData.class).addFilterLike("companyName",
                        "%" + searchDataHolder.getAdminClients().getCompanyName() + "%"));
            search.addFilterIn("businessUser.businessUserData", data);
        }
        if (searchDataHolder.getAdminClients().getFirstName() != null) {
            Collection<BusinessUserData> data = generalService.search(
                    new Search(BusinessUserData.class).addFilterLike("personFirstName",
                        "%" + searchDataHolder.getAdminClients().getFirstName() + "%"));
            search.addFilterIn("businessUser.businessUserData", data);
        }
        if (searchDataHolder.getAdminClients().getLastName() != null) {
            List<BusinessUserData> data = generalService.search(
                    new Search(BusinessUserData.class).addFilterLike("personLastName",
                        "%" + searchDataHolder.getAdminClients().getLastName() + "%"));
            search.addFilterIn("businessUser.businessUserData", data);
        }
        if (searchDataHolder.getAdminClients().getRatingFrom() != null) {
            search.addFilterGreaterOrEqual("overalRating",
                    searchDataHolder.getAdminClients().getRatingFrom());
        }
        if (searchDataHolder.getAdminClients().getRatingTo() != null) {
            search.addFilterLessOrEqual("overalRating",
                    searchDataHolder.getAdminClients().getRatingTo());
        }
        return search;
    }

    private List<ClientDetail> createClientDetailList(Collection<Client> demands) {
        List<ClientDetail> accessRoles = new ArrayList<ClientDetail>();
        for (Client role : demands) {
            accessRoles.add(ClientDetail.createClientDetail(role));
        }
        return accessRoles;
    }

    /**********************************************************************************************
     ***********************  SUPPLIER SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminSuppliersCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Supplier.class);
        } else {
            search = this.setAdminSuppliersFilters(searchDataHolder, new Search(Supplier.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<FullSupplierDetail> getAdminSuppliers(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Supplier.class);
        if (searchDataHolder != null) {
            search = this.setAdminSuppliersFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createFullSupplierDetailList(generalService.search(search));
    }

    @Override
    public FullSupplierDetail updateSupplier(FullSupplierDetail accessRoleDetail) {
        return generalService.merge(accessRoleDetail);
    }

//    @Override
//    public List<FullSupplierDetail> getAdminSortedSuppliers(int start,
//    int count, Map<String, OrderType> orderColumns) {
//        return this.createFullSupplierDetailList(
//                generalService.search(this.setSortSearch(start, count, orderColumns, Supplier.class)));
//    }
    private Search setAdminSuppliersFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminSuppliers().getSupplierName() != null) {
            Collection<BusinessUserData> data = generalService.search(
                    new Search(BusinessUserData.class).addFilterLike("companyName",
                        "%" + searchDataHolder.getAdminSuppliers().getSupplierName() + "%"));
            search.addFilterIn("businessUser.businessUserData", data);
        }
        if (searchDataHolder.getAdminSuppliers().getSupplierDescription() != null) {
            Collection<BusinessUserData> data = generalService.search(
                    new Search(BusinessUserData.class).addFilterLike("description",
                        "%" + searchDataHolder.getAdminSuppliers().getSupplierDescription() + "%"));
            search.addFilterIn("businessUser.businessUserData", data);
        }
        if (searchDataHolder.getAdminSuppliers().getSupplierCategory() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminSuppliers().getIdFrom());
        }
        if (searchDataHolder.getAdminSuppliers().getSupplierLocality() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminSuppliers().getIdTo());
        }
        if (searchDataHolder.getAdminSuppliers().getRatingFrom() != null) {
            search.addFilterGreaterOrEqual("overalRating", searchDataHolder.getAdminSuppliers().getRatingFrom());
        }
        if (searchDataHolder.getAdminSuppliers().getRatingFrom() != null) {
            search.addFilterLessOrEqual("overalRating", searchDataHolder.getAdminSuppliers().getRatingTo());
        }
        if (searchDataHolder.getAdminSuppliers().getType() != null) {
            search.addFilterEqual("businessUser.businessType",
                    BusinessType.valueOf(searchDataHolder.getAdminSuppliers().getType()));
        }
        if (searchDataHolder.getAdminSuppliers().getCertified() != null) {
            search.addFilterEqual("certified", searchDataHolder.getAdminSuppliers().getCertified());
        }
        if (searchDataHolder.getAdminSuppliers().getVerified() != null) {
            search.addFilterEqual("verification",
                    Verification.valueOf(searchDataHolder.getAdminSuppliers().getVerified()));
        }
        return search;
    }

    private List<FullSupplierDetail> createFullSupplierDetailList(Collection<Supplier> demands) {
        List<FullSupplierDetail> accessRoles = new ArrayList<FullSupplierDetail>();
        for (Supplier role : demands) {
            accessRoles.add(FullSupplierDetail.createFullSupplierDetail(role));
        }
        return accessRoles;
    }

    /**********************************************************************************************
     ***********************  OFFER SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminOffersCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Offer.class);
        } else {
            search = this.setAdminOffersFilters(searchDataHolder, new Search(Offer.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<OfferDetail> getAdminOffers(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Offer.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createOfferDetailList(generalService.search(search));
    }

    @Override
    public OfferDetail updateOffer(OfferDetail accessRoleDetail) {
        return generalService.merge(accessRoleDetail);
    }

//    @Override
//    public List<OfferDetail> getAdminSortedOffers(int start, int count, Map<String, OrderType> orderColumns) {
//        return this.createOfferDetailList(
//                generalService.search(this.setSortSearch(start, count, orderColumns, Offer.class)));
//    }
    private Search setAdminOffersFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminOffers().getOfferIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminOffers().getOfferIdFrom());
        }
        if (searchDataHolder.getAdminOffers().getOfferIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminOffers().getOfferIdTo());
        }
        if (searchDataHolder.getAdminOffers().getDemandIdFrom() != null) {
            search.addFilterGreaterOrEqual("demand.id", searchDataHolder.getAdminOffers().getDemandIdTo());
        }
        if (searchDataHolder.getAdminOffers().getDemandIdTo() != null) {
            search.addFilterLessOrEqual("demand.id", searchDataHolder.getAdminOffers().getDemandIdTo());
        }
        if (searchDataHolder.getAdminOffers().getSupplierIdFrom() != null) {
            search.addFilterGreaterOrEqual("supplier.id", searchDataHolder.getAdminOffers().getSupplierIdFrom());
        }
        if (searchDataHolder.getAdminOffers().getSupplierIdTo() != null) {
            search.addFilterLessOrEqual("supplier.id", searchDataHolder.getAdminOffers().getSupplierIdTo());
        }
        if (searchDataHolder.getAdminOffers().getPriceFrom() != null) {
            search.addFilterGreaterOrEqual("price", searchDataHolder.getAdminOffers().getPriceFrom());
        }
        if (searchDataHolder.getAdminOffers().getPriceTo() != null) {
            search.addFilterLessOrEqual("price", searchDataHolder.getAdminOffers().getPriceTo());
        }
        if (searchDataHolder.getAdminOffers().getCreatedFrom() != null) {
            search.addFilterGreaterOrEqual("created", searchDataHolder.getAdminOffers().getCreatedFrom());
        }
        if (searchDataHolder.getAdminOffers().getCreatedTo() != null) {
            search.addFilterLessOrEqual("created", searchDataHolder.getAdminOffers().getCreatedTo());
        }
        if (searchDataHolder.getAdminOffers().getFinnishFrom() != null) {
            search.addFilterGreaterOrEqual("finnishDate", searchDataHolder.getAdminOffers().getFinnishFrom());
        }
        if (searchDataHolder.getAdminOffers().getFinnishTo() != null) {
            search.addFilterLessOrEqual("finnishDate", searchDataHolder.getAdminOffers().getFinnishTo());
        }
        if (searchDataHolder.getAdminOffers().getState() != null) {
            search.addFilterEqual("state", OfferState.Type.valueOf(searchDataHolder.getAdminOffers().getState()));
        }
        return search;
    }

    private List<OfferDetail> createOfferDetailList(Collection<Offer> demands) {
        List<OfferDetail> accessRoles = new ArrayList<OfferDetail>();
        for (Offer item : demands) {
            accessRoles.add(OfferDetail.createOfferDetail(item));
        }
        return accessRoles;
    }

    /**********************************************************************************************
     ***********************  ACCESS ROLE SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminAccessRolesCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(AccessRole.class);
        } else {
            search = this.setAdminAccessRoleFilters(searchDataHolder, new Search(AccessRole.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<AccessRoleDetail> getAdminAccessRoles(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(AccessRole.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createAccessRoleDetailList(generalService.search(search));
    }

//    @Override
    public AccessRoleDetail updateAccessRole(AccessRoleDetail accessRoleDetail) {
        return generalService.merge(accessRoleDetail);
    }

//    @Override
//    public List<AccessRoleDetail> getAdminSortedAccessRoles(int start, int count,
//    Map<String, OrderType> orderColumns) {
//        return this.createAccessRoleDetailList(
//                generalService.search(this.setSortSearch(start, count, orderColumns, AccessRole.class)));
//    }
    private Search setAdminAccessRoleFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminAccessRoles().getIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminAccessRoles().getIdFrom());
        }
        if (searchDataHolder.getAdminAccessRoles().getIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminAccessRoles().getIdTo());
        }
        if (searchDataHolder.getAdminAccessRoles().getCode() != null) {
            search.addFilterLike("code", searchDataHolder.getAdminAccessRoles().getCode());
        }
        if (searchDataHolder.getAdminAccessRoles().getRoleName() != null) {
            search.addFilterLike("name", searchDataHolder.getAdminAccessRoles().getRoleName());
        }
        if (searchDataHolder.getAdminAccessRoles().getRoleDescription() != null) {
            search.addFilterLike("description", searchDataHolder.getAdminAccessRoles().getRoleDescription());
        }
        if (searchDataHolder.getAdminAccessRoles().getPermisstions() != null) {
            search.addFilterIn("permissions", Arrays.asList(searchDataHolder.getAdminAccessRoles().getPermisstions()));
        }
        return search;
    }

    private List<AccessRoleDetail> createAccessRoleDetailList(Collection<AccessRole> demands) {
        List<AccessRoleDetail> accessRoles = new ArrayList<AccessRoleDetail>();
        for (AccessRole role : demands) {
            accessRoles.add(AccessRoleDetail.createAccessRoleDetail(role));
        }
        return accessRoles;
    }

    /**********************************************************************************************
     ***********************  EMAIL ACTIVATION SECTION. *******************************************
     **********************************************************************************************/
    @Override
    public Long getAdminEmailsActivationCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(EmailActivation.class);
        } else {
            search = this.setAdminEmailsActivationFilters(searchDataHolder, new Search(EmailActivation.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<EmailActivationDetail> getAdminEmailsActivation(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(EmailActivation.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createEmailActivationDetailList(generalService.search(search));
    }

    @Override
    public EmailActivationDetail updateEmailActivation(EmailActivationDetail supplierDetail) {
        return generalService.merge(supplierDetail);
    }

    private Search setAdminEmailsActivationFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminEmailActivation().getIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminEmailActivation().getIdFrom());
        }
        if (searchDataHolder.getAdminEmailActivation().getIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminEmailActivation().getIdTo());
        }
        if (searchDataHolder.getAdminEmailActivation().getActivationLink() != null) {
            search.addFilterLike("activationLink", searchDataHolder.getAdminEmailActivation().getActivationLink());
        }
        if (searchDataHolder.getAdminEmailActivation().getTimeoutFrom() != null) {
            search.addFilterGreaterOrEqual("timeout", searchDataHolder.getAdminEmailActivation().getTimeoutFrom());
        }
        if (searchDataHolder.getAdminEmailActivation().getTimeoutTo() != null) {
            search.addFilterLessOrEqual("timeout", searchDataHolder.getAdminEmailActivation().getTimeoutTo());
        }
        return search;
    }

    private List<EmailActivationDetail> createEmailActivationDetailList(Collection<EmailActivation> emailsList) {
        List<EmailActivationDetail> emailsActivation = new ArrayList<EmailActivationDetail>();
        for (EmailActivation email : emailsList) {
            emailsActivation.add(EmailActivationDetail.createEmailActivationDetail(email));
        }
        return emailsActivation;
    }

    /**********************************************************************************************
     ***********************  INVOICE SECTION. ****************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminInvoicesCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Invoice.class);
        } else {
            search = this.setAdminInvoicesFilters(searchDataHolder, new Search(Invoice.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<InvoiceDetail> getAdminInvoices(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Invoice.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createInvoiceDetailList(generalService.search(search));
    }

    @Override
    public InvoiceDetail updateInvoice(InvoiceDetail supplierDetail) {
        return generalService.merge(supplierDetail);
    }

    private Search setAdminInvoicesFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminInvoice().getIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminInvoice().getIdFrom());
        }
        if (searchDataHolder.getAdminInvoice().getIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminInvoice().getIdTo());
        }
        if (searchDataHolder.getAdminInvoice().getInvoiceNumberFrom() != null) {
            search.addFilterGreaterOrEqual("invoiceNumber", searchDataHolder.getAdminInvoice().getInvoiceNumberFrom());
        }
        if (searchDataHolder.getAdminInvoice().getInvoiceNumberTo() != null) {
            search.addFilterLessOrEqual("invoiceNumber", searchDataHolder.getAdminInvoice().getInvoiceNumberTo());
        }
        if (searchDataHolder.getAdminInvoice().getTotalPriceFrom() != null) {
            search.addFilterGreaterOrEqual("totalPrice", searchDataHolder.getAdminInvoice().getTotalPriceFrom());
        }
        if (searchDataHolder.getAdminInvoice().getTotalPriceTo() != null) {
            search.addFilterLessOrEqual("totalPrice", searchDataHolder.getAdminInvoice().getTotalPriceTo());
        }
        if (searchDataHolder.getAdminInvoice().getVariableSymbol() != null) {
            search.addFilterLike("variableSymbol", searchDataHolder.getAdminInvoice().getVariableSymbol());
        }
        if (searchDataHolder.getAdminInvoice().getVariableSymbol() != null) {
            search.addFilterEqual("paymentMethod", generalService.find(
                    PaymentMethod.class, searchDataHolder.getAdminInvoice().getPaymentMethodId()));
        }
        return search;
    }

    private List<InvoiceDetail> createInvoiceDetailList(Collection<Invoice> invoicesList) {
        List<InvoiceDetail> invoices = new ArrayList<InvoiceDetail>();
        for (Invoice invoice : invoicesList) {
            invoices.add(InvoiceDetail.createInvoiceDetail(invoice));
        }
        return invoices;
    }

    /**********************************************************************************************
     ***********************  MESSAGE SECTION. ****************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminMessagesCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Message.class);
        } else {
            search = this.setAdminMessagesFilters(searchDataHolder, new Search(Message.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<MessageDetail> getAdminMessages(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Message.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
//        search.setPage(count);
        return this.createMessageDetailList(generalService.search(search));
    }

    @Override
    public MessageDetail updateMessage(MessageDetail supplierDetail) {
        return generalService.merge(supplierDetail);
    }

    private Search setAdminMessagesFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminMessages().getMessageIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminMessages().getMessageIdFrom());
        }
        if (searchDataHolder.getAdminMessages().getMessageIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminMessages().getMessageIdTo());
        }
        if (searchDataHolder.getAdminMessages().getDemandIdFrom() != null) {
            search.addFilterGreaterOrEqual("demand.id", searchDataHolder.getAdminMessages().getDemandIdTo());
        }
        if (searchDataHolder.getAdminMessages().getDemandIdTo() != null) {
            search.addFilterLessOrEqual("demand.id", searchDataHolder.getAdminMessages().getDemandIdTo());
        }
        if (searchDataHolder.getAdminMessages().getParentIdFrom() != null) {
            search.addFilterGreaterOrEqual("parent.id", searchDataHolder.getAdminMessages().getParentIdFrom());
        }
        if (searchDataHolder.getAdminMessages().getParentIdTo() != null) {
            search.addFilterLessOrEqual("parent.id", searchDataHolder.getAdminMessages().getParentIdTo());
        }
        if (searchDataHolder.getAdminMessages().getSenderIdFrom() != null) {
            search.addFilterGreaterOrEqual("sender.id", searchDataHolder.getAdminMessages().getSenderIdFrom());
        }
        if (searchDataHolder.getAdminMessages().getSenderIdTo() != null) {
            search.addFilterLessOrEqual("sender.id", searchDataHolder.getAdminMessages().getSenderIdTo());
        }
        if (searchDataHolder.getAdminMessages().getReceiverIdFrom() != null) {
            search.addFilterGreaterOrEqual("receiver.id", searchDataHolder.getAdminMessages().getReceiverIdFrom());
        }
        if (searchDataHolder.getAdminMessages().getReceiverIdTo() != null) {
            search.addFilterLessOrEqual("receiver.id", searchDataHolder.getAdminMessages().getReceiverIdTo());
        }
        if (searchDataHolder.getAdminMessages().getSubject() != null) {
            search.addFilterLike("subject", searchDataHolder.getAdminMessages().getSubject());
        }
        if (searchDataHolder.getAdminMessages().getBody() != null) {
            search.addFilterLike("body", searchDataHolder.getAdminMessages().getBody());
        }
        if (searchDataHolder.getAdminMessages().getCreatedFrom() != null) {
            search.addFilterGreaterOrEqual("created", searchDataHolder.getAdminMessages().getCreatedFrom());
        }
        if (searchDataHolder.getAdminMessages().getCreatedTo() != null) {
            search.addFilterLessOrEqual("created", searchDataHolder.getAdminMessages().getCreatedTo());
        }
        if (searchDataHolder.getAdminMessages().getSentFrom() != null) {
            search.addFilterGreaterOrEqual("sent", searchDataHolder.getAdminMessages().getSentFrom());
        }
        if (searchDataHolder.getAdminMessages().getSentFrom() != null) {
            search.addFilterLessOrEqual("sent", searchDataHolder.getAdminMessages().getSentFrom());
        }
        if (searchDataHolder.getAdminMessages().getType() != null) {
            search.addFilterEqual("messageState", MessageState.valueOf(searchDataHolder.getAdminMessages().getType()));
        }
        //TODO skontrolovat, v message totiz list roli + ci je to rovnaky typ triedy
        if (searchDataHolder.getAdminMessages().getState() != null) {
            search.addFilterIn("roles.MessageUserRole.MessageUserRoleType",
                    MessageType.valueOf(searchDataHolder.getAdminMessages().getState()));
        }
        return search;
    }

    private List<MessageDetail> createMessageDetailList(Collection<Message> messagesList) {
        List<MessageDetail> messages = new ArrayList<MessageDetail>();
        for (Message message : messagesList) {
            messages.add(MessageDetail.createMessageDetail(message));
        }
        return messages;
    }

    /**********************************************************************************************
     ***********************  OUR PAYMENT DETAIL SECTION. *****************************************
     **********************************************************************************************/
    @Override
    public Long getAdminOurPaymentDetailsCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(OurPaymentDetails.class);
        } else {
            search = this.setAdminOurPaymentDetailsFilters(searchDataHolder, new Search(OurPaymentDetails.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<PaymentDetail> getAdminOurPaymentDetails(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(OurPaymentDetails.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createPaymentDetailList(generalService.search(search));
    }

    @Override
    public PaymentDetail updateOurPaymentDetail(PaymentDetail supplierDetail) {
        return generalService.merge(supplierDetail);
    }

    private Search setAdminOurPaymentDetailsFilters(SearchModuleDataHolder searchDataHolder, Search search) {

        return search;
    }

    private List<PaymentDetail> createPaymentDetailList(Collection<OurPaymentDetails> paymentsList) {
        List<PaymentDetail> payments = new ArrayList<PaymentDetail>();
        for (OurPaymentDetails payment : paymentsList) {
            payments.add(PaymentDetail.createOurPaymentDetailDetail(payment));
        }
        return payments;
    }

    /**********************************************************************************************
     ***********************  PAYMENT METHOD SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminPaymentMethodsCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(PaymentMethod.class);
        } else {
            search = this.setAdminPaymentMethodsFilters(searchDataHolder, new Search(PaymentMethod.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<PaymentMethodDetail> getAdminPaymentMethods(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(PaymentMethod.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createPaymentMethodDetailList(generalService.search(search));
    }

    @Override
    public List<PaymentMethodDetail> getAdminPaymentMethods() {
        final Search search = new Search(PaymentMethod.class);
        search.addSort("id", false);
        return this.createPaymentMethodDetailList(generalService.search(search));
    }

    @Override
    public PaymentMethodDetail updatePaymentMethod(PaymentMethodDetail accessRoleDetail) {
        return generalService.merge(accessRoleDetail);
    }

    private Search setAdminPaymentMethodsFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminPaymentMethods().getIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminPaymentMethods().getIdFrom());
        }
        if (searchDataHolder.getAdminPaymentMethods().getIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminPaymentMethods().getIdTo());
        }
        if (searchDataHolder.getAdminPaymentMethods().getName() != null) {
            search.addFilterLike("name", searchDataHolder.getAdminPaymentMethods().getName());
        }
        if (searchDataHolder.getAdminPaymentMethods().getDescription() != null) {
            search.addFilterLike("description", searchDataHolder.getAdminPaymentMethods().getDescription());
        }
        return search;
    }

    private List<PaymentMethodDetail> createPaymentMethodDetailList(Collection<PaymentMethod> demands) {
        List<PaymentMethodDetail> accessRoles = new ArrayList<PaymentMethodDetail>();
        for (PaymentMethod item : demands) {
            accessRoles.add(PaymentMethodDetail.createPaymentMethodDetail(item));
        }
        return accessRoles;
    }

    /**********************************************************************************************
     ***********************  PERMISSION SECTION. *************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminPermissionsCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Permission.class);
        } else {
            search = this.setAdminPermissionsFilters(searchDataHolder, new Search(Permission.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<PermissionDetail> getAdminPermissions(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Permission.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createPermissionDetailList(generalService.search(search));
    }

    @Override
    public PermissionDetail updatePermission(PermissionDetail supplierDetail) {
        return generalService.merge(supplierDetail);
    }

    private Search setAdminPermissionsFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminPermissions().getIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminPermissions().getIdFrom());
        }
        if (searchDataHolder.getAdminPermissions().getIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminPermissions().getIdTo());
        }
        if (searchDataHolder.getAdminPermissions().getName() != null) {
            search.addFilterLike("name", searchDataHolder.getAdminPermissions().getName());
        }
        if (searchDataHolder.getAdminPermissions().getCode() != null) {
            search.addFilterLike("code", searchDataHolder.getAdminPermissions().getCode());
        }
        if (searchDataHolder.getAdminPermissions().getDescription() != null) {
            search.addFilterLike("description", searchDataHolder.getAdminPermissions().getDescription());
        }
        return search;
    }

    private List<PermissionDetail> createPermissionDetailList(Collection<Permission> permissionList) {
        List<PermissionDetail> accessRoles = new ArrayList<PermissionDetail>();
        for (Permission permission : permissionList) {
            accessRoles.add(PermissionDetail.createPermissionsDetail(permission));
        }
        return accessRoles;
    }

    /**********************************************************************************************
     ***********************  PREFERENCE SECTION. *************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminPreferencesCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Preference.class);
        } else {
            search = this.setAdminPreferencesFilters(searchDataHolder, new Search(Preference.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<PreferenceDetail> getAdminPreferences(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Preference.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createPreferenceDetailList(generalService.search(search));
    }

    @Override
    public PreferenceDetail updatePreference(PreferenceDetail supplierDetail) {
        return generalService.merge(supplierDetail);
    }

    private Search setAdminPreferencesFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminPreferences().getIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminPreferences().getIdFrom());
        }
        if (searchDataHolder.getAdminPreferences().getIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminPreferences().getIdTo());
        }
        if (searchDataHolder.getAdminPreferences().getKey() != null) {
            search.addFilterLike("key", searchDataHolder.getAdminPreferences().getKey());
        }
        if (searchDataHolder.getAdminPreferences().getValue() != null) {
            search.addFilterLike("value", searchDataHolder.getAdminPreferences().getValue());
        }
        if (searchDataHolder.getAdminPreferences().getDescription() != null) {
            search.addFilterLike("description", searchDataHolder.getAdminPreferences().getDescription());
        }
        return search;
    }

    private List<PreferenceDetail> createPreferenceDetailList(Collection<Preference> preference) {
        List<PreferenceDetail> preferences = new ArrayList<PreferenceDetail>();
        for (Preference role : preference) {
            preferences.add(PreferenceDetail.createPreferenceDetail(role));
        }
        return preferences;
    }

    /**********************************************************************************************
     ***********************  PROBLEM SECTION. ************************************************
     **********************************************************************************************/
    @Override
    public Long getAdminProblemsCount(SearchModuleDataHolder searchDataHolder) {
        Search search = null;
        if (searchDataHolder == null) {
            search = new Search(Problem.class);
        } else {
            search = this.setAdminProblemsFilters(searchDataHolder, new Search(Problem.class));
        }
        return (long) generalService.count(search);
    }

    @Override
    public List<ProblemDetail> getAdminProblems(int start, int count,
            SearchModuleDataHolder searchDataHolder, Map<String, OrderType> orderColumns) {
        Search search = new Search(Problem.class);
        if (searchDataHolder != null) {
            search = this.setAdminAccessRoleFilters(searchDataHolder, search);
        }
        search = this.setSortSearch(orderColumns, search);
        search.setFirstResult(start);
        search.setMaxResults(count);
        return this.createProblemDetailList(generalService.search(search));
    }

    @Override
    public ProblemDetail updateProblem(ProblemDetail accessRoleDetail) {
        return generalService.merge(accessRoleDetail);
    }

    private Search setAdminProblemsFilters(SearchModuleDataHolder searchDataHolder, Search search) {
        if (searchDataHolder.getAdminProblems().getIdFrom() != null) {
            search.addFilterGreaterOrEqual("id", searchDataHolder.getAdminProblems().getIdFrom());
        }
        if (searchDataHolder.getAdminProblems().getIdTo() != null) {
            search.addFilterLessOrEqual("id", searchDataHolder.getAdminProblems().getIdTo());
        }
        if (searchDataHolder.getAdminProblems().getText() != null) {
            search.addFilterLike("text", searchDataHolder.getAdminProblems().getText());
        }
        return search;
    }

    private List<ProblemDetail> createProblemDetailList(Collection<Problem> demands) {
        List<ProblemDetail> problems = new ArrayList<ProblemDetail>();
        for (Problem item : demands) {
            problems.add(ProblemDetail.createProblemDetail(item));
        }
        return problems;
    }

    /**********************************************************************************************
     ***********************  COMMON METHODS. *************************************************
     **********************************************************************************************/
    private Search setSortSearch(Map<String, OrderType> orderColumns, Search search) {
        List<Sort> sorts = new ArrayList<Sort>();
        for (String str : orderColumns.keySet()) {
            if (orderColumns.get(str) == OrderType.ASC) {
                sorts.add(new Sort(str, false));
            } else {
                sorts.add(new Sort(str, true));
            }
        }
        return search.addSorts(sorts.toArray(new Sort[sorts.size()]));
    }
}