package org.sergp.propertyservice.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.sergp.propertyservice.dto.PropertyRequest;
import org.sergp.propertyservice.dto.PropertyResponse;
import org.sergp.propertyservice.dto.PropertyUpdate;
import org.sergp.propertyservice.services.PropertyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/properties")
@Slf4j
public class PropertyController {

    private final PropertyService propertyService;

    @GetMapping
    public ResponseEntity<List<PropertyResponse>> getAllProperties() {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.getAllProperties());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PropertyResponse>> getAllActiveProperties() {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.getAllActiveProperties());
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<PropertyResponse> findById(@PathVariable UUID uuid) {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.findById(uuid));
    }

    @PostMapping
    public ResponseEntity<PropertyResponse> createProperty(@RequestBody PropertyRequest propertyRequest, HttpServletRequest request) {
        log.info(propertyRequest.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(propertyService.createProperty(propertyRequest, request));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<PropertyResponse> updateProperty(@Valid @RequestBody PropertyUpdate propertyUpdate, @PathVariable UUID uuid, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.updateProperty(propertyUpdate, uuid, request));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteProperty(@PathVariable UUID uuid, HttpServletRequest request) {
        propertyService.deletePropertyById(uuid, request);
        return ResponseEntity.status(HttpStatus.OK).body("Property deleted");
    }

    @PatchMapping("/{uuid}/changeStatus")
    public ResponseEntity<String> changeStatus(@PathVariable UUID uuid, HttpServletRequest request) {
        Boolean status = propertyService.changeStatus(uuid, request);
        return ResponseEntity.status(HttpStatus.OK).body("Property status changed to " + (status ? "active" : "inactive"));
    }

    @GetMapping("/{id}/isPropertyActive")
    public ResponseEntity<?> isPropertyActive(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.isPropertyActive(id));
    }

    // TODO add property search filters
}
