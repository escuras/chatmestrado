# API end Point com Mongodb

### Chat OK entre vários utilizadores (Guarda histórico na base dados MongoDB com API):

1: fazer registo (Ainda só na API) :http://chat-ipg-04.azurewebsites.net/api/auth/register

2: criar contactos (Ainda Só na API) : http://chat-ipg-04.azurewebsites.net/api/chat/new/"ID_do_utilizador_a_adicionar" (deve ser colocar JWT token de autenticação)
 
2: abrir Android chat e fazer login com email+password do registo

3: ver que o contacto já está criado

4: enviar mensagens encriptadas entre utilizadores




## Documentação
https://documenter.getpostman.com/view/1885494/S11Bxgv2#6bf6dd63-7868-4b61-9859-1da9983f0bec

<a href="http://mcm.ipg.pt"><img src="http://www.ipg.pt/website/imgs/logotipo_ipg.jpg" title="IPG(MCM)" alt="IPG MCM 2018/19"></a>

# Android Chat UI
<img src="https://user-images.githubusercontent.com/2634610/52165634-3509eb00-26fb-11e9-8eef-c553c78997e4.png" width="250">
<img src="https://user-images.githubusercontent.com/2634610/52754390-448a0d80-2ff2-11e9-84cf-a7761e306d85.png" width="250">

## Version
v0.2

### TODO


# Functionality

### Funcionalidades - Serviços
- [x] Troca de texto
- [ ] Suporte de voz
- [ ] Troca de ficheiros

### Requisitos de Modos de comunicação
- [ ] Comunicação por infra-estrutura
- [ ] Utilização de rede wifi ou Ethernet para comunicar
- [ ] Comunicação Standalone
- [ ] Estabelecimento de comunicação via Bluetooth com os dispositivos que são detectados

### Requisitos de implementação
- [x] Gestão de utilizadores em servidor central, com sinalização de utilizadores online/offline
- [x] Armazenamento de mensagem em servidor para possibilidade concorrente em vários dispositivos
- [ ] Comunicação em claro ou encriptada por selecção explicita da encriptação por parte dos utilizadores

### Requisitos de implementação - Segurança
- [ ] Comunicação em claro com verificação da integridade da comunicação
- [ ] Escolha de um dos algoritmos MD5 ou SHA Hash Algorithms: SHA-1, HAVAL, MD2, MD5, SHA-256, SHA-384, SHA-512
- [x] Comunicação encriptada de mensagens
- [ ] Escolha de um dos protocolos DES, 3DES ou AES



### Contributing

