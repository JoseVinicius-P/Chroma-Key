# Chroma-Key

Este é um aplicativo Android Java que foi desenvolvido no ano de 2018 para teste de conceito de cores RGB. Este app faz a verificação de todos os pixels de uma imagem e verifica se ele é um pixel verde, retirando-o assim da imagem e criando um fundo transparente ao redor do(s) objeto(s) alvo. Um Chroma Key. 

Abaixo vemos a ultilização do app com um tecido verde pouco regular o que prova, em parte, a eficiência da regra utilizada para descrever um pixel verde. 

<p align="center">
  <img width="200" src="app/src/to_readmi/app.gif">
</p>

Regra: No RGB do pixel, o verde (G) deve representar mais de 40% da soma das 3 cores (RGB).

Exemplo: 
Um pixel com os valores R=46, G=107, B=35. A soma dos 3 valores é igual a 188 e 40% de 188 é aproximadamente 75 o que define este RGB como verde, já que 107 é maior que 75. Este pixel será apagado.Esta regra pode ser vista em detalhes no código do app.


