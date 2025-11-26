package br.eng.rodrigogml.rfw.kernel.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.eng.rodrigogml.rfw.kernel.exceptions.RFWException;
import br.eng.rodrigogml.rfw.kernel.exceptions.RFWValidationException;

/**
 * Testes unitários da {@link RUMail}.<br>
 * Cada método público é testado com cenários válidos e inválidos dentro de um único método de teste.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RUMailTest {

  /**
   * Testa {@link RUMail#validateMailAddress(String)} com endereços válidos e inválidos.
   */
  @Test
  public void t00_validateMailAddress() throws RFWException {
    List<String> validAddresses = Arrays.asList(
        "simple@example.com",
        "user.name+tag@sub.domain.com",
        "user_name@domain.co",
        "u-ser@dominio.com.br",
        "123@numbers.net");

    for (String address : validAddresses) {
      RUMail.validateMailAddress(address);
    }

    assertThrows(RFWValidationException.class, () -> RUMail.validateMailAddress(null));
    assertThrows(RFWValidationException.class, () -> RUMail.validateMailAddress(""));
    assertThrows(RFWValidationException.class, () -> RUMail.validateMailAddress("sem-arroba.com"));
    assertThrows(RFWValidationException.class, () -> RUMail.validateMailAddress("nome@dominio"));
    assertThrows(RFWValidationException.class, () -> RUMail.validateMailAddress("nome@dominio..com"));
  }

  /**
   * Testa {@link RUMail#parseMailAddresses(String)} para garantir extração sequencial de endereços.
   */
  @Test
  public void t01_parseMailAddresses() {
    List<String> resultado = RUMail.parseMailAddresses("Contato: teste@dominio.com e copia para outro.email@sub.dominio.com.br.");
    assertEquals(Arrays.asList("teste@dominio.com", "outro.email@sub.dominio.com.br"), resultado);

    resultado = RUMail.parseMailAddresses("Texto sem emails válidos mas com semelhante test@dominio");
    assertTrue(resultado.isEmpty());

    resultado = RUMail.parseMailAddresses("Mistura invalida@dominio..com e valida@ok.com");
    assertEquals(Arrays.asList("valida@ok.com"), resultado);

    assertThrows(NullPointerException.class, () -> RUMail.parseMailAddresses(null));
  }

  /**
   * Testa {@link RUMail#parseMailAddress(String)} retornando apenas o primeiro endereço localizado.
   */
  @Test
  public void t02_parseMailAddress() {
    assertEquals("primeiro@teste.com", RUMail.parseMailAddress("Envie para primeiro@teste.com ou segundo@teste.com"));
    assertEquals(null, RUMail.parseMailAddress("Não há email aqui"));
    assertThrows(NullPointerException.class, () -> RUMail.parseMailAddress(null));
  }

  /**
   * Testa {@link RUMail#isMailAddress(String)} com variações válidas e inválidas.
   */
  @Test
  public void t03_isMailAddress() {
    assertTrue(RUMail.isMailAddress("valido@dominio.com"));
    assertTrue(RUMail.isMailAddress("nome.sobrenome+tag@sub.dominio.org"));
    assertFalse(RUMail.isMailAddress("invalido@dominio"));
    assertFalse(RUMail.isMailAddress("invalido@dominio..com"));
    assertFalse(RUMail.isMailAddress(null));
  }

  /**
   * Testa {@link RUMail#parseMailName(String)} cobrindo formatos com e sem nome, bem como entradas inválidas.
   */
  @Test
  public void t04_parseMailName() {
    assertEquals("Rodrigo Leitão", RUMail.parseMailName("Rodrigo Leitão <rodrigo@teste.com>"));
    assertEquals("Nome com \"aspas\"", RUMail.parseMailName("\"Nome com \"aspas\"\" <pessoa@teste.com>"));
    assertEquals("sem nome", RUMail.parseMailName("sem nome <usuario@teste.com>"));
    assertNull(RUMail.parseMailName("<usuario@teste.com>"));
    assertNull(RUMail.parseMailName("usuario@teste.com"));
    assertNull(RUMail.parseMailName(null));
  }

  /**
   * Testa {@link RUMail#loadMessageTemplate(String, HashMap)} garantindo substituição de variáveis.
   */
  @Test
  public void t05_loadMessageTemplate() throws RFWException {
    HashMap<String, String> valores = new HashMap<String, String>();
    valores.put("name", "Cliente");
    valores.put("orderId", "12345");
    valores.put("link", "http://exemplo.com/pedido/12345");

    String conteudo = RUMail.loadMessageTemplate(
        "br/eng/rodrigogml/rfw/kernel/utils/templates/mail-template.html",
        valores);

    assertTrue(conteudo.contains("Olá Cliente"));
    assertTrue(conteudo.contains("pedido número 12345"));
    assertTrue(conteudo.contains("http://exemplo.com/pedido/12345"));
    assertTrue(conteudo.contains("Assinatura fixa"));

    String conteudoSemMapa = RUMail.loadMessageTemplate(
        "br/eng/rodrigogml/rfw/kernel/utils/templates/mail-template.html",
        null);
    assertEquals(conteudoSemMapa, RUMail.loadMessageTemplate(
        "br/eng/rodrigogml/rfw/kernel/utils/templates/mail-template.html",
        new HashMap<String, String>()));
  }

  /**
   * Testa {@link RUMail#sendMail(br.eng.rodrigogml.rfw.kernel.utils.RUMail.MailProtocol, String, String, String, String, br.eng.rodrigogml.rfw.kernel.utils.RUMail.Mail)}
   * apenas para validar cenários de montagem e validação de dados antes do envio real.
   */
  @Test
  public void t06_sendMail() {
    assertThrows(RFWValidationException.class, () -> RUMail.sendMail(RUMail.MailProtocol.TLS, "localhost", "25", "user", "pass", null));

    TestMail mailInvalido = new TestMail("remetente-invalido", Arrays.asList("destino@teste.com"), null, null, "Assunto", "<p>Teste</p>");
    assertThrows(RFWValidationException.class, () -> RUMail.sendMail(RUMail.MailProtocol.TLS, "localhost", "25", "user", "pass", mailInvalido));

    TestMail mailDestinoInvalido = new TestMail("remetente@teste.com", Arrays.asList("destino-invalido"), new ArrayList<String>(), new ArrayList<String>(), "Assunto", "<p>Teste</p>");
    assertThrows(RFWValidationException.class, () -> RUMail.sendMail(RUMail.MailProtocol.SSL, "localhost", "465", "user", "pass", mailDestinoInvalido));
  }

  /**
   * Implementação simples de {@link RUMail.Mail} para uso nos testes.
   */
  private static class TestMail implements RUMail.Mail {
    private final String from;
    private final List<String> to;
    private final List<String> cc;
    private final List<String> bcc;
    private final String subject;
    private final String body;

    TestMail(String from, List<String> to, List<String> cc, List<String> bcc, String subject, String body) {
      this.from = from;
      this.to = to;
      this.cc = cc;
      this.bcc = bcc;
      this.subject = subject;
      this.body = body;
    }

    @Override
    public String getFrom() {
      return from;
    }

    @Override
    public List<String> getTo() {
      return to;
    }

    @Override
    public List<String> getCc() {
      return cc;
    }

    @Override
    public List<String> getBcc() {
      return bcc;
    }

    @Override
    public String getSubject() {
      return subject;
    }

    @Override
    public String getBody() {
      return body;
    }
  }
}
