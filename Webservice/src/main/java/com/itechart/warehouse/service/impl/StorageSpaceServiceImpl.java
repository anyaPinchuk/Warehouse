package com.itechart.warehouse.service.impl;

import com.itechart.warehouse.dao.*;
import com.itechart.warehouse.dao.exception.GenericDAOException;
import com.itechart.warehouse.dto.StorageSpaceDTO;
import com.itechart.warehouse.entity.*;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.StorageSpaceService;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for storage space
 * Created by Lenovo on 07.05.2017.
 */

@Service
public class StorageSpaceServiceImpl implements StorageSpaceService {
    private StorageSpaceDAO storageSpaceDAO;
    private WarehouseDAO warehouseDAO;
    private StorageSpaceTypeDAO storageSpaceTypeDAO;
    private Logger logger = LoggerFactory.getLogger(StorageSpaceServiceImpl.class);

    @Autowired
    public void setStorageSpaceDAO(StorageSpaceDAO storageSpaceDAO) {
        this.storageSpaceDAO = storageSpaceDAO;
    }

    @Autowired
    public void setWarehouseDAO(WarehouseDAO warehouseDAO) {
        this.warehouseDAO = warehouseDAO;
    }

    @Autowired
    public void setStorageSpaceTypeDAO(StorageSpaceTypeDAO storageSpaceTypeDAO) {
        this.storageSpaceTypeDAO = storageSpaceTypeDAO;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasPermission(#idWarehouse, 'Warehouse', 'GET')")
    public List<StorageSpace> findStorageByWarehouseId(Long idWarehouse) throws DataAccessException, IllegalParametersException {
        logger.info("Find storage by id warehouse: {}", idWarehouse);

        List<StorageSpace> storageSpaces;
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpace.class);
        criteria.add(Restrictions.eq("warehouse.idWarehouse", idWarehouse));

        try {
            storageSpaces = storageSpaceDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return storageSpaces;
    }

    @Override
    @Transactional(readOnly = true)
    //it's not private information => don't need in the security
    public List<StorageSpaceType> findAllStorageSpaceType() throws DataAccessException {
        logger.info("Find all storage space type");

        List<StorageSpaceType> storageSpaceTypes;
        DetachedCriteria criteria = DetachedCriteria.forClass(StorageSpaceType.class);

        try {
            storageSpaceTypes = storageSpaceTypeDAO.findAll(criteria, -1, -1);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return storageSpaceTypes;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#storageSpaceDTO.idWarehouse, 'Warehouse', 'POST')")
    public StorageSpace createStorageSpace(StorageSpaceDTO storageSpaceDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        if (storageSpaceDTO == null) {
            throw new IllegalParametersException("storage space DTO is null");
        }

        logger.info("Creating storage space from DTO: {}", storageSpaceDTO);
        try {
            StorageSpace storageSpace = new StorageSpace();
            storageSpace.setStorageCellList(new ArrayList<>());//so we are creating only place
            storageSpace.setWarehouse(warehouseDAO.findById(storageSpaceDTO.getIdWarehouse()).get());
            storageSpace.setStorageSpaceType(storageSpaceTypeDAO.findById(storageSpaceDTO.getIdStorageSpaceType()).get());
            storageSpace.setStatus(storageSpaceDTO.getStatus());
            if (storageSpace.getWarehouse() != null && storageSpace.getStorageSpaceType() != null ) {
                storageSpace = storageSpaceDAO.insert(storageSpace);
                return storageSpace;
            } else {
                throw new ResourceNotFoundException("Such data was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving storage space: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(#storageSpaceDTO.idWarehouse, 'Warehouse', 'PUT')")
    public StorageSpace updateStorageSpace(StorageSpaceDTO storageSpaceDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        if (storageSpaceDTO == null || storageSpaceDTO.getIdStorageSpace() == null) {
            throw new IllegalParametersException("Id or storage space DTO is null");
        }
        logger.info("Updating storage space with id {} from DTO: {}", storageSpaceDTO.getIdStorageSpace(), storageSpaceDTO);

        try {
            Optional<StorageSpace> storageSpaceResult = storageSpaceDAO.findById(storageSpaceDTO.getIdStorageSpace());
            if (storageSpaceResult.isPresent()) {
                StorageSpace storageSpace = storageSpaceResult.get();
                Warehouse warehouse = warehouseDAO.findById(storageSpaceDTO.getIdWarehouse()).get();
                StorageSpaceType storageSpaceType = storageSpaceTypeDAO.findById(storageSpaceDTO.getIdStorageSpaceType()).get();
                Long idStorageSpace = storageSpaceDTO.getIdStorageSpace();

                if(warehouse == null || storageSpaceType == null
                        || idStorageSpace==null) {
                    throw new IllegalParametersException("Can't find such data in the database");
                }
                else {
                    storageSpace.setWarehouse(warehouse);
                    storageSpace.setStorageSpaceType(storageSpaceType);
                    storageSpace.setIdStorageSpace(idStorageSpace);
                    storageSpace.setStatus(storageSpaceDTO.getStatus());
                    storageSpace = storageSpaceDAO.update(storageSpace);
                    return storageSpace;
                }
            } else {
                throw new ResourceNotFoundException("Storage Space with such id was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during saving Storage Space: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    /**
     * Because this method don't delete really in the database
     * and merely change status, this method can call twice:
     * when you "delete" entity and "restore" entity,
     * so this method just change status to opposite
     * */
    @Override
    @Transactional
    @PreAuthorize("hasPermission(#idSpace, 'Space', 'Delete')")
    public void deleteStorageSpace(Long idSpace) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        if (idSpace == null) {
            throw new IllegalParametersException("Id is null");
        }
        logger.info("Deleting storage space with id: {}", idSpace);
        try {
            Optional<StorageSpace> result = storageSpaceDAO.findById(idSpace);
            if (result.isPresent()) {
                result.get().setStatus(!result.get().getStatus());//so can recovery it, merely change status to opposite
                storageSpaceDAO.update(result.get());
            } else {
                throw new ResourceNotFoundException("Storage space with such id was not found");
            }
        } catch (GenericDAOException e) {
            logger.error("Error during deleting storage space: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseCompany findWarehouseCompanyBySpace(Long idSpace) throws DataAccessException {
        logger.info("Find warehouse company by id of cell: {}", idSpace);

        WarehouseCompany warehouseCompany;
        try {
            warehouseCompany = storageSpaceDAO.findWarehouseCompanyBySpace(idSpace);
        } catch (GenericDAOException e) {
            logger.error("Error during searching for warehouse: {}", e.getMessage());
            throw new DataAccessException(e.getCause());
        }
        return warehouseCompany;
    }
}
