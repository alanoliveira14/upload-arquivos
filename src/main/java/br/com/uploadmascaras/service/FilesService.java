package br.com.uploadmascaras.service;

import br.com.uploadmascaras.DTO.FilesDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilesService {


    Environment environment;
    String path;

    @Autowired
    public FilesService(Environment environment) {
        this.environment = environment;
        this.path = environment.getProperty("file.diretorio");
    }

    public void salvaArquivo(MultipartFile file) throws IOException {

        log.info("salvando novo arquivo enviado [{}]", file.getOriginalFilename());

        String fullPath = path + file.getOriginalFilename();

        Path path = Paths.get(fullPath);

        Files.write(path, file.getInputStream().readAllBytes());

        log.info("arquivo salvo com sucesso");

        salvaJson(file.getOriginalFilename());
    }

    public void recarregaServico() throws IOException, InterruptedException {

        ProcessBuilder builder = new ProcessBuilder();

        builder.command("sh", "-c", environment.getProperty("sh.restartSendmail"));

        Process process = builder.start();

        process.waitFor();

    }


    private void salvaJson(String fileName) throws IOException {

        log.info("gravando historico no json");

        FilesDTO filesDTO = FilesDTO.builder().dataGravacao(LocalDateTime.now().toString()).nomeArquivo(fileName).build();

        ObjectMapper mapper = new ObjectMapper();

        List<FilesDTO> listFiles = new ArrayList();

        String fullPath = path + "uploadedFiles.json";

        if (Files.exists(Paths.get(fullPath))){

            log.info("JSON existe");

            listFiles = mapper.readValue(new File(fullPath), new TypeReference<List<FilesDTO>>() {});

            listFiles = listFiles.stream()
                    .filter(f -> !fileName.equalsIgnoreCase(f.getNomeArquivo()))
                    .collect(Collectors.toList());
            ;

        }

        listFiles.add(filesDTO);

        Files.write(Paths.get(fullPath),  mapper.writeValueAsBytes(listFiles));

        log.info("json salvo com sucesso.");

    }

}
