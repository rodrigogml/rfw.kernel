package br.eng.rodrigogml.rfw.kernel.file.vo;

import java.time.LocalDateTime;

import br.eng.rodrigogml.rfw.kernel.dataformatters.LocaleConverter;
import br.eng.rodrigogml.rfw.kernel.file.vo.FileVO.FileCompression;
import br.eng.rodrigogml.rfw.kernel.file.vo.FileVO.FilePersistenceType;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaDateField.DateResolution;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaEnumField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaLongField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaRelationshipField.RelationshipTypes;
import br.eng.rodrigogml.rfw.kernel.rfwmeta.RFWMetaStringField;
import br.eng.rodrigogml.rfw.kernel.utils.RUGenerators;
import br.eng.rodrigogml.rfw.kernel.vo.RFWVO;

/**
 * Description: Classe que representa um arquivo utilizado pelo sistema.<br>
 * O Core do bis prov� um sistema de armazemamento de arquivos diretamente no banco de dados para simplificar a vida dos desenvolvedores de m�dulos.<br>
 *
 * @author Rodrigo Leit�o
 * @since 3.1.0 (DEZ / 2009)
 */
@SuppressWarnings("unused")
public class FileVO extends RFWVO {

  private static final long serialVersionUID = 938608718161221863L;

  /**
   * Define o tipo de persist�ncia do arquivo.
   */
  public static enum FilePersistenceType {
    /**
     * Indica que o conte�do do arquivo � persistido no Banco de Dados.
     */
    DB,
    /**
     * Indica que o conte�do do arquivo � persistido e versionado no AWS S3.
     */
    S3
  }

  /**
   * Modelos de compress�o do conte�do suportados
   */
  public static enum FileCompression {

    /**
     * O conte�do do arquivo n�o est� comprimido.<br>
     * Este modo � interessante quando o arquivo � muito utilizado, assim tiramos o overhead de processamento de descoprimi-lo o tempo todo.
     */
    NONE,
    /**
     * Metodo de m�ximo de compress�o do BIS, utiliza a compress�o no m�ximo para diminuir o espa�o utilizado em disco, mesmo que utilize mais processamento.<br>
     * Este modo � �til quando os arquivos ser�o guardados por longo per�odo de tempo e ser�o acessado poucas vezes.<Br>
     * <br>
     * Obs: para ajudar a recuperar o arquivo original, indepentende do moto de compress�o utilizado, verifique o m�todo {@link B10File#processFileVOTempFile(FileVO)}.
     */
    MAXIMUM_COMPRESSION,
  }

  /**
   * Nome do arquivo salvo. <br>
   * Mesmo que no modo {@link FilePersistenceType#S3} o arquivo seja comprimido (enviado como .zip para o S3) aqui a exten��o e nome do arquivo continuaram intactos. No momento de recuperar o arquivo do S3 o valor de {@link FileCompression} � avaliado para determinar a exten��o correta a ser utilizada
   */
  @RFWMetaStringField(caption = "Arquivo", maxlength = 255, required = true, pattern = "[^\\\\/<>\\?:\\|\\*\\\"]+\\.[^\\\\/<>\\?:\\|\\*\\\"]{1,4}")
  private String name = null;

  /**
   * Tag com o nome do m�dulo dono do arquivo. Esta tag ajuda em caso de problemas a identificar o arquivo e encontra-lo.<br>
   * Caso seja salvo no S3, esse valor � colocado como o nome de um folder.<Br>
   * <br>
   * Obs1: Os TagIDs do sistema ficam registrados como constantes em {@link BISSystem} com o prefixo "FILETAG_". Exemplo: {@link BISSystem#FILETAG_NFE_XML}.<br>
   * Obs2: Considere utilizar o m�todo {@link B10File#createFileVO(FilePersistenceType, byte[], String, String, String, FileCompression)} ou similar para gerar o FileVO.
   */
  @RFWMetaStringField(caption = "TagID", maxlength = 15, pattern = "\\w+", required = true)
  private String tagID = null;

  /**
   * Para arquivos persistidos no S3 n�o utilizaremos o nome do arquivo pois precisamos garantir um identificador �nico para que os arquivos n�o se sobreponham. Assim geramos um UUID que identifica o arquivo. No S3 esse UUID � utilizado como nome do arquivo.
   */
  @RFWMetaStringField(caption = "UUID do Arquivo", maxlength = 36, required = false, pattern = RUGenerators.UUID_REGEXP)
  private String fileUUID = null;

  /**
   * Indica se o conte�do do arquivo est� comprimido ou n�o.<Br>
   * Quando comprimido � preciso descoprimir o conte�do do arquivo antes de utiliza-lo.
   */
  @RFWMetaEnumField(caption = "Compress�o", required = true)
  private FileCompression compression = null;

  /**
   * Data de cria��o do arquivo.
   */
  @RFWMetaDateField(caption = "Data Cria��o", resolution = DateResolution.SECOND, required = true)
  private LocalDateTime dateCreation = null;

  /**
   * Data de modifica��o do arquivo.
   */
  @RFWMetaDateField(caption = "Data Modifica��o", resolution = DateResolution.SECOND, required = true)
  private LocalDateTime dateModification = null;

  /**
   * Tamanho do arquivo em bytes (para formatar o valor cheque a classe {@link LocaleConverter})
   */
  @RFWMetaLongField(caption = "Tamanho", minvalue = 0, required = true)
  private Long size = null;

  /**
   * Encoding do conte�do do arquivo. Geralmente utilizado para arquivos de Texto.
   */
  @RFWMetaStringField(caption = "Encoding", maxlength = 15, required = false)
  private String encoding = null;

  /**
   * Local onde o arquivo � persistido.
   */
  @RFWMetaEnumField(caption = "Tipo de Persist�ncia", required = true)
  private FilePersistenceType persistenceType = null;

  /**
   * Conte�do do arquivo quando persistido no banco de dados {@link FilePersistenceType} = {@link FilePersistenceType#DB}.<br>
   */
  @RFWMetaRelationshipField(caption = "Conte�do", relationship = RelationshipTypes.COMPOSITION, required = false, columnMapped = "idk_file")
  private FileContentVO fileContentVO = null;

  /**
   * Chave de vers�o do arquivo quando no S3. Obrigat�rio quando {@link FilePersistenceType} = {@link FilePersistenceType#S3}.<br>
   * <b>ATEN��O:</B> Quando o FileVO tiver o conte�do do arquivo alterado, e tivermos de postar um novo arquivo o versionID deve ser definido para nulo. o CRUD realizar� um novo post no S3 quando o versionID for nulo. Caso contr�rio assume que o arquivo continua sendo a vers�o passada.
   */
  @RFWMetaStringField(caption = "ID Vers�o S3", maxlength = 50, required = false)
  private String versionID = null;

  /**
   * Caminho para o arquivo tempor�rio onde encontramos o arquivo quando {@link FilePersistenceType} = {@link FilePersistenceType#S3}.<br>
   * Este atributo n�o � persistido no banco de dados pois � um caminho tempor�rio.<br>
   * Para persistir o FileVO no S3 este atributo � obrigat�rio, pois o arquivo que ser� postado no S3 ser� lido desse caminho.<br>
   * Quando o FileVO for recuperado do banco de dados este atributo vir� nulo. Ap�s chamar o m�todo de recupera��o do arquivo no S3 o caminho para o arquivo baixado deve ser salvo aqui.<br>
   * Note que o Crud n�o verifica o conte�do do arquivo para determinar se houve ou n�o altera��o. Para que o CRUD saiba que o conte�do do arquivo mudou o conte�do de {@link #versionID} deve ser definido como null.<br>
   * O conte�do do arquivo tempPath deve sempre acompanhar o valor definido em {@link #compression}. Ou seja, se ouver compress�o o conte�do deste arquivo j� deve estar comprimido conforme a defini��o, se n�o houver, dever� ser o arquivo "pleno", e assim por diante. Assim, <b>Cuidado ao l�r o conte�do deste arquivo</b> pois ele pode estar comprimido e precisa de tratamento antes.
   */
  // SEM @BISMETA PQ ESSE ATRIBUTO N�O � PERSISTIDO
  private String tempPath = null;

  /**
   * # nome do arquivo salvo. <br>
   * Mesmo que no modo {@link FilePersistenceType#S3} o arquivo seja comprimido (enviado como .zip para o S3) aqui a exten��o e nome do arquivo continuaram intactos. No momento de recuperar o arquivo do S3 o valor de {@link FileCompression} � avaliado para determinar a exten��o correta a ser utilizada.
   *
   * @return the nome do arquivo salvo
   */
  public String getName() {
    return name;
  }

  /**
   * # nome do arquivo salvo. <br>
   * Mesmo que no modo {@link FilePersistenceType#S3} o arquivo seja comprimido (enviado como .zip para o S3) aqui a exten��o e nome do arquivo continuaram intactos. No momento de recuperar o arquivo do S3 o valor de {@link FileCompression} � avaliado para determinar a exten��o correta a ser utilizada.
   *
   * @param name the new nome do arquivo salvo
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * # data de cria��o do arquivo.
   *
   * @return the data de cria��o do arquivo
   */
  public LocalDateTime getDateCreation() {
    return dateCreation;
  }

  /**
   * # data de cria��o do arquivo.
   *
   * @param dateCreation the new data de cria��o do arquivo
   */
  public void setDateCreation(LocalDateTime dateCreation) {
    this.dateCreation = dateCreation;
  }

  /**
   * # data de modifica��o do arquivo.
   *
   * @return the data de modifica��o do arquivo
   */
  public LocalDateTime getDateModification() {
    return dateModification;
  }

  /**
   * # data de modifica��o do arquivo.
   *
   * @param dateModification the new data de modifica��o do arquivo
   */
  public void setDateModification(LocalDateTime dateModification) {
    this.dateModification = dateModification;
  }

  /**
   * # tamanho do arquivo em bytes (para formatar o valor cheque a classe {@link LocaleConverter}).
   *
   * @return the tamanho do arquivo em bytes (para formatar o valor cheque a classe {@link LocaleConverter})
   */
  public Long getSize() {
    return size;
  }

  /**
   * # tamanho do arquivo em bytes (para formatar o valor cheque a classe {@link LocaleConverter}).
   *
   * @param size the new tamanho do arquivo em bytes (para formatar o valor cheque a classe {@link LocaleConverter})
   */
  public void setSize(Long size) {
    this.size = size;
  }

  /**
   * # conte�do do arquivo quando persistido no banco de dados {@link FilePersistenceType} = {@link FilePersistenceType#DB}.<br>
   * .
   *
   * @return the conte�do do arquivo quando persistido no banco de dados {@link FilePersistenceType} = {@link FilePersistenceType#DB}
   */
  public FileContentVO getFileContentVO() {
    return fileContentVO;
  }

  /**
   * # conte�do do arquivo quando persistido no banco de dados {@link FilePersistenceType} = {@link FilePersistenceType#DB}.<br>
   * .
   *
   * @param fileContentVO the new conte�do do arquivo quando persistido no banco de dados {@link FilePersistenceType} = {@link FilePersistenceType#DB}
   */
  public void setFileContentVO(FileContentVO fileContentVO) {
    this.fileContentVO = fileContentVO;
  }

  /**
   * # encoding do conte�do do arquivo. Geralmente utilizado para arquivos de Texto.
   *
   * @return the encoding do conte�do do arquivo
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * # encoding do conte�do do arquivo. Geralmente utilizado para arquivos de Texto.
   *
   * @param encoding the new encoding do conte�do do arquivo
   */
  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }

  /**
   * # local onde o arquivo � persistido.
   *
   * @return the local onde o arquivo � persistido
   */
  public FilePersistenceType getPersistenceType() {
    return persistenceType;
  }

  /**
   * # local onde o arquivo � persistido.
   *
   * @param persistenceType the new local onde o arquivo � persistido
   */
  public void setPersistenceType(FilePersistenceType persistenceType) {
    this.persistenceType = persistenceType;
  }

  /**
   * # chave de vers�o do arquivo quando no S3. Obrigat�rio quando {@link FilePersistenceType} = {@link FilePersistenceType#S3}.<br>
   * <b>ATEN��O:</B> Quando o FileVO tiver o conte�do do arquivo alterado, e tivermos de postar um novo arquivo o versionID deve ser definido para nulo. o CRUD realizar� um novo post no S3 quando o versionID for nulo. Caso contr�rio assume que o arquivo continua sendo a vers�o passada.
   *
   * @return the chave de vers�o do arquivo quando no S3
   */
  public String getVersionID() {
    return versionID;
  }

  /**
   * # chave de vers�o do arquivo quando no S3. Obrigat�rio quando {@link FilePersistenceType} = {@link FilePersistenceType#S3}.<br>
   * <b>ATEN��O:</B> Quando o FileVO tiver o conte�do do arquivo alterado, e tivermos de postar um novo arquivo o versionID deve ser definido para nulo. o CRUD realizar� um novo post no S3 quando o versionID for nulo. Caso contr�rio assume que o arquivo continua sendo a vers�o passada.
   *
   * @param versionID the new chave de vers�o do arquivo quando no S3
   */
  public void setVersionID(String versionID) {
    this.versionID = versionID;
  }

  /**
   * # tag com o nome do m�dulo dono do arquivo. Esta tag ajuda em caso de problemas a identificar o arquivo e encontra-lo.<br>
   * Caso seja salvo no S3, esse valor � colocado como o nome de um folder.
   *
   * @return the tag com o nome do m�dulo dono do arquivo
   */
  public String getTagID() {
    return tagID;
  }

  /**
   * # tag com o nome do m�dulo dono do arquivo. Esta tag ajuda em caso de problemas a identificar o arquivo e encontra-lo.<br>
   * Caso seja salvo no S3, esse valor � colocado como o nome de um folder.
   *
   * @param tagID the new tag com o nome do m�dulo dono do arquivo
   */
  public void setTagID(String tagID) {
    this.tagID = tagID;
  }

  /**
   * # caminho para o arquivo tempor�rio onde encontramos o arquivo quando {@link FilePersistenceType} = {@link FilePersistenceType#S3}.<br>
   * Este atributo n�o � persistido no banco de dados pois � um caminho tempor�rio.<br>
   * Para persistir o FileVO no S3 este atributo � obrigat�rio, pois o arquivo que ser� postado no S3 ser� lido desse caminho.<br>
   * Quando o FileVO for recuperado do banco de dados este atributo vir� nulo. Ap�s chamar o m�todo de recupera��o do arquivo no S3 o caminho para o arquivo baixado deve ser salvo aqui.<br>
   * Note que o Crud n�o verifica o conte�do do arquivo para determinar se houve ou n�o altera��o. Para que o CRUD saiba que o conte�do do arquivo mudou o conte�do de {@link #versionID} deve ser definido como null.<br>
   * O conte�do do arquivo tempPath deve sempre acompanhar o valor definido em {@link #compression}. Ou seja, se ouver compress�o o conte�do deste arquivo j� deve estar comprimido conforme a defini��o, se n�o houver, dever� ser o arquivo "pleno", e assim por diante. Assim, <b>Cuidado ao l�r o conte�do deste arquivo</b> pois ele pode estar comprimido e precisa de tratamento antes.
   *
   * @return the caminho para o arquivo tempor�rio onde encontramos o arquivo quando {@link FilePersistenceType} = {@link FilePersistenceType#S3}
   */
  public String getTempPath() {
    return tempPath;
  }

  /**
   * # caminho para o arquivo tempor�rio onde encontramos o arquivo quando {@link FilePersistenceType} = {@link FilePersistenceType#S3}.<br>
   * Este atributo n�o � persistido no banco de dados pois � um caminho tempor�rio.<br>
   * Para persistir o FileVO no S3 este atributo � obrigat�rio, pois o arquivo que ser� postado no S3 ser� lido desse caminho.<br>
   * Quando o FileVO for recuperado do banco de dados este atributo vir� nulo. Ap�s chamar o m�todo de recupera��o do arquivo no S3 o caminho para o arquivo baixado deve ser salvo aqui.<br>
   * Note que o Crud n�o verifica o conte�do do arquivo para determinar se houve ou n�o altera��o. Para que o CRUD saiba que o conte�do do arquivo mudou o conte�do de {@link #versionID} deve ser definido como null.<br>
   * O conte�do do arquivo tempPath deve sempre acompanhar o valor definido em {@link #compression}. Ou seja, se ouver compress�o o conte�do deste arquivo j� deve estar comprimido conforme a defini��o, se n�o houver, dever� ser o arquivo "pleno", e assim por diante. Assim, <b>Cuidado ao l�r o conte�do deste arquivo</b> pois ele pode estar comprimido e precisa de tratamento antes.
   *
   * @param tempPath the new caminho para o arquivo tempor�rio onde encontramos o arquivo quando {@link FilePersistenceType} = {@link FilePersistenceType#S3}
   */
  public void setTempPath(String tempPath) {
    this.tempPath = tempPath;
  }

  /**
   * # para arquivos persistidos no S3 n�o utilizaremos o nome do arquivo pois precisamos garantir um identificador �nico para que os arquivos n�o se sobreponham. Assim geramos um UUID que identifica o arquivo. No S3 esse UUID � utilizado como nome do arquivo.
   *
   * @return the para arquivos persistidos no S3 n�o utilizaremos o nome do arquivo pois precisamos garantir um identificador �nico para que os arquivos n�o se sobreponham
   */
  public String getFileUUID() {
    return fileUUID;
  }

  /**
   * # para arquivos persistidos no S3 n�o utilizaremos o nome do arquivo pois precisamos garantir um identificador �nico para que os arquivos n�o se sobreponham. Assim geramos um UUID que identifica o arquivo. No S3 esse UUID � utilizado como nome do arquivo.
   *
   * @param fileUUID the new para arquivos persistidos no S3 n�o utilizaremos o nome do arquivo pois precisamos garantir um identificador �nico para que os arquivos n�o se sobreponham
   */
  public void setFileUUID(String fileUUID) {
    this.fileUUID = fileUUID;
  }

  /**
   * # indica se o conte�do do arquivo est� comprimido ou n�o.<Br>
   * Quando comprimido � preciso descoprimir o conte�do do arquivo antes de utiliza-lo.
   *
   * @return the indica se o conte�do do arquivo est� comprimido ou n�o
   */
  public FileCompression getCompression() {
    return compression;
  }

  /**
   * # indica se o conte�do do arquivo est� comprimido ou n�o.<Br>
   * Quando comprimido � preciso descoprimir o conte�do do arquivo antes de utiliza-lo.
   *
   * @param compression the new indica se o conte�do do arquivo est� comprimido ou n�o
   */
  public void setCompression(FileCompression compression) {
    this.compression = compression;
  }

}
