package com.projeto.api.service;

import com.projeto.api.model.Transacao;
import com.projeto.api.model.EstatisticaDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class TransacaoService {

    private final List<Transacao> transacoes = new CopyOnWriteArrayList<>();

    public void adicionar(Transacao t) {
        transacoes.add(t);
    }

    public void limpar() {
        transacoes.clear();
    }

    public List<Transacao> getTodas() {
        return transacoes;
    }

    public List<Transacao> getPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return transacoes.stream()
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
        return transacoes.stream().max(Comparator.comparing(Transacao::getDataHora)).orElse(null);
    }

    public void excluirPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        transacoes.removeIf(t -> !t.getDataHora().isBefore(inicio) && !t.getDataHora().isAfter(fim));
    }
}
