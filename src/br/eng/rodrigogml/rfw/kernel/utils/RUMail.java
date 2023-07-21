package br.eng.rodrigogml.rfw.kernel.utils;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilit�ria para conter m�todos de aux�lio do servi�o de e-mail.<BR>
 *
 * @author Rodrigo Leit�o
 * @since 4.2.0 (30/10/2011)
 */
public class RUMail {

  /**
   * Patter dos caracteres aceitos em um e-mail. Tanto na �rea de usu�rio quando de dom�nio.<br>
   * Esse pattern est� incompleto pois n�o aceita os "escape caracteres", por exemplo segundo as especifica��es um email pode ter uma @ como parte do nome do usu�rio deste que seja "escaped" com o caractere \@. Esses caracteres incomuns n�o est�o sendo tratados neste pattern.<Br>
   * <b>Aten��o:</n> N�o incluir neste patter os caracteres de sintaxe. Por exemplo, o e-mail pode ter o nome do usu�rio cercado por aspas, e o dom�nio em forma de ip se cercado por colchetes. Ex: "rodiro leitao"@[10.0.0.1]. Esses caracteres de "entorno" n�o devem ser considerados neste patter, mesmo que a " possa fazer parte do nome do usu�rio como um escaped caracter \", n�o estamos falando dos
   * escapade caracteres, apenas dos caracteres de entorno. Tamb�m n�o colocar nenhum tipo de defini��o de quantidade de repeti��o, deixar apenas a lista de caracteres v�lidos.
   */
  public static final String mailAcceptedChar = "[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~\\.]";

  /**
   * Construtor privado para classe est�tica.
   */
  private RUMail() {
  }

  /**
   * Valida se a string passada � um e-mail. Aceita apenas o e-mail e n�o uma string contendo um e-mail.
   *
   * @param mail E-mail a ser validado.
   * @throws RFWException lan�a a exce��o com a mensagem do porque o e-mail n�o � v�lido.
   */
  public static void validateMailAddress(String mail) throws RFWException {
    if (mail == null) throw new RFWValidationException("RFW_ERR_200489");

    // 1-Quebramos o valor pela @ para pegar o usu�rio e o domain do e-mail.
    String[] parts = mail.toString().split("@");
    if (parts.length != 2) {
      throw new RFWValidationException("RFW_ERR_200317", new String[] { mail });
    }
    // Pattern geral usado apenas para validar se os caractes usados no e-mail s�o validos
    String generalpatter = mailAcceptedChar + "+";
    // 2- Validamos a parte do usu�rio
    if (!parts[0].matches(generalpatter)) {
      throw new RFWValidationException("RFW_ERR_200318", new String[] { mail });
    }
    if (parts[0].indexOf("..") >= 0) {
      throw new RFWValidationException("RFW_ERR_200319", new String[] { mail });
    }
    if (parts[0].charAt(0) == '.' || parts[0].charAt(parts[0].length() - 1) == '.') {
      throw new RFWValidationException("RFW_ERR_200320", new String[] { mail });
    }
    if (parts[0].length() > 64) {
      throw new RFWValidationException("RFW_ERR_200322", new String[] { mail });
    }

    // 3-Validamos o domain
    if (!parts[1].matches(generalpatter)) {
      throw new RFWValidationException("RFW_ERR_200318", new String[] { mail });
    }
    if (parts[1].indexOf("..") >= 0) {
      throw new RFWValidationException("RFW_ERR_200319", new String[] { mail });
    }
    if (parts[1].indexOf("--") >= 0) {
      throw new RFWValidationException("RFW_ERR_200325", new String[] { mail });
    }
    if (parts[1].charAt(0) == '.' || parts[1].charAt(parts[1].length() - 1) == '.') {
      throw new RFWValidationException("RFW_ERR_200321", new String[] { mail });
    }
    if (parts[1].length() > 253) {
      throw new RFWValidationException("RFW_ERR_200323", new String[] { mail });
    }
    // Validamos se todos os dom�nios do e-mail t�m menos de 63 caracteres.
    String[] domainparts = parts[1].split("\\.");
    for (int i = 0; i < domainparts.length; i++) {
      if (domainparts[i].length() > 63) {
        throw new RFWValidationException("RFW_ERR_200324", new String[] { mail });
      }
      if (domainparts[i].charAt(0) == '-' || domainparts[i].charAt(domainparts[i].length() - 1) == '-') {
        throw new RFWValidationException("RFW_ERR_200326", new String[] { mail });
      }
    }
    // Valida se a parte mais a direia � s� alfab�tica
    if (!domainparts[domainparts.length - 1].matches("[A-Za-z]+")) {
      throw new RFWValidationException("RFW_ERR_200327", new String[] { mail });
    }
  }
}
