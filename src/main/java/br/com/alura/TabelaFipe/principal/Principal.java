package br.com.alura.TabelaFipe.principal;

import br.com.alura.TabelaFipe.model.Dados;
import br.com.alura.TabelaFipe.model.Modelos;
import br.com.alura.TabelaFipe.model.Veiculo;
import br.com.alura.TabelaFipe.service.ConsultaAPI;
import br.com.alura.TabelaFipe.service.ConverteDados;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.*;
import java.util.stream.Collectors;


public class Principal {
    private Scanner leitura = new Scanner(System.in);
    private ConsultaAPI consulta = new ConsultaAPI();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://parallelum.com.br/fipe/api/v1/";


    public void exibeMenu() throws JsonProcessingException {
        String menu= """
                *-*-*-*-*OPÇÕES*-*-*-*-*
              
                carros
                motos
                caminhoes
                
                Digite para consultar os valores:
                """;
        System.out.println(menu);
        var opcao = leitura.nextLine();
        String url;

        //pegando opcao
        if (opcao.toLowerCase().contains("carr")){
            url = ENDERECO + "carros/marcas/";
        } else if (opcao.toLowerCase().contains("mot")) {
            url = ENDERECO + "motos/marcas/";
        } else {
            url = ENDERECO + "caminhoes/marcas/";
        }

        var json = consulta.obterDados(url);

        var marcas = conversor.obterLista(json, Dados.class);
        marcas.stream().sorted(Comparator.comparing(Dados::codigo)).
                forEach(System.out::println);

        System.out.println("Informe o código da marca para consulta: ");
        var cod = leitura.nextLine();

        url = url + cod + "/modelos/";
        json = consulta.obterDados(url);
        System.out.println(json);

        var modeloLista = conversor.obterDados(json, Modelos.class);
        System.out.println("modelos dessa marca: ");
        modeloLista.modelos().stream()
                .sorted(Comparator.comparing(Dados::codigo))
                .forEach(System.out::println);

        System.out.println("Digite um trecho do carro a ser buscado: ");
        var nomeVeiculo = leitura.nextLine();


        List<Dados> modelosFiltrados = modeloLista.modelos().stream()
                .filter(m->m.nome().toLowerCase().contains(nomeVeiculo.toLowerCase()))
                .collect(Collectors.toList());
        System.out.println("\nModelos filtrados: ");
        modelosFiltrados.forEach(System.out::println);


        System.out.println("Digite por favor o código do modelos desejado: ");
        var codigoModelo = leitura.nextLine();

        url = url + codigoModelo + "/anos/";
        json = consulta.obterDados(url);
        List<Dados> anos = conversor.obterLista(json, Dados.class);
        List<Veiculo> veiculos = new ArrayList<>();

        for (int i = 0; i < anos.size(); i++) {
            var enderecoAnos = url + anos.get(i).codigo();
            json =  consulta.obterDados(enderecoAnos);
            Veiculo veiculo = conversor.obterDados(json, Veiculo.class);
            veiculos.add(veiculo);
        }

        System.out.println("\nTodos os veiculos filtrados com avaliação por ano");
        veiculos.forEach(System.out::println);



    }
}
