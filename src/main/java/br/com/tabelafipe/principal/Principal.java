package br.com.tabelafipe.principal;

import br.com.tabelafipe.model.Dados;
import br.com.tabelafipe.model.DadosAno;
import br.com.tabelafipe.model.Modelos;
import br.com.tabelafipe.model.Veiculo;
import br.com.tabelafipe.service.ConsumoAPI;
import br.com.tabelafipe.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner sc = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados converteDados = new ConverteDados();
    private final String ENDPOINTCARRO = "https://parallelum.com.br/fipe/api/v1/carros/marcas/";
    private final String ENDPOINTMOTO = "https://parallelum.com.br/fipe/api/v1/motos/marcas/";
    private final String ENDPOINTCAMINHAO = "https://parallelum.com.br/fipe/api/v1/caminhoes/marcas/";
    private String endereco = "";

    public void exibeMenu() {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("                   SELECIONE O TIPO DE VEÍCULO");
        System.out.println("---------------------------------------------------------------------");
        System.out.println("1 - CARRO");
        System.out.println("2 - MOTO");
        System.out.println("3 - CAMINHÃO");
        System.out.println("---------------------------------------------------------------------");

        var tipoVeiculo = sc.nextInt();

        if (tipoVeiculo == 1) {
            endereco = ENDPOINTCARRO;
        } else if (tipoVeiculo == 2) {
            endereco = ENDPOINTMOTO;
        } else if (tipoVeiculo == 3) {
            endereco = ENDPOINTCAMINHAO;
        }

        var json = consumoAPI.obterDados(endereco);

        var marcas = converteDados.obterListaDados(json, Dados.class);

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("                  SELECIONE A MARCA DO VEÍCULO");
        System.out.println("---------------------------------------------------------------------");

        marcas.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(d -> System.out.println(
                        "Código: " + d.codigo() + "      Marca: " + d.nome() +
                                "\n---------------------------------------------------------------------"
                ));

        var codigoMarca = sc.nextInt();
        sc.nextLine();

        endereco = endereco + codigoMarca + "/modelos/";
        json = consumoAPI.obterDados(endereco);

        var modelosLista = converteDados.obterDados(json, Modelos.class);

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("                   SELECIONE O MODELO DA MARCA");
        System.out.println("---------------------------------------------------------------------");

        modelosLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(d -> System.out.println(
                    "Código: " + d.codigo() + "      Modelo: " + d.nome() +
                            "\n---------------------------------------------------------------------"
        ));

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("                   PESQUISE O MODELO DA MARCA");
        System.out.println("---------------------------------------------------------------------");

        var nomeVeiculo = sc.nextLine();

        List<Dados> modelosFiltrados = modelosLista.modelos().stream()
                .filter(m -> m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("                        MODELOS ENCONTRADOS");
        System.out.println("---------------------------------------------------------------------");

        modelosFiltrados.stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(m -> System.out.println(
                        "Código: " + m.codigo() + "      Modelo: " + m.nome() +
                                "\n---------------------------------------------------------------------"
                ));

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("               SELECIONE O CÓDIGO DO MODELO PESQUISADO");
        System.out.println("---------------------------------------------------------------------");

        var codigoModelo = sc.nextInt();

        endereco = endereco + codigoModelo + "/anos/";
        json = consumoAPI.obterDados(endereco);

        List<DadosAno> anos = converteDados.obterListaDados(json, DadosAno.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAno = endereco + anos.get(i).codigo() + "/";
            json = consumoAPI.obterDados(enderecoAno);

            Veiculo veiculo = converteDados.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\n---------------------------------------------------------------------");
        System.out.println("        AVALIAÇÕES DO MODELO SELECIONADO ORDENADOS POR ANO");
        System.out.println("---------------------------------------------------------------------");

        veiculos.stream()
                .sorted(Comparator.comparing(Veiculo::ano))
                .forEach(v -> System.out.println(
                        "Marca: " + v.marca() +
                                "\nModelo: " + v.modelo() +
                                "\nAno: " + v.ano() +
                                "\nCombustível: " + v.combustivel() +
                                "\nValor: " + v.valor() + "\n"
                ));



    }
}
