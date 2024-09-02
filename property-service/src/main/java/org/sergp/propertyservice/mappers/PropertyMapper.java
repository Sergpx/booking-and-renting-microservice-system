package org.sergp.propertyservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sergp.propertyservice.dto.PropertyRequest;
import org.sergp.propertyservice.dto.PropertyResponse;
import org.sergp.propertyservice.models.Property;

import java.util.List;

@Mapper
public interface PropertyMapper {

    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    // PropertyRequest
    PropertyRequest propertyToPropertyRequest(Property property);
    Property propertyRequestToProperty(PropertyRequest propertyRequest);
    List<PropertyResponse> propertiesListToPropertyResponses(List<Property> properties);

    // PropertyResponse
    PropertyResponse propertyToPropertyResponse(Property property);
    Property propertyResponseToProperty(PropertyResponse propertyResponse);
}
