package com.projeto.api.controller;

import com.projeto.api.model.EstatisticaDTO;
import com.projeto.api.model.Transacao;
import com.projeto.api.service.TransacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(TransacaoService service) {
        this.service = service;
    }

    // Endpoint público para adicionar transações
    @PostMapping("/publico")
    public ResponseEntity<Void> postTransacao(@RequestBody @Valid Transacao transacao) {
        if (transacao.getDataHora().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data futura não é permitida.");
        }
        service.adicionar(transacao);
        return ResponseEntity.status(201).build();
    }

    // Endpoint USER/ADMIN: Estatísticas últimos 60 segundos
    @GetMapping("/user/estatistica")
    public EstatisticaDTO getEstatisticasRecentes() {
        LocalDateTime agora = LocalDateTime.now();
        List<Transacao> ultimos60 = service.getPorPeriodo(agora.minusSeconds(60), agora);
        return service.calcularEstatisticas(ultimos60);
    }

    // Endpoint USER/ADMIN: Estatísticas por período
    @PostMapping("/user/periodo")
    public EstatisticaDTO getPorPeriodo(@RequestBody PeriodoComBancoDTO dto) {
        return service.calcularEstatisticas(service.getPorPeriodo(dto.getDataInicial(), dto.getDataInicial()));
    }

    // Endpoint USER/ADMIN: Buscar transações por CPF
    @GetMapping("/user/cpf")
    public ResponseEntity<List<Transacao>> buscarPorCpf(@RequestParam String valor) {
        return ResponseEntity.ok(service.buscarPorCpf(valor));
    }

    // Endpoint USER/ADMIN: Buscar transações por nome
    @GetMapping("/user/nome")
    public ResponseEntity<List<Transacao>> buscarPorNome(@RequestParam String valor) {
        return ResponseEntity.ok(service.buscarPorNome(valor));
    }

    // Endpoint USER/ADMIN: Buscar última transação
    @GetMapping("/user/ultima")
    public ResponseEntity<Transacao> getUltima() {
        Transacao ultima = service.getUltima();
        return (ultima != null) ? ResponseEntity.ok(ultima) : ResponseEntity.noContent().build();
    }

    // Endpoint ADMIN: Atualizar nome, CPF e banco
    @PutMapping("/admin/{id}")
    public ResponseEntity<Transacao> atualizarTransacao(@PathVariable Integer id, @RequestBody Transacao novaTransacao) {
        return ResponseEntity.ok(service.atualizarTransacao(id, novaTransacao));
    }

    // Endpoint ADMIN: Deletar por ID
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deletarPorId(@PathVariable Integer id) {
        service.deletarPorId(id);
        return ResponseEntity.noContent().build();
    }

    // Endpoint ADMIN: Deletar por período e banco
    @DeleteMapping("/admin/periodo")
    public ResponseEntity<Void> deletarPorPeriodo(@RequestBody Integer dto) {
        service.deletarPorId(dto);
        return ResponseEntity.noContent().build();
    }
}
