# GoCommunity
Projeto de TQS - LEI 2017/2018

Disponível online [aqui](http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT), o Jenkins [aqui](http://192.168.160.226:8090/job/GoCommunity/) e o SonarQube [aqui](http://192.168.160.226:9000/dashboard/index/com.mycompany:GoCommunity).

## Descrição de práticas usadas

O projeto utiliza arquitetura Java EE e um container Glassfish. O continuous deployment é feito por uma instância Jenkins.

A unidade de persistência gere [dois tipos de entidades](https://github.com/chffUA/GoCommunity/tree/master/src/main/java/db), **Client** e **Project**. O terceiro tipo (TestEntityClass) existe apenas para realizar testes sobre a base de dados, não sendo diretamente relevante para o funcionamente do projeto.

A lógica de interação com a persistence unit, utilizador e disponibilização do API existem em [3 ficheiros distintos](https://github.com/chffUA/GoCommunity/tree/master/src/main/java/com/mycompany/gocommunity), cada um com o seu propósito.

A comunicação com a base de dados é efetuada através de métodos da classe **DatabaseHandler**, a única que interage de forma direta com instâncias do Entity Manager.

A lógica do website encontra-se no bean **ComBean**, responsável pela procura e apresentação de dados, interação com o utilizador através de formulários/botões e comunicação com a base de dados através de uma instância de **DatabaseHandler**.

Os métodos do REST API estão na classe **ApiBean**, e são possíveis de aceder e testar através de URLs com a seguinte estrutura:

```

http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT/faces/api/data/<@Path definido para o método>
```

Como exemplo:

```
http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT/faces/api/data/user/1
http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT/faces/api/data/project/Exemplo
http://deti-tqs-05.ua.pt:8181/GoCommunity-1.0-SNAPSHOT/faces/api/data/popular
```

As [páginas web](https://github.com/chffUA/GoCommunity/tree/master/src/main/webapp) são do formato .xhtml, suportando JSF, e interagem com o **ComBean** para apresentar e processar informação.

## Guia de estilo

O código é indentado com 4 espaços.

Para garantir simplicidade e organização, interação com a persistence unit deve ser feita através do **DatabaseHandler**.

Os nomes dos métodos e variáveis devem ser descritivos.

Os "code smells" (como descritos pelo SonarQube) devem ser minimizados, exceto quando a sua correção gravemente piore a facilidade de leitura do código.

Em condicionais, deve-se usar chavetas mesmo para possíveis "one-liners", de modo a manter consistência na escrita. Por exemplo:

```java
if (c==null) {
    return Response.status(404).entity(notFound).build();
}
```

Em vez de:

```java
if (c==null) return Response.status(404).entity(notFound).build();
```

Se possível, pedaços de código repetitivos devem ser usados como métodos privados, para diminuir a quantidade de código em métodos essenciais. Por exemplo:

```java
private String moneyFormat(double original) {
    return String.format("%.2f", original);
}   
```
