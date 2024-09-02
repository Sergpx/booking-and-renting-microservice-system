package org.sergp.propertyservice.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sergp.propertyservice.dto.*;
import org.sergp.propertyservice.exceptions.AccessDeniedException;
import org.sergp.propertyservice.exceptions.PropertyAlreadyExistException;
import org.sergp.propertyservice.exceptions.PropertyNotFoundException;
import org.sergp.propertyservice.mappers.PropertyMapper;
import org.sergp.propertyservice.models.Property;
import org.sergp.propertyservice.repositories.PropertyRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PropertyService {

    private final PropertyRepository propertyRepository;

    @Transactional(readOnly = true)
    public PropertyResponse findById(UUID uuid) {
        return PropertyMapper.INSTANCE.propertyToPropertyResponse(getById(uuid));
    }
    @Transactional(readOnly = true)
    public List<PropertyResponse> getAllProperties(){
        return PropertyMapper.INSTANCE.propertiesListToPropertyResponses(propertyRepository.findAll());
    }

    public PropertyResponse createProperty(PropertyRequest propertyRequest, HttpServletRequest request) {
        if (isPropertyExist(propertyRequest.getAddress())) {
            throw new PropertyAlreadyExistException("Address already exist");
        }
        Property property = PropertyMapper.INSTANCE.propertyRequestToProperty(propertyRequest);
        property.setOwnerId(UUID.fromString(request.getHeader("id")));
        property.setStatus(true); // true - is active / false - is inactive
        property.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        property.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return PropertyMapper.INSTANCE.propertyToPropertyResponse(propertyRepository.save(property));
    }



    public PropertyResponse updateProperty(PropertyUpdate propertyUpdate, UUID uuid, HttpServletRequest request) {
        Property currentProperty = getById(uuid);

        if (!currentProperty.getOwnerId().equals(UUID.fromString(request.getHeader("id")))){
            throw new AccessDeniedException("You are not owner of this property");
        }
        // change address
        if(propertyUpdate.getAddress() != null){
            currentProperty.setAddress(propertyUpdate.getAddress());
        }
        // change title
        if(propertyUpdate.getTitle() != null) {
            currentProperty.setTitle(propertyUpdate.getTitle());
        }
        // change description
        if(propertyUpdate.getDescription() != null){
            currentProperty.setDescription(propertyUpdate.getDescription());
        }
        // change price
        if(propertyUpdate.getPrice() != null) {
            currentProperty.setPrice(propertyUpdate.getPrice());
        }
        // change beds
        if(propertyUpdate.getBedrooms() != null ) {
            currentProperty.setBedrooms(propertyUpdate.getBedrooms());
        }

        currentProperty.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));

        return PropertyMapper.INSTANCE.propertyToPropertyResponse(propertyRepository.save(currentProperty));
    }

    //TODO check this method try catch block
    public void deletePropertyById(UUID uuid, HttpServletRequest request){
        Property property = getById(uuid);
        if (!property.getOwnerId().equals(UUID.fromString(request.getHeader("id")))){
            throw new AccessDeniedException("You are not owner of this property");
        }
        try {
            propertyRepository.deleteById(uuid);
        }
        catch (EmptyResultDataAccessException ex){
            throw new PropertyNotFoundException("Property %s not found".formatted(uuid));
        }
    }

    public Boolean changeStatus(UUID uuid, HttpServletRequest request) {
        Property property = getById(uuid);
        if (!property.getOwnerId().equals(UUID.fromString(request.getHeader("id")))){
            throw new AccessDeniedException("You are not owner of this property");
        }
        property.setStatus(!property.getStatus());
        property.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        propertyRepository.save(property);
        return property.getStatus();
    }

    @Transactional(readOnly = true)
    public List<PropertyResponse> getAllActiveProperties() {
        return PropertyMapper.INSTANCE.propertiesListToPropertyResponses(propertyRepository.findPropertyByStatusIsTrue());
    }

    // TODO Think about this method
    @Transactional(readOnly = true)
    public PropertyAvailableResponse isPropertyActive(UUID id) {
        Property property = getById(id);
        return PropertyAvailableResponse.builder()
                .status(property.getStatus())
                .price(property.getPrice())
                .build();
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
