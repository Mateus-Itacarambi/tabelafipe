package br.com.tabelafipe.service;

import java.util.List;

public interface IConverteDados {
    <T> T obterDados(String json, Class<T> classe);

    <T> List<T> obterListaDados(String json, Class<T> classe);
}
