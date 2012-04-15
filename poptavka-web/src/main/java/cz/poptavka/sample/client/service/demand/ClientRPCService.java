package cz.poptavka.sample.client.service.demand;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import cz.poptavka.sample.domain.common.OrderType;
import cz.poptavka.sample.shared.domain.adminModule.ClientDetail;
import cz.poptavka.sample.shared.domain.UserDetail;
import cz.poptavka.sample.shared.exceptions.RPCException;

import java.util.Map;

@RemoteServiceRelativePath(ClientRPCService.URL)
public interface ClientRPCService extends RemoteService {

    String URL = "service/cs";

    ArrayList<UserDetail> getAllClients() throws RPCException;

    ArrayList<ClientDetail> getClients(int start, int count) throws RPCException;

    UserDetail createNewClient(UserDetail clientDetail) throws RPCException;

    Integer getClientsCount() throws RPCException;

    ClientDetail updateClient(ClientDetail supplierDetail) throws RPCException;

    ArrayList<ClientDetail> getSortedClients(int start, int count, Map<String, OrderType> orderColumns)
        throws RPCException;
}
