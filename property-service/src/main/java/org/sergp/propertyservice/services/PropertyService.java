package org.sergp.propertyservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.propertyservice.dto.PropertyDTO;
import org.sergp.propertyservice.dto.PropertyUpdate;
import org.sergp.propertyservice.exceptions.PropertyAlreadyExistException;
import org.sergp.propertyservice.exceptions.PropertyNotFoundException;
import org.sergp.propertyservice.mappers.PropertyMapper;
import org.sergp.propertyservice.models.Property;
import org.sergp.propertyservice.repositories.PropertyRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PropertyService {

    private final PropertyRepository propertyRepository;

    public PropertyDTO createProperty(Property property) {
        if (isPropertyExist(property.getAddress())) {
            throw new PropertyAlreadyExistException("Address already exist");
        }
        property.setOwnerId(UUID.randomUUID()); // TODO change owner id
        property.setStatus(true); // true - is active / false - is inactive
        return PropertyMapper.INSTANCE.propertyToPropertyDTO(propertyRepository.save(property));
    }

    public List<PropertyDTO> getAllProperties(){
        return PropertyMapper.INSTANCE.propertiesListToPropertyDTOs(propertyRepository.findAll());
    }

    public PropertyDTO findById(UUID uuid) {
        return PropertyMapper.INSTANCE.propertyToPropertyDTO(getById(uuid));
    }

    public PropertyDTO updateProperty(PropertyUpdate propertyUpdate, UUID uuid) {
        Property currentProperty = getById(uuid);
        // change address
        if(propertyUpdate.getAddress() != null && !propertyUpdate.getAddress().isBlank()){
            currentProperty.setAddress(propertyUpdate.getAddress());
        }
        // change description
        if(propertyUpdate.getDescription() != null && !propertyUpdate.getDescription().isBlank()){
            currentProperty.setDescription(propertyUpdate.getDescription());
        }
        // change price
        if(propertyUpdate.getPrice() != null && propertyUpdate.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            currentProperty.setPrice(propertyUpdate.getPrice());
        }
        // change beds
        if(propertyUpdate.getBedrooms() != null && propertyUpdate.getBedrooms() > 0) {
            currentProperty.setBedrooms(propertyUpdate.getBedrooms());
        }

        return PropertyMapper.INSTANCE.propertyToPropertyDTO(propertyRepository.save(currentProperty));
    }

    public void deletePropertyById(UUID uuid){
        try {
            propertyRepository.deleteById(uuid);
        }
        catch (EmptyResultDataAccessException ex){
            throw new PropertyNotFoundException("Property %s not found".formatted(uuid));
        }
    }

    public Boolean changeStatus(UUID uuid) {
        Property property = getById(uuid);
        property.setStatus(!property.getStatus());
        propertyRepository.save(property);
        return property.getStatus();
    }

    public List<PropertyDTO> getAllActiveProperties() {
        return PropertyMapper.INSTANCE.propertiesListToPropertyDTOs(propertyRepository.findPropertyByStatusIsTrue());
    }

// private methods

    private Property getById(UUID uuid) {
        return propertyRepository.findById(uuid)
                .orElseThrow(()-> new PropertyNotFoundException("Property %s not found".formatted(uuid)));
    }

    private Boolean isPropertyExist(String address) {
        return propertyRepository.findPropertyByAddress(address).isPresent();
    }



}
