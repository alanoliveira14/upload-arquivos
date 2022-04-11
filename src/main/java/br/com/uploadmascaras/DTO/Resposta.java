package br.com.uploadmascaras.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Resposta {

    private String mensagem;
    private Object resposta;

}
