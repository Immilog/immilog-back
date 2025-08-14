package com.backend.immilog.company.domain.service;

import com.backend.immilog.company.domain.model.Company;
import com.backend.immilog.company.exception.CompanyErrorCode;
import com.backend.immilog.company.exception.CompanyException;
import org.springframework.stereotype.Service;

@Service
public class CompanyValidationService {

    public void validateCompanyExists(Company company) {
        if (company == null || company.isEmpty()) {
            throw new CompanyException(CompanyErrorCode.COMPANY_NOT_FOUND);
        }
    }

    public void validateCompanyName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_NAME);
        }
        if (name.length() > 100) {
            throw new CompanyException(CompanyErrorCode.COMPANY_NAME_TOO_LONG);
        }
    }

    public void validateCompanyEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_EMAIL);
        }
        if (!isValidEmail(email)) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_EMAIL_FORMAT);
        }
    }

    public void validateCompanyPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_PHONE);
        }
        if (!isValidPhone(phone)) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_PHONE_FORMAT);
        }
    }

    public void validateCompanyAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_ADDRESS);
        }
        if (address.length() > 500) {
            throw new CompanyException(CompanyErrorCode.COMPANY_ADDRESS_TOO_LONG);
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }

    private boolean isValidPhone(String phone) {
        return phone.matches("^[0-9-+()\\s]+$");
    }
}