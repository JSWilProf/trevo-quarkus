# Trevo Quarkus

## Projeto de referência

### Etapas de configuração e execução

Antes de criar os containers de infraestrutura deve ser ajustado o endereço IP
onde o "Elasticsearch" irá ser vinculado. Isto é configurado no arquivo:

 - .env - Na variável ES_HOST. Este arquivo fica na pasta principal.

Após executar o "docker-compose up"é criado os arquivos na pasta "cert"
deve ser copiado a sub-pasta "ca" contida na pasta "cert" para dentro do 
projeto "propostas" na pasta "src/main/resource".

Também após a execução dos containers de infraestrutura, é necessário criar
as contas de acesso no "Keycloak", acessado pale URL http://localhost:8180
com a conta "admin" e senha "admin" e seguindo os seguintes passos:

 - criar um Realm com o nome "trevo"
 - criar um cliente com o nome "trevo-oauth" e adicionando "*" às 
   propriedades "Valid Redirect URIs" e "Web Origins"
 - adicionar ao cliente um "mapper" do tipo "realm roles" e modificando
   sua propriedade "Token Claim Name" para "groups"
 - crie Roles "cliente" e "admin"
 - crie a conta de usuário para o cliente e administrador, garanta que
   a propriedade "Email verified" esteja "ON", ao adicionar a senha
   garanta que a propriedade "Temporary" esteja "OFF", por fim adicione 
   os Role Mappings correspondentes à cada tipo de conta.
 - Obtenha o Token de autenticação a partir da URL http://localhost:8180/auth/realms/trevo
   copie o atributo "public_key" do JSON de resposta e substitua no 
   parâmetro "mp.jwt.verify.publickey" no "application.properties" de
   cada projeto.

- Para a execução dos projetos utilize o Gradle:
  - pela linha de comando ./gradlew quarkusDev em cada pasta dos projetos
  - pela interface do IDEA IntelliJ na janela da ferramente Gradle selecione
    trevo_quarkus->NOME_DO_PROJETO->Tasks->quarkus->quarkusDev
    substitua "NOME_DO_PROJETO" por "produtos" e "propostas" para
    subir as aplicações respectivamente.

- Na execução dos testes será gerado erro na 1ª vez pois o arquivo utilizado
  para a comparação do resultado não foi criado.
  É necessário copiar o conteúdo do arquivo com a extensão "received.json" para
  o arquivo com a extensão "approved.json", seguido de nova execução.

###### atualizado em 15/05/2023