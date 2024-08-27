package org.sergp.propertyservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.sergp.propertyservice.dto.PropertyDTO;
import org.sergp.propertyservice.models.Property;

import java.util.List;

@Mapper
public interface PropertyMapper {

    PropertyMapper INSTANCE = Mappers.getMapper(PropertyMapper.class);

    PropertyDTO propertyToPropertyDTO(Property property);
    List<PropertyDTO> propertiesListToPropertyDTOs(List<Property> properties);

}
