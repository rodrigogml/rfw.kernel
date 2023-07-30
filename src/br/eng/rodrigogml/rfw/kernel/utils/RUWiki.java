package br.eng.rodrigogml.rfw.kernel.utils;

/**
 * Description: Classe utilit�ria para os m�todos auxiliares do Wiki do RFW.<br>
 * O objetivo deste Wiki � ter um padr�o parecido com o do wikipedia que permita que relat�rios sejam escritos em um formato comum do sistema, e que ele possa ser traduzido para outros formatos, como os padr�es de impress�o direto em impressora, HTML, XML, PDF, RichText, etc.
 *
 * @author Rodrigo Leit�o
 * @since 10.0 (30 de nov. de 2021)
 */
public class RUWiki {

  /**
   * Construtor privado, classe utilitaria totalmente est�tica.
   */
  private RUWiki() {
  }

  /**
   * Recupera conte�do em HTML que pode ser exibido para o usu�rio para ensina-lo a utilizar o RFWWiki.
   *
   * @return Texto em formato HTML com tutorial para ensinar o RFWWiki.
   */
  public static String getFWWikiManual_HTML() {
    StringBuilder buff = new StringBuilder();
    buff.append("<b>Formata��o de Texto:</b>");
    buff.append("<ul>");
    buff.append("<b>'''</b> Inicia/termina negrito.<br>");
    buff.append("<b>''</b> Inicia/termina it�lico.<br>");
    buff.append("</ul>");
    buff.append("<b>Lista com Marcadores e Lista Numerada:</b>");
    buff.append("<ul>");
    buff.append("<b>+#</b> Colocado no in�cio da linha, inicia uma lista numerada.<br>");
    buff.append("<b>-#</b> No in�cio da linha, termina uma lista numerada.<br>");
    buff.append("<b>+*</b> No in�cio da linha, inicia uma lista de itens (marcadores).<br>");
    buff.append("<b>-*</b> No in�cio da linha, termina uma lista de itens (marcadores).<br>");
    buff.append("<b>**</b> No in�cio da linha, escreve um item de lista.<br>");
    buff.append("</ul>");
    return buff.toString();
  }

}
