package br.com.uploadmascaras.controller;


import br.com.uploadmascaras.DTO.FilesDTO;
import br.com.uploadmascaras.DTO.Resposta;
import br.com.uploadmascaras.service.FilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/mascaras")
@Slf4j
public class MascarasController {

    @Autowired
    FilesService service;

    @PostMapping(value = "/inputfile", consumes = "multipart/form-data")
    public ResponseEntity<Resposta> inputFile(@RequestBody MultipartFile file){

        try {
            service.salvaArquivo(file);
            return ResponseEntity.ok(Resposta
                    .builder()
                    .resposta(FilesDTO
                            .builder()
                            .nomeArquivo(file.getOriginalFilename())
                            .dataGravacao(LocalDateTime.now().toString())
                            .build())
                            .mensagem("Aquivo salvo com sucesso.")
                    .build());
        } catch (IOException e) {
            log.error("ERRO",e);
            return new ResponseEntity<>(Resposta
                    .builder()
                    .mensagem("Falha ao salvar arquivo.")
                    .build(),HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("/reload")
    public ResponseEntity<Resposta> recarregaMascaras(){

        try {
            service.recarregaServico();
        } catch (Exception e) {
            log.error("erro ao dar restart no serviço",e);
        }

        return ResponseEntity.ok(
                Resposta.builder()
                        .mensagem("Reload de máscaras feito")
                        .build()
        );

    }


}
