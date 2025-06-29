package keysson.apis.administration.dto.response;

import lombok.Data;

@Data
public class EmpresasStatusDTO {
    private int pendente;
    private int ativo;
    private int rejeitado;

}