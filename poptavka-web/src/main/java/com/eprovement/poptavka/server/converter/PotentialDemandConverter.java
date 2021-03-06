/*
 * Copyright (C) 2011, eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.server.converter;

import com.eprovement.poptavka.domain.demand.DemandType;
import com.eprovement.poptavka.domain.message.UserMessage;
import com.eprovement.poptavka.shared.domain.demand.DemandTypeDetail;
import com.eprovement.poptavka.shared.domain.demandsModule.PotentialDemandDetail;
import org.apache.commons.lang.Validate;

/**
 * Converts UserMessage to PotrentialDemandDetail.
 * //TODO Martin switch to SupplierPotentialDetail
 * @author Juraj Martinka
 */
public final class PotentialDemandConverter extends AbstractConverter<UserMessage, PotentialDemandDetail> {

    /**************************************************************************/
    /* Attributes                                                             */
    /**************************************************************************/
    private final Converter<DemandType, DemandTypeDetail> demandTypeConverter;

    /**************************************************************************/
    /* Constructor                                                            */
    /**************************************************************************/
    /**
     * Creates PotentialDemandConverter.
     */
    private PotentialDemandConverter(Converter<DemandType, DemandTypeDetail> demandTypeConverter) {
        // Spring instantiates converters - see converters.xml
        Validate.notNull(demandTypeConverter);
        this.demandTypeConverter = demandTypeConverter;
    }

    /**************************************************************************/
    /* Convert methods                                                        */
    /**************************************************************************/
    /**
     * @{inheritDoc}
     */
    @Override
    public PotentialDemandDetail convertToTarget(UserMessage userMessage) {
        final PotentialDemandDetail detail = new PotentialDemandDetail();
        detail.setUserMessageId(userMessage.getId());
        detail.setStarred(userMessage.isStarred());
        if (userMessage.getMessage() != null) {
            detail.setMessageId(userMessage.getMessage().getId());
            if (userMessage.getMessage().getDemand() != null) {
                detail.setDemandId(userMessage.getMessage().getDemand().getId());
                detail.setTitle(userMessage.getMessage().getDemand().getTitle());
                detail.setPrice(userMessage.getMessage().getDemand().getPrice());
                detail.setDemandType(demandTypeConverter.convertToTarget(
                        userMessage.getMessage().getDemand().getType()));
                detail.setDemandStatus(userMessage.getMessage().getDemand().getStatus());
                detail.setCreatedDate(convertDate(userMessage.getMessage().getCreated()));
                detail.setEndDate(convertDate(userMessage.getMessage().getDemand().getEndDate()));
                detail.setValidToDate(convertDate(userMessage.getMessage().getDemand().getValidTo()));
                if (userMessage.getMessage().getDemand().getClient() != null) {
                    if (userMessage.getMessage().getDemand().getClient().getOveralRating() != null) {
                        detail.setRating(userMessage.getMessage().getDemand().getClient().getOveralRating());
                    } else {
                        detail.setRating(0);
                    }
                    if (userMessage.getMessage().getDemand().getClient().getBusinessUser() != null
                            && userMessage.getMessage().getDemand().getClient()
                            .getBusinessUser().getBusinessUserData() != null) {
                        detail.setSender(userMessage.getMessage().getDemand().getClient()
                                .getBusinessUser().getBusinessUserData().getDisplayName());
                    }
                }
            }
        }

        return detail;

    }

    /**
     * @{inheritDoc}
     */
    @Override
    public UserMessage convertToSource(PotentialDemandDetail potentialDemandDetail) {
        throw new UnsupportedOperationException("Conversion from PotentialDemandDetail to domain object UserMessage "
                + "is not implemented yet!");
    }
}
