package com.projeto.api.controller;

import com.projeto.api.model.Transacao;
import com.projeto.api.model.EstatisticaDTO;
import com.projeto.api.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    @PostMapping("/transacao")
    public ResponseEntity<Void> postTransacao(@RequestBody @Valid Transacao transacao) {
        if (transacao.getDataHora().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data futura");
        }
        service.adicionar(transacao);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/transacao")
    public ResponseEntity<Void> deleteTodas() {
        service.limpar();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/estatistica")
    public EstatisticaDTO getEstatisticasRecentes() {
        LocalDateTime agora = LocalDateTime.now();
        List<Transacao> ultimos60 = service.getPorPeriodo(agora.minusSeconds(60), agora);
        return service.calcularEstatisticas(ultimos60);
    }

    @PostMapping("/transacao/periodo")
    public EstatisticaDTO getPorPeriodo(@RequestBody Map<String, String> periodo) {
        LocalDateTime ini = LocalDateTime.parse(periodo.get("dataInicial"));
        LocalDateTime fim = LocalDateTime.parse(periodo.get("dataFinal"));
        return service.calcularEstatisticas(service.getPorPeriodo(ini, fim));
    }

    @GetMapping("/transacao/ultima")
    public ResponseEntity<Transacao> getUltima() {
        Transacao ultima = service.getUltima();
        return (ultima != null) ? ResponseEntity.ok(ultima) : ResponseEntity.noContent().build();
    }

    @DeleteMapping("/transacao/periodo")
    public ResponseEntity<Void> deletePeriodo(@RequestBody Map<String, String> periodo) {
        LocalDateTime ini = LocalDateTime.parse(periodo.get("dataInicial"));
        LocalDateTime fim = LocalDateTime.parse(periodo.get("dataFinal"));
        List<Transacao> alvo = service.getPorPeriodo(ini, fim);
        if (alvo.isEmpty()) return ResponseEntity.noContent().build();
        service.excluirPeriodo(ini, fim);
        return ResponseEntity.ok().build();
    }
}
