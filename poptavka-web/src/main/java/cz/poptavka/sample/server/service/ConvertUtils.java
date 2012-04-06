/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.poptavka.sample.server.service;

import cz.poptavka.sample.domain.address.Locality;
import cz.poptavka.sample.domain.demand.Category;
import cz.poptavka.sample.domain.product.UserService;
import cz.poptavka.sample.domain.user.BusinessUserRole;
import cz.poptavka.sample.domain.user.Client;
import cz.poptavka.sample.domain.user.Supplier;
import cz.poptavka.sample.domain.user.Verification;
import cz.poptavka.sample.shared.domain.SupplierDetail;
import cz.poptavka.sample.shared.domain.UserDetail;
import cz.poptavka.sample.shared.domain.UserDetail.Role;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.Validate;

/**
 * Utility class containing conversions methods between Domain objects (used on backend) from/to
 * detail objects (used on frontend).
 *
 * @author Praso
 * @author Juraj Martinka
 */
public final class ConvertUtils {

    private ConvertUtils() {
        // UTILITY CLASS - DO NOT INSTANTIATE!
    }

    public static UserDetail toUserDetail(Long userId, List<BusinessUserRole> userRoles) {
        if (userRoles == null) {
            throw new NullPointerException("These roles are not defined");
        }

        UserDetail detail = new UserDetail();
        detail.setUserId(userId);

        // Set UserDetail according to his roles
        for (BusinessUserRole role : userRoles) {
            if (role instanceof Client) {
                final Client clientRole = (Client) role;
                detail.setClientId(clientRole.getId());
                detail.addRole(Role.CLIENT);

                detail.setVerified(clientRole.getVerification().equals(Verification.VERIFIED));
            }
            if (role instanceof Supplier) {
                final Supplier supplierRole = (Supplier) role;
                detail.setSupplierId(supplierRole.getId());
                detail.addRole(Role.SUPPLIER);
                detail.setSupplier(toSupplierDetail(supplierRole));
                detail.setVerified(supplierRole.getVerification().equals(Verification.VERIFIED));
            }
        }
        // TODO Beho fix this user ID
        System.out.println("ID is: " + userRoles.get(0).getBusinessUser().getId());
        return detail;
    }


    /**
     * Converts given list of categories to the list of strings containing codes (the same as IDs) of given categories.
     * @param categories
     * @return list containing categories' codes (i.e. IDs)
     */
    public static ArrayList<String> convertCategoriesToDetail(List<Category> categories) {
        Validate.notNull(categories, "categories cannot be null");
        final ArrayList<String> categoriesCodes = new ArrayList<String>();
        for (Category category : categories) {
            // no code is stored in DB for category - use ID instead
            categoriesCodes.add(category.getId() + "");
        }
        return categoriesCodes;
    }


    /**
     * Converts given list of localities to the list of strings containing codes of given localities.
     * @param localities
     * @return list containing localities' codes
     */
    public static ArrayList<String> convertLocalitiesToDetail(List<Locality> localities) {
        Validate.notNull(localities, "localities cannot be null");
        final ArrayList<String> localitiesCodes = new ArrayList<String>();
        for (Locality locality : localities) {
            localitiesCodes.add(locality.getCode() + "");
        }
        return localitiesCodes;
    }


    //--------------------------------------------------- HELPER METHODS -----------------------------------------------

    private static SupplierDetail toSupplierDetail(Supplier supplierRole) {
        final SupplierDetail supplierDetail = new SupplierDetail();

        // supplierServices
        for (UserService us : supplierRole.getBusinessUser().getUserServices()) {
            supplierDetail.addService(us.getId().intValue());
        }

        supplierDetail.setCategories(convertCategoriesToDetail(supplierRole.getCategories()));
        supplierDetail.setLocalities(convertLocalitiesToDetail(supplierRole.getLocalities()));
        return supplierDetail;
    }

}