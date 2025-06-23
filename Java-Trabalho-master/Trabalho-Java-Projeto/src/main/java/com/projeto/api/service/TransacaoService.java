package com.projeto.api.service;

import com.projeto.api.model.Transacao;
import com.projeto.api.model.EstatisticaDTO;
import com.projeto.api.repository.TransacaoRepository;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository repository;

    public void adicionar(Transacao t) {
        repository.save(t);
    }

    public List<Transacao> getTodas() {
        return repository.findAll();
    }

    public List<Transacao> getPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return repository.findAll().stream()
                .filter(t -> !t.getDataHora().isBefore(inicio) && !t.getDataHora().isAfter(fim))
                .toList();
    }

    public EstatisticaDTO calcularEstatisticas(List<Transacao> lista) {
        if (lista.isEmpty()) return EstatisticaDTO.vazia();
        double sum = lista.stream().mapToDouble(Transacao::getValor).sum();
        long count = lista.size();
        double avg = sum / count;
        double min = lista.stream().mapToDouble(Transacao::getValor).min().orElse(0.0);
        double max = lista.stream().mapToDouble(Transacao::getValor).max().orElse(0.0);
        return new EstatisticaDTO(count, sum, avg, min, max);
    }

    public Transacao getUltima() {
        return repository.findAll().stream()
                .max(Comparator.comparing(Transacao::getDataHora)).orElse(null);
    }

    public List<Transacao> buscarPorCpf(String cpf) {
        return repository.findByCpf(cpf);

    }

    public List<Transacao> buscarPorNome(String nome) {
        return repository.findByNomeContainingIgnoreCase(nome);
    }

    @Transactional
    public Transacao atualizarTransacao(Integer id, Transacao novaTransacao) {
        Optional<Transacao> transacaoOptional = repository.findById(id);
        if (transacaoOptional.isPresent()) {
            Transacao existente = transacaoOptional.get();
            existente.setNome(novaTransacao.getNome());
            existente.setCpf(novaTransacao.getCpf());
            existente.setBanco(novaTransacao.getBanco());
            return repository.save(existente);
        } else {
            throw new RuntimeException("Transação com ID " + id + " não encontrada.");
        }
    }

    public void deletarPorId(Integer id) {
        repository.deleteById(id);
    }

    public void deletarPorPeriodo(com.projeto.api.controller.PeriodoComBancoDTO dto) {
        List<Transacao> porPeriodo = repository.findAll().stream()
                .filter(t -> t.getBanco().equalsIgnoreCase(dto.getBanco()))
                .filter(t -> !t.getDataHora().isBefore(dto.getDataInicial()) && !t.getDataHora().isAfter(dto.getDataFinal()))
                .toList();

        repository.deleteAll(porPeriodo);
    }
}
