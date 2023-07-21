package br.eng.rodrigogml.rfw.kernel.utils;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Description: Classe utilitária para conter métodos de auxílio do serviço de e-mail.<BR>
 *
 * @author Rodrigo Leitão
 * @since 4.2.0 (30/10/2011)
 */
public class RUMail {

  /**
   * Patter dos caracteres aceitos em um e-mail. Tanto na área de usuário quando de domínio.<br>
   * Esse pattern está incompleto pois não aceita os "escape caracteres", por exemplo segundo as especificações um email pode ter uma @ como parte do nome do usuário deste que seja "escaped" com o caractere \@. Esses caracteres incomuns não estão sendo tratados neste pattern.<Br>
   * <b>Atenção:</n> Não incluir neste patter os caracteres de sintaxe. Por exemplo, o e-mail pode ter o nome do usuário cercado por aspas, e o domínio em forma de ip se cercado por colchetes. Ex: "rodiro leitao"@[10.0.0.1]. Esses caracteres de "entorno" não devem ser considerados neste patter, mesmo que a " possa fazer parte do nome do usuário como um escaped caracter \", não estamos falando dos
   * escapade caracteres, apenas dos caracteres de entorno. Também não colocar nenhum tipo de definição de quantidade de repetição, deixar apenas a lista de caracteres válidos.
   */
  public static final String mailAcceptedChar = "[a-zA-Z0-9\\!\\#\\$\\%\\&\\'\\*\\+\\-\\/\\=\\?\\^\\_\\`\\{\\|\\}\\~\\.]";

  /**
   * Construtor privado para classe estática.
   */
  private RUMail() {
  }

  /**
   * Valida se a string passada é um e-mail. Aceita apenas o e-mail e não uma string contendo um e-mail.
   *
   * @param mail E-mail a ser validado.
   * @throws RFWException lança a exceção com a mensagem do porque o e-mail não é válido.
   */
  public static void validateMailAddress(String mail) throws RFWException {
    if (mail == null) throw new RFWValidationException("RFW_ERR_200489");

    // 1-Quebramos o valor pela @ para pegar o usuário e o domain do e-mail.
    String[] parts = mail.toString().split("@");
    if (parts.length != 2) {
      throw new RFWValidationException("RFW_ERR_200317", new String[] { mail });
    }
    // Pattern geral usado apenas para validar se os caractes usados no e-mail são validos
    String generalpatter = mailAcceptedChar + "+";
    // 2- Validamos a parte do usuário
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
    // Validamos se todos os domínios do e-mail têm menos de 63 caracteres.
    String[] domainparts = parts[1].split("\\.");
    for (int i = 0; i < domainparts.length; i++) {
      if (domainparts[i].length() > 63) {
        throw new RFWValidationException("RFW_ERR_200324", new String[] { mail });
      }
      if (domainparts[i].charAt(0) == '-' || domainparts[i].charAt(domainparts[i].length() - 1) == '-') {
        throw new RFWValidationException("RFW_ERR_200326", new String[] { mail });
      }
    }
    // Valida se a parte mais a direia é só alfabética
    if (!domainparts[domainparts.length - 1].matches("[A-Za-z]+")) {
      throw new RFWValidationException("RFW_ERR_200327", new String[] { mail });
    }
  }
}
