package keysson.apis.administration.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmpresaPendenteDTO {

    private int id;
    private String cnpj;
    private int status;
    private String descricao;
    private String nome;
    private int numeroConta;
}
