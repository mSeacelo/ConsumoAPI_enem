# Consumo da API ENEM

Programa em Java que busca as questões de **Matemática** do ENEM usando a [API ENEM](https://github.com/yunger7/enem-api) (`https://api.enem.dev/v1`).

O trabalho foi feito para ajudar os professores do cursinho **Rumo à Universidade (UFOP)** a encontrarem questões do ENEM.

> **Protótipo.** Este projeto é apenas uma prova de conceito e **não tem teor de uso imediato**: ainda não está pronto para ser usado pelos professores no dia a dia.

## Como usar

Requisito: Java 17 ou superior. As questões aparecem no console, e as imagens (figuras e alternativas em imagem) aparecem como links: copie e abra no navegador.

Para trocar a edição da prova, altere `int ano = 2023;` no método `main` de `src/main/java/EnemApiClient.java` antes de executar.

Para executar:

1. Abra a pasta do projeto na IDE (IntelliJ IDEA, por exemplo) e aguarde o Maven baixar as dependências.
2. Abra `src/main/java/EnemApiClient.java` e execute o método `main`.

## Limitações

- A API retorna no máximo **50 questões por requisição**. Como uma prova tem cerca de 180 questões, o programa busca por páginas (`limit=50` e `offset` crescente) até acabarem.
- A API tem falhas: paginação inconsistente, algumas questões ausentes e classificação de disciplina incorreta em alguns casos. O programa filtra pelo número da questão (136 a 180 são Matemática), remove duplicatas e busca as faltantes individualmente.
- Não há filtro por assunto nem busca por palavra-chave.

## Autores

- [mSeacelo](https://github.com/mSeacelo)
- [emerson-ataide](https://github.com/emerson-ataide)

## Créditos

Dados da [API ENEM](https://enem.dev), projeto open-source de [yunger7](https://github.com/yunger7) (GPL-2.0). As questões pertencem ao Inep/MEC.
