package keysson.apis.administration.controller;

import keysson.apis.administration.dto.request.ChangeAccountStatusRequest;
import keysson.apis.administration.dto.request.CreateDepartmentRequest;
import keysson.apis.administration.dto.request.DeleteDepartmentRequest;
import keysson.apis.administration.dto.response.CompanyStatusDTO;
import keysson.apis.administration.dto.response.DepartmentResponse;
import keysson.apis.administration.dto.response.PendingCompanyDTO;
import keysson.apis.administration.exception.BusinessRuleException;
import keysson.apis.administration.service.AdministrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdministrationControllerImpl implements AdministrationController{

    private final AdministrationService administrationService;

    @Autowired
    public AdministrationControllerImpl(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @Override
    public ResponseEntity<List<PendingCompanyDTO>> getPendingCompany(int accountNumber) throws BusinessRuleException {
        return administrationService.pendingCompany(accountNumber);
    }

    @Override
    public void putStatusAccount(int numeroConta, ChangeAccountStatusRequest requestBody) throws BusinessRuleException {
        administrationService.changeStatus(requestBody.getNewStatus(), numeroConta);
    }

    @Override
    public CompanyStatusDTO getStatusCompanies() throws BusinessRuleException {
        return administrationService.StatusCompanies();
    }

    @Override
    public void postDepartment(@RequestBody CreateDepartmentRequest requestBody) throws BusinessRuleException {
        administrationService.registerDepartment(requestBody);
    }

    @Override
    public List<DepartmentResponse> getAllDepartments() throws BusinessRuleException {
        return administrationService.searchAllDepartments();
    }

    @Override
    public void deleteDepartmentById(@RequestBody DeleteDepartmentRequest requestBody) throws BusinessRuleException {
        administrationService.deleteDepartment(requestBody);
    }


}
