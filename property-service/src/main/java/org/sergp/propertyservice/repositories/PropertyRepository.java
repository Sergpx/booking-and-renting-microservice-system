package org.sergp.propertyservice.repositories;

import org.sergp.propertyservice.models.Property;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PropertyRepository extends JpaRepository<Property, UUID> {

    Optional<Property> findPropertyByAddress(String address);

    List<Property> findPropertyByStatusIsTrue();

}
