package com.itechart.warehouse.service.services;


import com.itechart.warehouse.dto.IncomingInvoiceDTO;
import com.itechart.warehouse.dto.OutgoingInvoiceDTO;
import com.itechart.warehouse.entity.Invoice;
import com.itechart.warehouse.entity.InvoiceStatus;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;

import java.util.List;

public interface InvoiceService {
    List<Invoice> findAllInvoices() throws DataAccessException;

    List<IncomingInvoiceDTO> findAllIncomingInvoices() throws DataAccessException;

    List<OutgoingInvoiceDTO> findAllOutgoingInvoices() throws DataAccessException;

    Invoice findInvoiceById(Long id) throws DataAccessException;

    Invoice findInvoiceByNumber(String number) throws DataAccessException;

    Invoice saveInvoice(Invoice invoice) throws DataAccessException;

    Invoice saveIncomingInvoice(IncomingInvoiceDTO invoice) throws DataAccessException;

    Invoice saveOutgoingInvoice(OutgoingInvoiceDTO invoice) throws DataAccessException;

    Invoice updateInvoice(Invoice invoice) throws DataAccessException;

    InvoiceStatus updateInvoiceStatus(String id, String status)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException;

    void deleteInvoice(Invoice invoice) throws DataAccessException;

    boolean invoiceExists(Invoice invoice) throws DataAccessException;
}
