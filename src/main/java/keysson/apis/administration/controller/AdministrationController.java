package keysson.apis.administration.controller;

import io.swagger.v3.oas.annotations.Operation;
import keysson.apis.administration.dto.response.EmpresaPendenteDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AdministrationController {

    @GetMapping("/empresa/pendente/{numero-conta}")
    @Operation(
            summary = "Busca empresas com status pendente.",
            description = "Endpoint para verificar empresas pendentes"
    )
    public ResponseEntity<EmpresaPendenteDTO> getPendingCompany (
            @PathVariable("numero-conta") String numeroConta
    )throws BusinessRuleException {
        return ResponseEntity.ok(new EmpresaPendenteDTO());
    }
}
