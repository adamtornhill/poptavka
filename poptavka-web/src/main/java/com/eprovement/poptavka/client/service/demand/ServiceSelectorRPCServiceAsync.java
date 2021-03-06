package com.eprovement.poptavka.client.service.demand;

import com.eprovement.poptavka.domain.enums.ServiceType;
import com.eprovement.poptavka.shared.domain.ServiceDetail;
import com.eprovement.poptavka.shared.domain.UserServiceDetail;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;

/**
 *
 * @author Martin Slavkovsky
 */
public interface ServiceSelectorRPCServiceAsync {

    void getSupplierServices(ServiceType[] serviceTypes, AsyncCallback<ArrayList<ServiceDetail>> callback);

    void createUserService(long userId, ServiceDetail serviceDetail, AsyncCallback<UserServiceDetail> callback);
}
