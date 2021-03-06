/*
 * Copyright (C) 2011, eProvement s.r.o. All rights reserved.
 */
package com.eprovement.poptavka.server.converter;

import com.eprovement.poptavka.base.BasicIntegrationTest;
import com.eprovement.poptavka.domain.address.Locality;
import com.eprovement.poptavka.domain.enums.LocalityType;
import com.eprovement.poptavka.shared.selectors.addressSelector.AddressSuggestionDetail;
import org.apache.commons.lang.Validate;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AddressSuggestionConverterTest extends BasicIntegrationTest {

    @Autowired
    @Qualifier("addressSuggestionConverter")
    Converter<Locality, AddressSuggestionDetail> addressSuggestionConverter;

    @Test
    public void testConvertToTarget() throws Exception {
        Validate.notNull(addressSuggestionConverter);
        final Locality locality = createTestLocalityCity();
        final AddressSuggestionDetail localitySuggestionDetail = addressSuggestionConverter.convertToTarget(locality);

        assertNotNull(localitySuggestionDetail);
        assertThat(Long.toString(localitySuggestionDetail.getCityId()), is("4"));
        assertThat(localitySuggestionDetail.getCityId(), is(4L));
        assertThat(localitySuggestionDetail.getCityName(), is("cityName"));
        assertThat(Long.toString(localitySuggestionDetail.getStateId()), is("2"));
        assertThat(localitySuggestionDetail.getStateId(), is(2L));
        assertThat(localitySuggestionDetail.getStateName(), is("regionName"));
    }

    private Locality createTestLocalityCity() {
        //CITY --> DISTRICT --> STATE --> COUNTRY
        final Locality locality = createLocalityCity();
        locality.setParent(createLocalityDistrict());
        locality.getParent().setParent(createLocalityRegion());
        return locality;
    }

    private Locality createLocalityCountry() {
        final Locality country = new Locality();
        country.setId(1L);
        country.setId(1L);
        country.setName("countryName");
        country.setType(LocalityType.COUNTRY);
        return country;
    }

    private Locality createLocalityRegion() {
        final Locality region = new Locality();
        region.setId(2L);
        region.setId(2L);
        region.setName("regionName");
        region.setType(LocalityType.REGION);
        return region;
    }

    private Locality createLocalityDistrict() {
        final Locality district = new Locality();
        district.setId(3L);
        district.setId(3L);
        district.setName("districtName");
        district.setType(LocalityType.DISTRICT);
        return district;
    }

    private Locality createLocalityCity() {
        final Locality city = new Locality();
        city.setId(4L);
        city.setId(4L);
        city.setName("cityName");
        city.setType(LocalityType.CITY);
        return city;
    }
}
