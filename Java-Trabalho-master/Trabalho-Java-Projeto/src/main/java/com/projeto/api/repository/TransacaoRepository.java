package com.projeto.api.repository;

import com.projeto.api.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Integer> {
    List<Transacao> findByCpf(String cpf);
    List<Transacao> findByNomeContainingIgnoreCase(String nome);
}
